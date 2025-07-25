package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
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

public class MeatShredderCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory MEAT_SHREDDER_CATEGORY = createCategory(ModRecipes.MEAT_SHREDDING, ModMachines.MEAT_SHREDDER);
    public MeatShredderCategory(MeatShredderRecipe recipe, ResourceLocation id) {
        super(MEAT_SHREDDER_CATEGORY, id, 72, 50);
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getOutput().getFluid(), recipe.getOutput().getAmount()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 16);
        widgets.addFillingArrow(24, 16, 10000);
        widgets.addTank(outputs.get(0).setAmount(outputs.get(0).getAmount()), 54, 0, 18, 50, 16000).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(MeatShredderRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.MEAT_SHREDDING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
            String path;
            if (recipe.getInput().values[0] instanceof Ingredient.TagValue tagValue) path = tagValue.tag().location().getPath();
            else path = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem()).getPath();
            registry.addRecipe(new MeatShredderCategory(recipe, new ResourceLocation(id.getNamespace(), "/meat_shredding/"+path)));
        }
    }
}
