package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IPlantable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ReedBehavior implements FarmBehavior {
	private final Block reed;
	
	public ReedBehavior(Supplier<Block> reedBlock) {
		this.reed = reedBlock.get();
	}
	
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.getItem() instanceof BlockItem bi && reed.getClass().isInstance(bi.getBlock());
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return isCorrectState(level.getBlockState(pos));
	}
	
	@Override
	public List<ItemStack> harvest(Level level, BlockPos pos, @Nullable ItemStack tool, @Nullable Entity harvester) {
		List<ItemStack> drops = new ArrayList<>();
		BlockState state = level.getBlockState(pos);
		int height = 0;
		int maxY = pos.getY();
		while (!state.isAir() && isCorrectState(state)) {
			height++;
			state = level.getBlockState(pos.above(height));
			maxY = pos.getY() + height;
		}
		if (height >= 2) {
			for (int y = pos.getY(); y <= maxY; y++) {
				BlockPos targetPos = new BlockPos(pos.getX(), y, pos.getZ());
				BlockState targetState = level.getBlockState(targetPos);
				if (isCorrectState(targetState)) {
					drops.addAll(Block.getDrops(targetState, (ServerLevel) level, targetPos,
						level.getBlockEntity(pos), harvester,
						tool == null ? ItemStack.EMPTY : tool));
					level.destroyBlock(targetPos, false);
				}
			}
		}
		return drops;
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 25;
	}
	
	private boolean isCorrectState(BlockState state) {
		return state.is(reed);
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		int height = 0;
		while (!state.isAir() && isCorrectState(state)) {
			height++;
			state = level.getBlockState(pos.above(height));
		}
		return height >= 2;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block == Blocks.AIR) return Blocks.AIR.defaultBlockState();
		BlockState state = block instanceof IPlantable plantable ? plantable.getPlant(level, pos) : reed.defaultBlockState();
		return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
	}
}
