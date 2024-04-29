package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.data.recipe.ShapedRetexturedRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedRetexturedRecipe>> SHAPED_RETEXTURED_SERIALIZER =
        RECIPES.register("shaped_retextured_recipe", ShapedRetexturedRecipe.Serializer::new);
}
