package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PitcherPlantBehavior implements FarmBehavior {
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.is(Items.PITCHER_POD);
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return level.getBlockState(pos).is(Blocks.PITCHER_CROP);
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.getValue(PitcherCropBlock.AGE) >= PitcherCropBlock.MAX_AGE;
	}
	
	@Override
	public boolean supportsReplanting() {
		return false;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof PitcherCropBlock flower) {
			BlockState state = flower.getPlant(level, pos);
			if (state.canSurvive(level, pos)) return state;
		}
		return Blocks.AIR.defaultBlockState();
	}
}
