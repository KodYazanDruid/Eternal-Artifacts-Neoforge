package com.sonamorningstar.eternalartifacts.core;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.stream.Stream;

public class ModBlockFamilies {
    private static final Map<Block, BlockFamily> MAP = Maps.newHashMap();

    public static final BlockFamily OBSIDIAN_BRICKS = familyBuilder(ModBlocks.OBSIDIAN_BRICKS.get())
            .wall(ModBlocks.OBSIDIAN_BRICK_WALL.get())
            .stairs(ModBlocks.OBSIDIAN_BRICK_STAIRS.get())
            .slab(ModBlocks.OBSIDIAN_BRICK_SLAB.get())
            .getFamily();

    public static final BlockFamily ICE_BRICKS = familyBuilder(ModBlocks.ICE_BRICKS.get())
            .wall(ModBlocks.ICE_BRICK_WALL.get())
            .stairs(ModBlocks.ICE_BRICK_STAIRS.get())
            .slab(ModBlocks.ICE_BRICK_SLAB.get())
            .dontGenerateModel()
            .getFamily();

    public static final BlockFamily SNOW_BRICKS = familyBuilder(ModBlocks.SNOW_BRICKS.get())
            .wall(ModBlocks.SNOW_BRICK_WALL.get())
            .stairs(ModBlocks.SNOW_BRICK_STAIRS.get())
            .slab(ModBlocks.SNOW_BRICK_SLAB.get())
            .getFamily();

    private static BlockFamily.Builder familyBuilder(Block base) {
        BlockFamily.Builder builder = new BlockFamily.Builder(base);
        BlockFamily blockfamily = MAP.put(base, builder.getFamily());
        if (blockfamily != null) throw new IllegalStateException("Duplicate family definition for " + BuiltInRegistries.BLOCK.getKey(base));
         else return builder;
    }

    public static Stream<BlockFamily> getAllFamilies() {
        return MAP.values().stream();
    }
}
