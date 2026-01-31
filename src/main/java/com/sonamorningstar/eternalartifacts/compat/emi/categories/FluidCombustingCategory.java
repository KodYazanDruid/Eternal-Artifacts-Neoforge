package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidCombustionRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class FluidCombustingCategory extends EAEmiRecipe {
	public static final EmiRecipeCategory FLUID_COMBUSTING_CATEGORY = new EmiRecipeCategory(ModRecipes.FLUID_COMBUSTING.getKey(), EmiStack.of(ModBlocks.FLUID_COMBUSTION_DYNAMO.asItem()));
	private final int generationRate;
	private final int duration;
	public FluidCombustingCategory(FluidCombustionRecipe recipe, ResourceLocation id) {
		super(FLUID_COMBUSTING_CATEGORY, id, 76, 56);
		addFluidIngredient(inputs, recipe.getFuel());
		this.generationRate = recipe.getGeneration();
		this.duration = recipe.getDuration();
	}
	
	@Override
	public void addWidgets(WidgetHolder widgetHolder) {
		EmiIngredient stack = inputs.get(0);
		addLargeTank(widgetHolder, stack, 0, 0, (int) stack.getAmount());
		long amount = (long) generationRate * duration;
		addEnergyBar(widgetHolder, amount, 56, 0, amount).recipeContext(this);
	}
	
	public static void fillRecipes(EmiRegistry registry) {
		for(FluidCombustionRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.FLUID_COMBUSTING.getType()).stream().map(RecipeHolder::value).toList()) {
			ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
			String path;
			if (recipe.getFuel().values[0] instanceof FluidIngredient.TagValue tagValue)
				path = tagValue.tag().location().getPath();
			else
				path = BuiltInRegistries.FLUID.getKey(recipe.getFuel().getFluidStacks()[0].getFluid()).getPath();
			registry.addRecipe(new FluidCombustingCategory(recipe,
					new ResourceLocation(id.getNamespace(), "/fluid_combusting/"+path)));
		}
	}
}
