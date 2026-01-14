package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SweetBerryBehavior implements FarmBehavior {
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.is(Items.SWEET_BERRIES);
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return level.getBlockState(pos).is(Blocks.SWEET_BERRY_BUSH);
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.getValue(SweetBerryBushBlock.AGE) >= SweetBerryBushBlock.MAX_AGE;
	}
	
	@Override
	public List<ItemStack> harvest(Level level, BlockPos pos, @Nullable ItemStack tool, @Nullable Entity harvester) {
		BlockState state = level.getBlockState(pos);
		var drops = Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos),
			harvester, tool == null ? ItemStack.EMPTY : tool);
		FluidState fluidState = level.getFluidState(pos);
		level.setBlockAndUpdate(pos, fluidState.isEmpty() ? Blocks.AIR.defaultBlockState() : fluidState.createLegacyBlock());
		return drops;
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 15;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof SweetBerryBushBlock berry) {
			return berry.getPlant(level, pos);
		}
		return Blocks.AIR.defaultBlockState();
	}
	
	@Override
	public BlockState getReplantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof SweetBerryBushBlock berry) {
			return berry.getPlant(level, pos).setValue(SweetBerryBushBlock.AGE, 1);
		}
		return Blocks.AIR.defaultBlockState();
	}
	
	@Override
	public SoundEvent getReplantSound(BlockState state) {
		return SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES;
	}
}
