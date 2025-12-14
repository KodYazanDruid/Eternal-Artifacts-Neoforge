package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ChorusFlowerBehavior implements FarmBehavior {
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.is(Items.CHORUS_FLOWER);
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		return false;
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 35;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof ChorusFlowerBlock flower) {
			BlockState state = flower.defaultBlockState();
			if (state.canSurvive(level, pos)) return state;
		}
		return Blocks.AIR.defaultBlockState();
	}
}
