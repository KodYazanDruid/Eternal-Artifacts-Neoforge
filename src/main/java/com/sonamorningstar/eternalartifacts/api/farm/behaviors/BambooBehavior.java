package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
	public PlantResult getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		FluidState fluidstate = level.getFluidState(pos);
		if (!fluidstate.isEmpty()) {
			return EMPTY_PLANT;
		} else {
			BlockState soilState = level.getBlockState(pos.below());
			if (soilState.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
				if (soilState.is(Blocks.BAMBOO_SAPLING)) {
					return new PlantResult(Blocks.BAMBOO.defaultBlockState().setValue(AGE, 0), Direction.UP);
				} else if (soilState.is(Blocks.BAMBOO)) {
					return new PlantResult(Blocks.BAMBOO.defaultBlockState().setValue(AGE, soilState.getValue(AGE) > 0 ? 1 : 0), Direction.UP);
				} else return new PlantResult(Blocks.BAMBOO_SAPLING.defaultBlockState(), Direction.UP);
			} else {
				return EMPTY_PLANT;
			}
		}
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 10;
	}
	
	@Override
	public PlantResult getReplantingState(Level level, BlockPos pos, ItemStack seed) {
		BlockState belowState = level.getBlockState(pos.below());
		if (belowState.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
			return new PlantResult(Blocks.BAMBOO.defaultBlockState().setValue(AGE, 0), Direction.UP);
		}
		return super.getReplantingState(level, pos, seed);
	}
}
