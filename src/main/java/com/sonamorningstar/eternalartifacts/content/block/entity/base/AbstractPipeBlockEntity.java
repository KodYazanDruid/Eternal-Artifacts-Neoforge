package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import java.util.*;

public abstract class AbstractPipeBlockEntity<CAP> extends ModBlockEntity implements TickableServer {
	//This is for this pipe's surroundings.
	public final LinkedHashSet<BlockPos> pipes = new LinkedHashSet<>();
	public final Map<BlockPos, BlockCapabilityCache<CAP, Direction>> sources = new LinkedHashMap<>();
	public final Map<BlockPos, BlockCapabilityCache<CAP, Direction>> targets = new LinkedHashMap<>();
	public EnumMap<Direction, Boolean> manuallyDisabled = Util.make(new EnumMap<>(Direction.class), map -> {
		for (Direction dir : Direction.values()) {
			map.put(dir, false);
		}
	});
	
	//This is for the entire network.
	public final LinkedHashSet<BlockPos> networkPipes = new LinkedHashSet<>();
	public final Map<BlockPos, BlockCapabilityCache<CAP, Direction>> allSources = new LinkedHashMap<>();
	public final Map<BlockPos, BlockCapabilityCache<CAP, Direction>> allTargets = new LinkedHashMap<>();
	public final Map<BlockPos, Map<BlockPos, Integer>> sourceToTargetDistances = new HashMap<>();
	
	
	public boolean isDirty = false;
	private boolean updateNetwork = false;
	protected boolean isUpdatingConnections = false;
	
	@Getter
	private final Class<CAP> capabilityClass;
	
	public AbstractPipeBlockEntity(BlockEntityType<?> type, Class<CAP> cls, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.capabilityClass = cls;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		if (level != null && !level.isClientSide()) isDirty = true;
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		ListTag manuallyDisabledList = new ListTag();
		for (Map.Entry<Direction, Boolean> entry : manuallyDisabled.entrySet()) {
			if (entry.getValue()) {
				CompoundTag entryTag = new CompoundTag();
				entryTag.putString("Direction", entry.getKey().getName());
				manuallyDisabledList.add(entryTag);
			}
		}
		tag.put("ManuallyDisabled", manuallyDisabledList);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		ListTag manuallyDisabledList = tag.getList("ManuallyDisabled", 10);
		for (int i = 0; i < manuallyDisabledList.size(); i++) {
			CompoundTag entryTag = manuallyDisabledList.getCompound(i);
			String dirName = entryTag.getString("Direction");
			Direction dir = Direction.byName(dirName);
			if (dir != null) {
				manuallyDisabled.put(dir, true);
			}
		}
	}
	
	public void openMenu(Player player, Direction dir) {}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		if (isDirty) {
			updateConnections(lvl);
			isDirty = false;
		}
		
		if (updateNetwork) {
			updateNetwork = false;
			collectNetworkDevicesWithDistances(lvl);
		}
		
		if (sources.isEmpty() || allTargets.isEmpty() || isUpdatingConnections) return;
		
