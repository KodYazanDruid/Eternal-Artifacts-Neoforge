package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@RequiredArgsConstructor
public class StemBehavior implements FarmBehavior {
	private final Item seed;
	private final Block stemBlock;
	private final Block fruitBlock;
	
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.is(seed);
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return level.getBlockState(pos).is(fruitBlock);
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		return level.getBlockState(pos).is(fruitBlock);
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		BlockState state = stemBlock.defaultBlockState();
		return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
	}
}
