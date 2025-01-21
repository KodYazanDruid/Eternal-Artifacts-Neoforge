package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.SqueezingRecipe;
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

public class SqueezingCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory SQUEEZING_CATEGORY = createCategory(ModRecipes.SQUEEZING, ModMachines.MATERIAL_SQUEEZER);
    public SqueezingCategory(SqueezingRecipe recipe, ResourceLocation id) {
        super(SQUEEZING_CATEGORY, id, 90, 18);
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getOutput()));
        outputs.add(EmiStack.of(recipe.getOutputFluid().getFluid(), recipe.getOutputFluid().getAmount()));
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.addSlot(inputs.get(0), 0, 0);
        widgetHolder.addFillingArrow(24, 0, 10000);
        widgetHolder.addSlot(outputs.get(0), 54, 0).recipeContext(this);
        widgetHolder.addSlot(outputs.get(1).setAmount(outputs.get(1).getAmount()), 72, 0).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(SqueezingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.SQUEEZING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
            String resultPath = BuiltInRegistries.FLUID.getKey(recipe.getOutputFluid().getFluid()).getPath();
            String path;
            if (recipe.getInput().values[0] instanceof Ingredient.TagValue tagValue) path = tagValue.tag().location().getPath();
            else path = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem()).getPath();
            registry.addRecipe(new SqueezingCategory(recipe,
                    new ResourceLocation(id.getNamespace(), "/squeezing/"+path+"_to_"+resultPath)));

        }
    }
}
