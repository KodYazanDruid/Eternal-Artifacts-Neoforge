package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChorusFlowerBehavior implements FarmBehavior {
	private static final int MIN_HEIGHT_TO_HARVEST = 4;
	
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.is(Items.CHORUS_FLOWER);
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(Blocks.CHORUS_PLANT) || state.is(Blocks.CHORUS_FLOWER);
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		Set<BlockPos> allBlocks = findAllConnectedBlocksAbove(level, pos);
		if (allBlocks.isEmpty()) return false;
		
		int maxY = allBlocks.stream().mapToInt(BlockPos::getY).max().orElse(pos.getY());
		int height = maxY - pos.getY() + 1;
		
		if (height >= MIN_HEIGHT_TO_HARVEST) {
			return true;
		}
		
		boolean hasLivingFlower = false;
		for (BlockPos blockPos : allBlocks) {
			BlockState state = level.getBlockState(blockPos);
			if (state.is(Blocks.CHORUS_FLOWER) && state.getValue(ChorusFlowerBlock.AGE) < 5) {
				hasLivingFlower = true;
				break;
			}
		}
		
		return !hasLivingFlower;
	}
	
	@Override
	public List<ItemStack> harvest(Level level, BlockPos pos, @Nullable ItemStack tool, @Nullable Entity harvester) {
		Set<BlockPos> allBlocks = findAllConnectedBlocksAbove(level, pos);
		
		if (allBlocks.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<ItemStack> allDrops = new ArrayList<>();
		
		List<BlockPos> sortedBlocks = new ArrayList<>(allBlocks);
		sortedBlocks.sort(Comparator.<BlockPos>comparingInt(BlockPos::getY).reversed());
		
		for (BlockPos blockPos : sortedBlocks) {
			BlockState state = level.getBlockState(blockPos);
			if (state.is(Blocks.CHORUS_FLOWER) || state.is(Blocks.CHORUS_PLANT)) {
				var drops = Block.getDrops(state, (ServerLevel) level, blockPos,
					level.getBlockEntity(blockPos), harvester, tool == null ? ItemStack.EMPTY : tool);
				allDrops.addAll(drops);
				level.destroyBlock(blockPos, false);
			}
		}
		
		return allDrops;
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		Set<BlockPos> allBlocks = findAllConnectedBlocksAbove(level, pos);
		if (allBlocks.isEmpty()) return 35;
		return 10 * allBlocks.size();
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
	
	private Set<BlockPos> findAllConnectedBlocksAbove(Level level, BlockPos rootPos) {
		Set<BlockPos> connected = new HashSet<>();
		Deque<BlockPos> queue = new ArrayDeque<>();
		
		BlockState rootState = level.getBlockState(rootPos);
		if (!rootState.is(Blocks.CHORUS_FLOWER) && !rootState.is(Blocks.CHORUS_PLANT)) {
			return connected;
		}
		
		queue.add(rootPos);
		
		while (!queue.isEmpty()) {
			BlockPos current = queue.poll();
			if (connected.contains(current)) continue;
			
			if (current.getY() < rootPos.getY()) continue;
			
			BlockState state = level.getBlockState(current);
			if (!state.is(Blocks.CHORUS_FLOWER) && !state.is(Blocks.CHORUS_PLANT)) continue;
			
			connected.add(current);
			
			for (Direction direction : Direction.values()) {
				if (direction == Direction.DOWN && current.getY() <= rootPos.getY()) continue;
				
				if (state.is(Blocks.CHORUS_PLANT)) {
					BooleanProperty property = PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
					if (property != null && !state.getValue(property)) {
						continue;
					}
				}
				
				BlockPos neighbor = current.relative(direction);
				if (!connected.contains(neighbor)) {
					BlockState neighborState = level.getBlockState(neighbor);
					if (neighborState.is(Blocks.CHORUS_FLOWER) || neighborState.is(Blocks.CHORUS_PLANT)) {
						queue.add(neighbor);
					}
				}
			}
		}
		
		return connected;
	}
}
