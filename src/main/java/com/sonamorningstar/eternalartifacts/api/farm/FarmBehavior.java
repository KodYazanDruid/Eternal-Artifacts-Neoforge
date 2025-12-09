package com.sonamorningstar.eternalartifacts.api.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public interface FarmBehavior extends Comparable<FarmBehavior> {
	boolean isCorrectSeed(ItemStack seedStack);
	
	boolean matches(Level level, BlockPos pos);
	
	boolean canHarvest(Level level, BlockPos pos);
	
	BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed);
	
	default List<ItemStack> harvest(Level level, BlockPos pos, @Nullable ItemStack tool) {
		BlockState state = level.getBlockState(pos);
		var drops = Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos),
			null, tool == null ? ItemStack.EMPTY : tool);
		level.destroyBlock(pos, false);
		return drops;
	}
	
	default boolean supportsReplanting() { return true; }
	
	default int getPriority() { return 100; }
	
	@Override
	default int compareTo(@NotNull FarmBehavior o) {
		return Integer.compare(o.getPriority(), this.getPriority());
	}
	
	default SoundEvent getReplantSound(BlockState state) {
		return state.getSoundType().getPlaceSound();
	}
	
	default BlockState getReplantingState(Level level, BlockPos pos, ItemStack seed) {
		return getPlantingState(level, pos, seed);
	}
}
