package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MaceratingRecipe;
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

public class MaceratingCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory MACERATING_CATEGORY = createCategory(ModRecipes.MACERATING, ModMachines.INDUSTRIAL_MACERATOR);
    public MaceratingCategory(MaceratingRecipe recipe, ResourceLocation id) {
        super(MACERATING_CATEGORY, id, 72, 18);
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addFillingArrow((width/2)-12, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), width-18, 0).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(MaceratingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.MACERATING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
            String resultPath = BuiltInRegistries.ITEM.getKey(recipe.getOutput().getItem()).getPath();
            String path;
            if (recipe.getInput().values[0] instanceof Ingredient.TagValue tagValue) path = tagValue.tag().location().getPath();
            else path = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem()).getPath();
            registry.addRecipe(new MaceratingCategory(recipe,
                    new ResourceLocation(id.getNamespace(), "/macerating/"+path+"_to_"+resultPath)));
        }
    }
}
