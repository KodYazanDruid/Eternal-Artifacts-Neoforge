package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidInfuserRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
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

public class FluidInfuserCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory FLUID_INFUSER_CATEGORY = createCategory(ModRecipes.FLUID_INFUSING, ModMachines.FLUID_INFUSER);
    public FluidInfuserCategory(FluidInfuserRecipe recipe, ResourceLocation id) {
        super(FLUID_INFUSER_CATEGORY, id, 90, 18);
        FluidIngredient inputFluid = recipe.getInputFluid();
        for (FluidIngredient.Value value : inputFluid.getValues()) {
            if (value instanceof FluidIngredient.TagValue tagValue)
                inputs.add(EmiIngredient.of(tagValue.tag(), tagValue.amount()));
            else if (value instanceof FluidIngredient.FluidValue fluidValue) {
                FluidStack stack = fluidValue.fluidStack();
                inputs.add(EmiStack.of(stack.getFluid(), stack.getAmount()));
            }
        }
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addSlot(inputs.get(1), 18, 0);
        widgets.addFillingArrow(42, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), 72, 0).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(FluidInfuserRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.FLUID_INFUSING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
            String resultPath = BuiltInRegistries.ITEM.getKey(recipe.getOutput().getItem()).getPath();
            String path;
            if (recipe.getInput().values[0] instanceof Ingredient.TagValue tagValue) path = tagValue.tag().location().getPath();
            else path = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem()).getPath();
            String fluidPath;
            if (recipe.getInputFluid().getValues()[0] instanceof FluidIngredient.TagValue tagValue) fluidPath = tagValue.tag().location().getPath();
            else fluidPath = BuiltInRegistries.FLUID.getKey(recipe.getInputFluid().getFluidStacks()[0].getFluid()).getPath();
            registry.addRecipe(new FluidInfuserCategory(recipe,
                    new ResourceLocation(id.getNamespace(), "/fluid_infusing/"+path+"_and_"+fluidPath+"_to_"+resultPath)));
        }
    }
}
