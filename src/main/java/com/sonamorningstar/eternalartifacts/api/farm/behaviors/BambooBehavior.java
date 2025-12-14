package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.BambooStalkBlock.AGE;

public class BambooBehavior extends ReedBehavior {
	public BambooBehavior() {
		super(() -> Blocks.BAMBOO);
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		FluidState fluidstate = level.getFluidState(pos);
		if (!fluidstate.isEmpty()) {
			return Blocks.AIR.defaultBlockState();
		} else {
			BlockState soilState = level.getBlockState(pos.below());
			if (soilState.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
				if (soilState.is(Blocks.BAMBOO_SAPLING)) {
					return Blocks.BAMBOO.defaultBlockState().setValue(AGE, 0);
				} else if (soilState.is(Blocks.BAMBOO)) {
					return Blocks.BAMBOO.defaultBlockState().setValue(AGE, soilState.getValue(AGE) > 0 ? 1 : 0);
				} else return Blocks.BAMBOO_SAPLING.defaultBlockState();
			} else {
				return Blocks.AIR.defaultBlockState();
			}
		}
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 10;
	}
	
	@Override
	public BlockState getReplantingState(Level level, BlockPos pos, ItemStack seed) {
		BlockState belowState = level.getBlockState(pos.below());
		if (belowState.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
			return Blocks.BAMBOO.defaultBlockState().setValue(AGE, 0);
		}
		return super.getReplantingState(level, pos, seed);
	}
}
