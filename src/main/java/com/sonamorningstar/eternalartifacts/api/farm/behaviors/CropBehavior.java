package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CropBehavior implements FarmBehavior {
	
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof CropBlock;
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof CropBlock;
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		CropBlock crop = (CropBlock) level.getBlockState(pos).getBlock();
		return crop.getAge(level.getBlockState(pos)) >= crop.getMaxAge();
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 25;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof CropBlock crop) {
			BlockState state = crop.getPlant(level, pos);
			if (state.canSurvive(level, pos)) return state;
		}
		return Blocks.AIR.defaultBlockState();
	}
}
