package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.SolidifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SolidifierCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory SOLIDIFIER_CATEGORY = createCategory(ModRecipes.SOLIDIFYING, ModMachines.SOLIDIFIER);
    public SolidifierCategory(SolidifierRecipe recipe, ResourceLocation id) {
        super(SOLIDIFIER_CATEGORY, id, 72, 50);
        addFluidIngredient(inputs, recipe.getInputFluid());
        outputs.add(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTank(inputs.get(0), 0, 0, 18, 50, 16000);
        widgets.addFillingArrow(24, 16, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), 54, 16).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(SolidifierRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.SOLIDIFYING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
            String resultPath = BuiltInRegistries.ITEM.getKey(recipe.getOutput().getItem()).getPath();
            String path;
            if (recipe.getInputFluid().values[0] instanceof FluidIngredient.TagValue tagValue) path = tagValue.tag().location().getPath();
            else path = BuiltInRegistries.FLUID.getKey(recipe.getInputFluid().getFluidStacks()[0].getFluid()).getPath();
            registry.addRecipe(new SolidifierCategory(recipe,
                    new ResourceLocation(id.getNamespace(), "/solidifying/"+path+"_to_"+resultPath)));
        }
    }
}
