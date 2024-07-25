package com.sonamorningstar.eternalartifacts.data;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemModelGenerators extends net.minecraft.data.models.ItemModelGenerators {
    public ItemModelGenerators(BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        super(output);
    }
}
