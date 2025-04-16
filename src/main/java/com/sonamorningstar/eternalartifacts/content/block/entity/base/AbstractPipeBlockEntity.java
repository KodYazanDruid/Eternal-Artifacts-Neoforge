package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractPipeBlockEntity<CAP> extends ModBlockEntity implements ITickableServer {
	//This is for this pipe's surroundings.
	public final LinkedHashSet<BlockPos> pipes = new LinkedHashSet<>();
	public final Map<BlockPos, BlockCapabilityCache<CAP, Direction>> sources = new LinkedHashMap<>();
	public final Map<BlockPos, BlockCapabilityCache<CAP, Direction>> targets = new LinkedHashMap<>();
	
	//This is for the entire network.
	public final LinkedHashSet<BlockPos> networkPipes = new LinkedHashSet<>();
	public final Map<BlockPos, BlockCapabilityCache<CAP, Direction>> allSources = new LinkedHashMap<>();
	public final Map<BlockPos, BlockCapabilityCache<CAP, Direction>> allTargets = new LinkedHashMap<>();
	
	public boolean isDirty = false;
	private boolean updateAllPairs = false;
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
	
	public void openMenu(ServerPlayer player, Direction dir) {
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		if (isDirty) {
			updateConnections(lvl);
			isDirty = false;
		}
		
		if (updateAllPairs) {
			updateAllPairs = false;
			networkPipes.clear();
			allSources.clear();
			allTargets.clear();
			collectNetworkDevices(networkPipes, allSources, allTargets, lvl);
		}
		
		if (allSources.isEmpty() || allTargets.isEmpty()) return;
		
		if (!isUpdatingConnections) {
			doTransfer(new LinkedHashMap<>(allSources), new LinkedHashMap<>(allTargets));
		}
	}
	
	/**
	 * Checks if the pipe should connect to the given block.
	 * @param neighborState the state of the block to check
	 * @return {@code true} if the pipe should connect, {@code false} otherwise
	 */
	protected abstract boolean shouldPipesConnect(BlockState neighborState);
	
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
				
				if (shouldPipesConnect(neighborState)) {
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
			updateAllPairs = true;
		}
	}
	
	protected void updatePipeConnections(Level lvl, BlockState state, Direction dir, boolean canConnect) {
		if (lvl.isAreaLoaded(getBlockPos(), 1) && state.getBlock() instanceof CableBlock) {
			lvl.setBlockAndUpdate(getBlockPos(), state.setValue(CableBlock.PROPERTY_BY_DIRECTION.get(dir), canConnect));
		}
	}
	
	protected abstract int getMaxConnections();
	
	private void collectNetworkDevices(LinkedHashSet<BlockPos> visitedCables,
									   Map<BlockPos, BlockCapabilityCache<CAP, Direction>> allSources,
									   Map<BlockPos, BlockCapabilityCache<CAP, Direction>> allTargets,
									   Level lvl) {
		visitedCables.add(getBlockPos());
		allSources.putAll(sources);
		allTargets.putAll(targets);
		
		if (visitedCables.size() < getMaxConnections()) {
			for (BlockPos cablePos : pipes) {
				if (!visitedCables.contains(cablePos)) {
					BlockEntity entity = lvl.getBlockEntity(cablePos);
					if (entity instanceof AbstractPipeBlockEntity<?> adjCable &&
						Objects.equals(adjCable.getCapabilityClass(), capabilityClass)) {
						AbstractPipeBlockEntity<CAP> typed = (AbstractPipeBlockEntity<CAP>) adjCable;
						typed.collectNetworkDevices(visitedCables, allSources, allTargets, lvl);
						typed.updateAllPairs = true;
					}
				}
			}
		}
	}
	
	protected abstract void doTransfer(Map<BlockPos, BlockCapabilityCache<CAP, Direction>> sources,
									 Map<BlockPos, BlockCapabilityCache<CAP, Direction>> targets);
	
}
