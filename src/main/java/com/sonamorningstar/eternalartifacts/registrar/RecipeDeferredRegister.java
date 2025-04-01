package com.sonamorningstar.eternalartifacts.registrar;

import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class RecipeDeferredRegister {
    private final String namespace;
    @Getter
    private final DeferredRegister<RecipeType<?>> recipeType;
    private final DeferredRegister<RecipeSerializer<?>> recipeSerializer;
    private final List<RecipeDeferredHolder<? extends Container, ? extends Recipe<?>>> recipes = new ArrayList<>();

    public RecipeDeferredRegister(String modid) {
        this.namespace = modid;
        this.recipeType = DeferredRegister.create(Registries.RECIPE_TYPE, modid);
        this.recipeSerializer = DeferredRegister.create(Registries.RECIPE_SERIALIZER, modid);
    }

    public void register(IEventBus bus) {
        this.recipeType.register(bus);
        this.recipeSerializer.register(bus);
    }

    public
    <C extends Container, R extends Recipe<C>, S extends RecipeSerializer<R>> RecipeDeferredHolder<C, R> register(String name, Supplier<S> serializerSup) {
        DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> serializerHolder = recipeSerializer.register(name,  serializerSup);
        DeferredHolder<RecipeType<?>, RecipeType<R>> typeHolder = recipeType.register(name, ()-> RecipeType.simple(new ResourceLocation(namespace, name)));
        RecipeDeferredHolder<C, R> holder = new RecipeDeferredHolder<>(serializerHolder, typeHolder);
        recipes.add(holder);
        return holder;
    }

    public <C extends Container, R extends Recipe<C>, S extends RecipeSerializer<R>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>>
    registerSerializer(String name, Supplier<S> serializerSup) {
        return recipeSerializer.register(name,  serializerSup);
    }

    public List<RecipeDeferredHolder<? extends Container, ? extends Recipe<?>>> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }
}
