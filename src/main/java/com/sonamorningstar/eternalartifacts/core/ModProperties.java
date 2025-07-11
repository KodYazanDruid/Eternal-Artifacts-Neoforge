package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModProperties {

    public static class Blocks {
        public static final BlockBehaviour.Properties ORE_BERRY = BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_ORANGE)
                .sound(SoundType.COPPER)
                .pushReaction(PushReaction.DESTROY)
                .randomTicks()
                .noOcclusion()
                .isValidSpawn(ModProperties.Blocks::never)
                .isRedstoneConductor(ModProperties.Blocks::never)
                .isSuffocating(ModProperties.Blocks::never)
                .isViewBlocking(ModProperties.Blocks::never);

        public static final BlockBehaviour.Properties CABLE = BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLUE)
                .sound(SoundType.COPPER)
                .pushReaction(PushReaction.BLOCK)
                .noOcclusion()
                .strength(3.0F, 5.0F)
                .isValidSpawn(ModProperties.Blocks::never)
                .isRedstoneConductor(ModProperties.Blocks::never)
                .isSuffocating(ModProperties.Blocks::never)
                .isViewBlocking(ModProperties.Blocks::never);
        
        public static final BlockBehaviour.Properties TIN_PIPE = BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.BLOCK)
            .noOcclusion()
            .strength(3.0F, 6.0F)
            .isValidSpawn(ModProperties.Blocks::never)
            .isRedstoneConductor(ModProperties.Blocks::never)
            .isSuffocating(ModProperties.Blocks::never)
            .isViewBlocking(ModProperties.Blocks::never);
        public static final BlockBehaviour.Properties COPPER_PIPE = BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .sound(SoundType.COPPER)
            .pushReaction(PushReaction.BLOCK)
            .noOcclusion()
            .strength(3.0F, 6.0F)
            .isValidSpawn(ModProperties.Blocks::never)
            .isRedstoneConductor(ModProperties.Blocks::never)
            .isSuffocating(ModProperties.Blocks::never)
            .isViewBlocking(ModProperties.Blocks::never);
        public static final BlockBehaviour.Properties GOLD_PIPE = BlockBehaviour.Properties.of()
            .mapColor(MapColor.GOLD)
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.BLOCK)
            .instrument(NoteBlockInstrument.BELL)
            .noOcclusion()
            .strength(3.0F, 6.0F)
            .isValidSpawn(ModProperties.Blocks::never)
            .isRedstoneConductor(ModProperties.Blocks::never)
            .isSuffocating(ModProperties.Blocks::never)
            .isViewBlocking(ModProperties.Blocks::never);
        public static final BlockBehaviour.Properties STEEL_PIPE = BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.BLOCK)
            .noOcclusion()
            .strength(5.5F, 7.0F)
            .isValidSpawn(ModProperties.Blocks::never)
            .isRedstoneConductor(ModProperties.Blocks::never)
            .isSuffocating(ModProperties.Blocks::never)
            .isViewBlocking(ModProperties.Blocks::never);

        public static final BlockBehaviour.Properties SNOW_BRICKS = BlockBehaviour.Properties.of()
                .mapColor(MapColor.SNOW)
                .requiresCorrectToolForDrops()
                .strength(0.9F)
                .explosionResistance(2.0F)
                .sound(SoundType.SNOW);

        public static final BlockBehaviour.Properties ICE_BRICKS = BlockBehaviour.Properties.of()
                .mapColor(MapColor.ICE)
                .requiresCorrectToolForDrops()
                .strength(1.2F, 2.5F)
                .sound(SoundType.GLASS)
                .friction(0.98F)
                .noOcclusion()
                .isRedstoneConductor(ModProperties.Blocks::never)
                .isValidSpawn((state, getter, pos, type) -> type == EntityType.POLAR_BEAR);

        public static final BlockBehaviour.Properties FLOWER = BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY);

        public static final BlockBehaviour.Properties MACHINE = BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL);
        public static final BlockBehaviour.Properties SHOCK_ABSORBER = BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_ORANGE)
                .instrument(NoteBlockInstrument.BANJO)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 7200000.0F)
                .sound(SoundType.METAL);
        public static final BlockBehaviour.Properties TESSERACT = BlockBehaviour.Properties
                .ofFullCopy(net.minecraft.world.level.block.Blocks.OBSIDIAN)
                .noOcclusion()
                .strength(4.0F, 3600F);
        
        public static final BlockBehaviour.Properties GRAVEL_ORE = BlockBehaviour.Properties
            .ofFullCopy(net.minecraft.world.level.block.Blocks.GRAVEL).requiresCorrectToolForDrops();
        public static final BlockBehaviour.Properties MOSS_ORE = BlockBehaviour.Properties
            .ofFullCopy(net.minecraft.world.level.block.Blocks.MOSS_BLOCK).requiresCorrectToolForDrops();

        private static Boolean never(BlockState st, BlockGetter lvl, BlockPos pos, EntityType<?> type) {return false;}
        private static Boolean always(BlockState st, BlockGetter lvl, BlockPos pos, EntityType<?> type) {return true;}
        private static boolean never(BlockState st, BlockGetter lvl, BlockPos pos) {return false;}
        private static boolean always(BlockState st, BlockGetter lvl, BlockPos pos) {
            return true;
        }
    }




}
