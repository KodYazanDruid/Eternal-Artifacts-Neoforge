package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class BlockHelper {

    public static VoxelShape generateByArea(double xLen, double yLen, double zLen, double xOfs, double yOfs, double zOfs) {
        return Block.box(xOfs, yOfs, zOfs, xOfs + xLen, yOfs + yLen, zOfs + zLen);
    }

    public static boolean isBlockStateTag(BlockState state, TagKey<Block> tag) {
        return state.is(tag);
    }

    public static boolean isBlockTag(Level level, BlockPos pos, TagKey<Block> tag) {
        return isBlockStateTag(level.getBlockState(pos), tag);
    }

    public static boolean isLog(Level level, BlockPos pos) {
        return isBlockTag(level, pos, BlockTags.LOGS) || level.getBlockState(pos).is(Blocks.MANGROVE_ROOTS);
    }

    public static boolean isOre(Level level, BlockPos pos) {
        return isBlockTag(level, pos, Tags.Blocks.ORES);
    }

    public static boolean isSame(Level level, BlockPos original, Block comparedTo) {
        return level.getBlockState(original).is(comparedTo);
    }

    public static boolean isLeaves(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockTags.WART_BLOCKS)
                || level.getBlockState(pos).is(BlockTags.LEAVES)
                || level.getBlockState(pos).getBlock().equals(Blocks.SHROOMLIGHT)
                || level.getBlockState(pos).getBlock().equals(Blocks.MOSS_CARPET)
                || (level.getBlockState(pos).getBlock().equals(Blocks.MANGROVE_PROPAGULE) && level.getBlockState(pos).getValue(MangrovePropaguleBlock.HANGING));
    }
    //This gets state from the server. No client interaction.
    public static List<ItemStack> getBlockDrops(ServerLevel level, BlockPos pos, @Nullable ItemStack tool, @Nullable BlockEntity blockEntity, @Nullable ServerPlayer player) {
        BlockState state = level.getBlockState(pos);
        LootParams.Builder lootparams$builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool == null ? ItemStack.EMPTY : tool)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player);
        return state.getDrops(lootparams$builder);
    }
    //State can be passed in. Might cause duplications if used in common code.
    public static List<ItemStack> getBlockDrops(ServerLevel level, BlockState state, BlockPos pos, @Nullable ItemStack tool, @Nullable BlockEntity blockEntity, @Nullable ServerPlayer player) {
        LootParams.Builder lootparams$builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool == null ? ItemStack.EMPTY : tool)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player);
        return state.getDrops(lootparams$builder);
    }

    public static int getFluidTintColor(FluidStack stack) {
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(stack.getFluid());
        return fluidTypeExtensions.getTintColor(stack);
    }
}
