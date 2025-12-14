package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CocoaBeansBehavior implements FarmBehavior {
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof CocoaBlock;
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof CocoaBlock;
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		return level.getBlockState(pos).getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 15;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof CocoaBlock cocoa) {
			BlockState blockstate = cocoa.defaultBlockState();
			for(Direction direction : Direction.values()) {
				if (direction.getAxis().isHorizontal()) {
					blockstate = blockstate.setValue(CocoaBlock.FACING, direction);
					if (blockstate.canSurvive(level, pos)) {
						return blockstate;
					}
				}
			}
		}
		return Blocks.AIR.defaultBlockState();
	}
}
