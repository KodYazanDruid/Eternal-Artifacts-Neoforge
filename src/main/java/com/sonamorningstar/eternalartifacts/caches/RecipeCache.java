package com.sonamorningstar.eternalartifacts.caches;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

@Getter
public class RecipeCache<R extends Recipe<C>, C extends Container> {

    private R recipe;

    public void findRecipe(RecipeType<R> recipeType, C container, Level level) {
        if(level == null) return;

        List<R> recipeList = level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
        for(R r : recipeList) {
            if(r.matches(container, level)) {
                recipe = r;
                return;
            }
        }
        clearCache();
    }

    public void clearCache() {
        recipe = null;
    }
}
