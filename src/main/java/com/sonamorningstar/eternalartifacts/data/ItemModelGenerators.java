package com.sonamorningstar.eternalartifacts.data;

import com.google.gson.JsonElement;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModModelTemplates;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemModelGenerators extends net.minecraft.data.models.ItemModelGenerators {
    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> output;

    public ItemModelGenerators(BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        super(output);
        this.output = output;
    }

    @Override
    public void run() {
        createBEWLRTransforms(ModBlocks.JAR);
        createBEWLRTransforms(ModBlocks.NOUS_TANK);
        createBEWLRTransforms(ModBlocks.FLUID_COMBUSTION_DYNAMO);
        createBEWLRTransforms(ModBlocks.OIL_REFINERY);
    }

    private void createBEWLRTransforms(DeferredBlock<?> holder) {
        ModModelTemplates.ENTITY_RENDER_TRANSFORMS.create(ModelLocationUtils.getModelLocation(holder.asItem()), TextureMapping.defaultTexture(holder.get()), output);
    }
}
