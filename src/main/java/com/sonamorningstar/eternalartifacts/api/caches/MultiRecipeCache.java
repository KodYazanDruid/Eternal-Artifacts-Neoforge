package com.sonamorningstar.eternalartifacts.api.caches;

import lombok.Getter;
import net.minecraft.world.item.crafting.Recipe;

@Getter
public class MultiRecipeCache {

    /*public void findRecipes(C container, Level level, List<RecipeType<Recipe<C>>> recipeTypes) {
        for (RecipeType<Recipe<C>> recipeType : recipeTypes) {
            findRecipe(recipeType, container, level);
            if (recipe != null) break;
        }
    }*/
    private Recipe<?> recipe;

    /*public *//*<R extends Recipe<Container>*//**//*, C extends Container*//**//*>*//* void findRecipes(Container container, Level level, List<RecipeType<? extends Recipe<Container>>> recipeTypes) {
        for (RecipeType<? extends Recipe<Container>> recipeType : recipeTypes) {
            RecipeCache<? extends Recipe<Container>, Container> recipeCache = new RecipeCache<>();
            recipeCache.findRecipe(recipeType, container, level);
            if (recipeCache.getRecipe() != null) {
                this.recipe = recipeCache.getRecipe();
                break;
            }
        }
    }*/
}
