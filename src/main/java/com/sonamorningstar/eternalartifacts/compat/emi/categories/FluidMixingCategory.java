package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidMixingRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidMixingCategory extends EAEmiRecipe {
	public static final EmiRecipeCategory FLUID_MIXING_CATEGORY = createCategory(ModRecipes.FLUID_MIXING, ModMachines.FLUID_MIXER);
	private final int fluid1Amount;
	private final int fluid2Amount;
	private final int outputAmount;
	private final int maximumAmount;
	
	public FluidMixingCategory(FluidMixingRecipe recipe, ResourceLocation id) {
		super(FLUID_MIXING_CATEGORY, id, 116, 56);
		if (!recipe.fluidInput1().isEmpty()) {
			addFluidIngredient(inputs, recipe.fluidInput1());
			this.fluid1Amount = recipe.fluidInput1().getFluidStacks()[0].getAmount();
		} else {
			inputs.add(EmiStack.EMPTY);
			fluid1Amount = 0;
		}
		inputs.add(EmiIngredient.of(recipe.itemInput().toIngredient()));
		if (!recipe.fluidInput2().isEmpty()) {
			addFluidIngredient(inputs, recipe.fluidInput2());
			this.fluid2Amount = recipe.fluidInput2().getFluidStacks()[0].getAmount();
		} else {
			inputs.add(EmiStack.EMPTY);
			fluid2Amount = 0;
		}
		FluidStack output = recipe.output();
		this.outputAmount = output.getAmount();
		outputs.add(EmiStack.of(output.getFluid(), outputAmount));
		maximumAmount = Math.max(Math.max(fluid1Amount, fluid2Amount), outputAmount);
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		if (!inputs.get(0).isEmpty()) addLargeTank(widgets, inputs.get(0), 0, 0, maximumAmount);
		widgets.addSlot(inputs.get(1), 22, 19);
		if (!inputs.get(2).isEmpty()) addLargeTank(widgets, inputs.get(2), 44, 0, maximumAmount);
		widgets.addFillingArrow(66, 19, 10000);
		addLargeTank(widgets, outputs.get(0), 98, 0, maximumAmount).recipeContext(this);
	}
	
	public static void fillRecipes(EmiRegistry registry) {
		for(FluidMixingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.FLUID_MIXING.getType()).stream().map(RecipeHolder::value).toList()) {
			ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
			String resultPath = BuiltInRegistries.FLUID.getKey(recipe.output().getFluid()).getPath();
			String input1 = "empty_fluid";
			if (!recipe.fluidInput1().isEmpty()){
				if (recipe.fluidInput1().getValues()[0] instanceof FluidIngredient.TagValue tagValue)
					input1 = tagValue.tag().location().getPath();
				else
					input1 = BuiltInRegistries.FLUID.getKey(recipe.fluidInput1().getFluidStacks()[0].getFluid()).getPath();
			}
			String input2 = "empty_fluid";
			if (!recipe.fluidInput2().isEmpty()){
				if (recipe.fluidInput2().getValues()[0] instanceof FluidIngredient.TagValue tagValue)
					input2 = tagValue.tag().location().getPath();
				else
					input2 = BuiltInRegistries.FLUID.getKey(recipe.fluidInput2().getFluidStacks()[0].getFluid()).getPath();
			}
			String itemInput = "empty_item";
			if (!recipe.itemInput().isEmpty()){
				if (recipe.itemInput().getValues()[0] instanceof SizedIngredient.TagValue tagValue)
					itemInput = tagValue.tag().location().getPath();
				else itemInput = BuiltInRegistries.ITEM.getKey(recipe.itemInput().getItems()[0].getItem()).getPath();
			}
			registry.addRecipe(new FluidMixingCategory(recipe,
				new ResourceLocation(id.getNamespace(), "/" + id.getPath() + "/" +input1+"_and_"+input2+"_with_"+itemInput+"_to_"+resultPath)));
		}
	}
}
