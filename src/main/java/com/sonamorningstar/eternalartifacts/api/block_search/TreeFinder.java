package com.sonamorningstar.eternalartifacts.api.block_search;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;
import java.util.stream.Collectors;

public class TreeFinder {
	private static final int MAX_LOGS = 256;
	
	private static final int[][] NEIGHBORS_6 = {
		{ 1, 0, 0}, {-1, 0, 0},
		{ 0, 1, 0}, { 0,-1, 0},
		{ 0, 0, 1}, { 0, 0,-1}
	};
	
	private static final int[][] NEIGHBORS_26;
	static {
		List<int[]> dirs = new ArrayList<>();
		for (int dx = -1; dx <= 1; dx++)
			for (int dy = -1; dy <= 1; dy++)
				for (int dz = -1; dz <= 1; dz++)
					if (dx != 0 || dy != 0 || dz != 0)
						dirs.add(new int[]{dx, dy, dz});
		NEIGHBORS_26 = dirs.toArray(new int[0][]);
	}
	
	public record RootInfo(Set<BlockPos> rootBlocks, BlockPos plantAnchor, boolean is2x2) {}
	
	public record TreeBlocks(Set<BlockPos> logs, Set<BlockPos> leaves, RootInfo root) {
		public boolean isEmpty() { return logs.isEmpty(); }
		public int totalCount() { return logs.size() + leaves.size(); }
	}
	
	public static TreeBlocks find(LevelAccessor level, BlockPos origin) {
		if (!isLog(level.getBlockState(origin))) {
			return new TreeBlocks(Set.of(), Set.of(), null);
		}
		
		Set<BlockPos> logs = new LinkedHashSet<>();
		bfsLogs(level, origin, logs);
		
		Set<BlockPos> leaves = new LinkedHashSet<>();
		collectLeaves(level, logs, leaves);
		
		RootInfo root = computeRoot(logs, origin);
		
		return new TreeBlocks(
			Collections.unmodifiableSet(logs),
			Collections.unmodifiableSet(leaves),
			root
		);
	}
	
	private static void bfsLogs(LevelAccessor level,
								BlockPos origin,
								Set<BlockPos> logs) {
		Deque<BlockPos> queue   = new ArrayDeque<>();
		Set<BlockPos>   visited = new HashSet<>();
		
		enqueue(queue, visited, origin);
		
		while (!queue.isEmpty() && logs.size() < MAX_LOGS) {
			BlockPos pos = queue.poll();
			logs.add(pos);
			
			for (int[] d : NEIGHBORS_26) {
				BlockPos neighbor = pos.offset(d[0], d[1], d[2]);
				if (!visited.contains(neighbor) && isLog(level.getBlockState(neighbor))) {
					enqueue(queue, visited, neighbor);
				}
			}
		}
	}
	
	private static void collectLeaves(LevelAccessor level,
									  Set<BlockPos> logs,
									  Set<BlockPos> leaves) {
		Map<BlockPos, Integer> visited = new HashMap<>();
		Deque<BlockPos>        queue   = new ArrayDeque<>();
		
		for (BlockPos log : logs) {
			for (int[] d : NEIGHBORS_6) {
				BlockPos neighbor = log.offset(d[0], d[1], d[2]);
				if (visited.containsKey(neighbor)) continue;
				
				BlockState state = level.getBlockState(neighbor);
				if (!isNaturalLeaf(state)) continue;
				
				int dist = getDistance(state);
				visited.put(neighbor.immutable(), dist);
				leaves.add(neighbor.immutable());
				queue.add(neighbor.immutable());
			}
		}
		
		while (!queue.isEmpty()) {
			BlockPos current     = queue.poll();
			int      currentDist = visited.get(current);
			
			for (int[] d : NEIGHBORS_6) {
				BlockPos neighbor = current.offset(d[0], d[1], d[2]);
				if (visited.containsKey(neighbor)) continue;
				
				BlockState neighborState = level.getBlockState(neighbor);
				if (!isNaturalLeaf(neighborState)) continue;
				
				int neighborDist = getDistance(neighborState);
				
				if (neighborDist > currentDist) {
					BlockPos immutable = neighbor.immutable();
					visited.put(immutable, neighborDist);
					leaves.add(immutable);
					queue.add(immutable);
				}
			}
		}
	}
	
	private static RootInfo computeRoot(Set<BlockPos> logs, BlockPos origin) {
		int minY = logs.stream().mapToInt(BlockPos::getY).min().orElse(origin.getY());
		
		Set<BlockPos> rootBlocks = logs.stream()
			.filter(p -> p.getY() == minY)
			.collect(Collectors.toCollection(LinkedHashSet::new));
		
		if (rootBlocks.size() >= 4) {
			Optional<BlockPos> corner = find2x2Corner(rootBlocks);
			if (corner.isPresent()) {
				return new RootInfo(
					Collections.unmodifiableSet(rootBlocks),
					corner.get(),
					true
				);
			}
		}
		
		if (rootBlocks.size() == 1) {
			BlockPos single = rootBlocks.iterator().next();
			return new RootInfo(Collections.unmodifiableSet(rootBlocks), single, false);
		}
		
		double avgX = rootBlocks.stream().mapToInt(BlockPos::getX).average().orElse(origin.getX());
		double avgZ = rootBlocks.stream().mapToInt(BlockPos::getZ).average().orElse(origin.getZ());
		BlockPos centroid = new BlockPos((int) Math.round(avgX), minY, (int) Math.round(avgZ));
		
		return new RootInfo(Collections.unmodifiableSet(rootBlocks), centroid, false);
	}
	
	private static Optional<BlockPos> find2x2Corner(Set<BlockPos> rootBlocks) {
		for (BlockPos pos : rootBlocks) {
			if (rootBlocks.contains(pos.east())        &&
				rootBlocks.contains(pos.south())       &&
				rootBlocks.contains(pos.east().south())) {
				return Optional.of(pos);
			}
		}
		return Optional.empty();
	}
	
	private static boolean isLog(BlockState state) {
		return state.is(BlockTags.LOGS);
	}
	
	private static boolean isNaturalLeaf(BlockState state) {
		if (!state.is(BlockTags.LEAVES)) return false;
		
		if (state.hasProperty(LeavesBlock.PERSISTENT)) {
			return !state.getValue(LeavesBlock.PERSISTENT);
		}
		
		return true;
	}
	
	private static int getDistance(BlockState state) {
		if (state.hasProperty(BlockStateProperties.DISTANCE)) {
			return state.getValue(BlockStateProperties.DISTANCE);
		}
		return 1;
	}
	
	private static void enqueue(Deque<BlockPos> queue,
								Set<BlockPos> visited,
								BlockPos pos) {
		BlockPos immutable = pos.immutable();
		if (visited.add(immutable)) {
			queue.add(immutable);
		}
	}
}