package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MeatShredderRecipe>> MEAT_SHREDDING_SERIALIZER =
            RECIPE_SERIALIZERS.register("meat_shredding", MeatShredderRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MeatShredderRecipe>> MEAT_SHREDDING_TYPE =
            RECIPE_TYPES.register("meat_shredding", ()-> RecipeType.simple(new ResourceLocation(MODID, "meat_shredding")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MobLiquifierRecipe>> MOB_LIQUIFIER_SERIALIZER =
            RECIPE_SERIALIZERS.register("mob_liquifying", MobLiquifierRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MobLiquifierRecipe>> MOB_LIQUIFIER_TYPE =
            RECIPE_TYPES.register("mob_liquifying", ()-> RecipeType.simple(new ResourceLocation(MODID, "mob_liquifying")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidCombustionRecipe>> FLUID_COMBUSTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("fluid_combusting", FluidCombustionRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidCombustionRecipe>> FLUID_COMBUSTING_TYPE =
            RECIPE_TYPES.register("fluid_combusting", ()-> RecipeType.simple(new ResourceLocation(MODID, "fluid_combusting")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<OilRefineryRecipe>> OIL_REFINERY_SERIALIZER =
            RECIPE_SERIALIZERS.register("oil_refining", OilRefineryRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<OilRefineryRecipe>> OIL_REFINERY_TYPE =
            RECIPE_TYPES.register("oil_refining", ()-> RecipeType.simple(new ResourceLocation(MODID, "oil_refining")));




}
