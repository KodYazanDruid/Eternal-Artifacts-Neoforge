package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.CompressorRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;

public class CompressingCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory COMPRESSOR_CATEGORY = createCategory(ModRecipes.COMPRESSING, ModMachines.COMPRESSOR);
    public CompressingCategory(CompressorRecipe recipe, ResourceLocation id) {
        super(COMPRESSOR_CATEGORY, id, 72, 18);
        inputs.add(EmiIngredient.of(recipe.getInput().toIngredient()));
        outputs.add(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addFillingArrow(24, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), 54, 0).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(CompressorRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.COMPRESSING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
            String resultPath = BuiltInRegistries.ITEM.getKey(recipe.getOutput().getItem()).getPath();
            String path;
            if (recipe.getInput().values[0] instanceof SizedIngredient.TagValue tagValue) path = tagValue.tag().location().getPath();
            else path = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem()).getPath();
            registry.addRecipe(new CompressingCategory(recipe,
                    new ResourceLocation(id.getNamespace(), "/compressing/"+path+"_to_"+resultPath)));
        }
    }
}