		doTransfer(Map.copyOf(sources), Map.copyOf(allTargets));
	}
	
	protected boolean shouldPipesConnect(BlockState neighborState, Direction direction) {
		return !manuallyDisabled.get(direction);
	}
	
	protected abstract BlockCapabilityCache<CAP, Direction> createCache(BlockPos pos, Direction dir);
	
	protected abstract boolean fillSourcesAndTargets(Map<BlockPos, BlockCapabilityCache<CAP, Direction>> sources,
									 Map<BlockPos, BlockCapabilityCache<CAP, Direction>> targets,
								     BlockCapabilityCache<CAP, Direction> cache,
									 BlockPos pos,
									 Direction dir);
	
	public void updateConnections(Level lvl) {
		if (lvl.isClientSide() || isUpdatingConnections) return;
		
		try {
			isUpdatingConnections = true;
			
			sources.clear();
			targets.clear();
			pipes.clear();
			
			for (Direction dir : Direction.values()) {
				BlockPos offset = getBlockPos().relative(dir);
				BlockState neighborState = lvl.getBlockState(offset);
				BlockState state = getBlockState();
				
				if (shouldPipesConnect(neighborState, dir)) {
					pipes.add(offset);
					updatePipeConnections(lvl, state, dir, true);
					continue;
				}
				
				BlockCapabilityCache<CAP, Direction> cache = createCache(offset, dir);
				
				CAP storage = cache.getCapability();
				boolean canConnect = false;
				if (storage != null) {
					canConnect = fillSourcesAndTargets(sources, targets, cache, offset, dir);
				}
				
				updatePipeConnections(lvl, state, dir, canConnect);
			}
		} finally {
			isUpdatingConnections = false;
			updateNetwork = true;
		}
	}
	
	protected void updatePipeConnections(Level lvl, BlockState state, Direction dir, boolean canConnect) {
		if (lvl.isAreaLoaded(getBlockPos(), 1) && state.getBlock() instanceof CableBlock) {
			lvl.setBlockAndUpdate(getBlockPos(), state.setValue(CableBlock.PROPERTY_BY_DIRECTION.get(dir), canConnect));
		}
	}
	
	protected abstract int getMaxRange();
	
	protected void collectNetworkDevicesWithDistances(Level lvl) {
		networkPipes.clear();
		allSources.clear();
		allTargets.clear();
		sourceToTargetDistances.clear();
		
		Queue<BlockPos> queue = new LinkedList<>();
		Map<BlockPos, AbstractPipeBlockEntity<CAP>> pipeEntities = new HashMap<>();
		
		queue.add(getBlockPos());
		networkPipes.add(getBlockPos());
		pipeEntities.put(getBlockPos(), this);
		
		while (!queue.isEmpty()) {
			BlockPos currentPos = queue.poll();
			AbstractPipeBlockEntity<CAP> currentPipe = pipeEntities.get(currentPos);
			
			allSources.putAll(currentPipe.sources);
			allTargets.putAll(currentPipe.targets);
			
			for (BlockPos pipePos : currentPipe.pipes) {
				if (networkPipes.contains(pipePos)) continue;
				
				BlockEntity be = lvl.getBlockEntity(pipePos);
				if (!(be instanceof AbstractPipeBlockEntity<?> pipe) ||
					!Objects.equals(pipe.getCapabilityClass(), capabilityClass)) continue;
				
				@SuppressWarnings("unchecked")
				AbstractPipeBlockEntity<CAP> typedPipe = (AbstractPipeBlockEntity<CAP>) pipe;
				
				networkPipes.add(pipePos);
				pipeEntities.put(pipePos, typedPipe);
				queue.add(pipePos);
				
				typedPipe.updateNetwork = true;
			}
		}
		
		calculateDistances(pipeEntities);
	}
	
	private void calculateDistances(Map<BlockPos, AbstractPipeBlockEntity<CAP>> pipeEntities) {
		for (BlockPos sourcePos : allSources.keySet()) {
			Map<BlockPos, Integer> distancesToTargets = new HashMap<>();
			sourceToTargetDistances.put(sourcePos, distancesToTargets);
			
			Queue<BlockPos> queue = new LinkedList<>();
			Set<BlockPos> visited = new HashSet<>();
			Map<BlockPos, Integer> distances = new HashMap<>();
			
			BlockPos startPos = findNearestPipe(sourcePos, pipeEntities);
			if (startPos == null) continue;
			
			queue.add(startPos);
			visited.add(startPos);
			distances.put(startPos, 0);
			
			while (!queue.isEmpty()) {
				BlockPos currentPos = queue.poll();
				int currentDistance = distances.get(currentPos);
				
				if (currentDistance >= getMaxRange()) continue;
				
				for (Map.Entry<BlockPos, BlockCapabilityCache<CAP, Direction>> entry : allTargets.entrySet()) {
					BlockPos targetPos = entry.getKey();
					if (isAdjacent(currentPos, targetPos)) {
						distancesToTargets.put(targetPos, currentDistance + 1);
					}
				}
				
				AbstractPipeBlockEntity<CAP> currentPipe = pipeEntities.get(currentPos);
				if (currentPipe == null) continue;
				
				for (BlockPos neighborPos : currentPipe.pipes) {
					if (!visited.contains(neighborPos) && networkPipes.contains(neighborPos)) {
						queue.add(neighborPos);
						visited.add(neighborPos);
						distances.put(neighborPos, currentDistance + 1);
					}
				}
			}
		}
	}
	
	private BlockPos findNearestPipe(BlockPos pos, Map<BlockPos, AbstractPipeBlockEntity<CAP>> pipeEntities) {
		if (pipeEntities.containsKey(pos)) {
			return pos;
		}
		
		for (Direction dir : Direction.values()) {
			BlockPos neighborPos = pos.relative(dir);
			if (pipeEntities.containsKey(neighborPos)) {
				return neighborPos;
			}
		}
		
		return null;
	}
	
	protected boolean isAdjacent(BlockPos pos1, BlockPos pos2) {
		for (Direction dir : Direction.values()) {
			if (pos1.relative(dir).equals(pos2)) {
				return true;
			}
		}
		return false;
	}
	
	protected abstract void doTransfer(Map<BlockPos, BlockCapabilityCache<CAP, Direction>> sources,
									   Map<BlockPos, BlockCapabilityCache<CAP, Direction>> targets);
	
}
