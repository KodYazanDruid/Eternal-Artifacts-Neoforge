package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KelpBehavior implements FarmBehavior {
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof KelpBlock;
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof KelpPlantBlock;
	}
	
	@Override
	public List<ItemStack> harvest(Level level, BlockPos pos, @Nullable ItemStack tool) {
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
						level.getBlockEntity(pos), null,
						tool == null ? ItemStack.EMPTY : tool));
					level.destroyBlock(targetPos, false);
				}
			}
		}
		return drops;
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
	
	private boolean isCorrectState(BlockState state) {
		return state.getBlock() instanceof KelpPlantBlock || state.getBlock() instanceof KelpBlock;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof GrowingPlantBlock growing) {
			var state = growing.getStateForPlacement(level);
			if (state.canSurvive(level, pos)) return state;
		}
		return Blocks.AIR.defaultBlockState();
	}
}
