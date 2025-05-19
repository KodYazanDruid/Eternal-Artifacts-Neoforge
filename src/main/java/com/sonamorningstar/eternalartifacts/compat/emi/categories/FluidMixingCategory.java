package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidMixingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidMixingCategory extends EAEmiRecipe {
	public static final EmiRecipeCategory FLUID_MIXING_CATEGORY = createCategory(ModRecipes.FLUID_MIXING, ModMachines.FLUID_MIXER);
	public FluidMixingCategory(FluidMixingRecipe recipe, ResourceLocation id) {
		super(FLUID_MIXING_CATEGORY, id, 112, 50);
		addFluidIngredient(inputs, recipe.fluidInput1());
		inputs.add(EmiIngredient.of(recipe.itemInput().toIngredient()));
		addFluidIngredient(inputs, recipe.fluidInput2());
		FluidStack output = recipe.output();
		outputs.add(EmiStack.of(output.getFluid(), output.getAmount()));
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		if (!inputs.isEmpty()) widgets.addTank(inputs.get(0), 0, 0, 18, 50, 16000);
		if (inputs.size() > 1) widgets.addSlot(inputs.get(1), 20, 16);
		if (inputs.size() > 2) widgets.addTank(inputs.get(2), 40, 0, 18, 50, 16000);
		widgets.addFillingArrow(62, 16, 10000);
		widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()),94,16).recipeContext(this);
	}
	
	public static void fillRecipes(EmiRegistry registry) {
		for(FluidMixingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.FLUID_MIXING.getType()).stream().map(RecipeHolder::value).toList()) {
			ResourceLocation id = registry.getRecipeManager().recipes.get(recipe.getType())
				.values().stream()
				.filter(holder ->  holder.value().equals(recipe)).findFirst().map(RecipeHolder::id).orElse(null);
			
			registry.addRecipe(new FluidMixingCategory(recipe, new ResourceLocation(id.getNamespace(), "/" + id.getPath())));
		}
	}
}
