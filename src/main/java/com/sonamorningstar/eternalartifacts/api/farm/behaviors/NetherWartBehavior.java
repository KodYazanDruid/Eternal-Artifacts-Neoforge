package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NetherWartBehavior implements FarmBehavior {
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof NetherWartBlock;
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof NetherWartBlock;
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		return level.getBlockState(pos).getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 25;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof NetherWartBlock wartBlock) {
			BlockState state = wartBlock.getPlant(level, pos);
			if (state.canSurvive(level, pos)) return state;
		}
		return Blocks.AIR.defaultBlockState();
	}
}
