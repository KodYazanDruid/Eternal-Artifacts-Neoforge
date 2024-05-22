package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.data.recipe.MeatPackerRecipe;
import com.sonamorningstar.eternalartifacts.data.recipe.ShapedRetexturedRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedRetexturedRecipe>> SHAPED_RETEXTURED_SERIALIZER =
        RECIPE_SERIALIZERS.register("shaped_retextured_recipe", ShapedRetexturedRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MeatPackerRecipe>> MEAT_PACKER_TYPE =
        RECIPE_TYPES.register("meat_packer", () -> MeatPackerRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MeatPackerRecipe>> MEAT_PACKER_SERIALIZER =
        RECIPE_SERIALIZERS.register("meat_packer", MeatPackerRecipe.Serializer::new);
}
