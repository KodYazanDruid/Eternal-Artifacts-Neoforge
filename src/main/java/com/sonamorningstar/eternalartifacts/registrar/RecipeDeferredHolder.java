package com.sonamorningstar.eternalartifacts.registrar;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

@RequiredArgsConstructor
public class RecipeDeferredHolder<C extends Container, R extends Recipe<C>> {
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> serializer;
    private final DeferredHolder<RecipeType<?>, RecipeType<R>> type;

    public DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> getSerializerHolder() {return this.serializer;}
    public RecipeSerializer<R> getSerializer() {return this.getSerializerHolder().get();}
    public DeferredHolder<RecipeType<?>, RecipeType<R>> getTypeHolder() {return this.type;}
    public RecipeType<R> getType() {return this.getTypeHolder().get();}

    public ResourceLocation getKey() {
        return BuiltInRegistries.RECIPE_TYPE.getKey(getType());
    }

    public ResourceLocation getSerializerKey() {
        return BuiltInRegistries.RECIPE_SERIALIZER.getKey(getSerializer());
    }
}
