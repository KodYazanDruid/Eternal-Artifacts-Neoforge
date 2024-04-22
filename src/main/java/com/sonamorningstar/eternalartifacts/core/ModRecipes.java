package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.GardeningPotBlockItem;
import com.sonamorningstar.eternalartifacts.data.ShapedRetexturedRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModRecipes {
    public static DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    /*public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedRetexturedRecipe>> SHAPED_RETEXTURED_SERIALIZER =
        RECIPES.register("shaped_retextured_recipe", () -> ShapedRetexturedRecipe.SERIALIZER);*/

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedRetexturedRecipe>> GARDENING_POT_RETEXTURED_SERIALIZER =
        RECIPES.register("shaped_retextured_recipe", ()-> ShapedRetexturedRecipe.getSerializer(ModItems.GARDENING_POT.get()));
}
