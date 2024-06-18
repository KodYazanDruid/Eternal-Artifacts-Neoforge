package com.sonamorningstar.eternalartifacts.data;

import com.google.gson.JsonElement;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockModelGenerators extends net.minecraft.data.models.BlockModelGenerators {
    public BlockModelGenerators(Consumer<BlockStateGenerator> stateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, Consumer<Item> skipped) {
        super(stateOutput, modelOutput, skipped);
    }

    @Override
    public void createGenericCube(Block pBlock) {
        super.createGenericCube(pBlock);
    }
}
