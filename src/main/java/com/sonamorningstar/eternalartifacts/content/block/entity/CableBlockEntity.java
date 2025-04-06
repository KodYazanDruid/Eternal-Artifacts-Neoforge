package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class CableBlockEntity extends ModBlockEntity implements ITickableServer {
    public final Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> sources = new LinkedHashMap<>();
    public final Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> targets = new LinkedHashMap<>();
    public final LinkedHashSet<BlockPos> cables = new LinkedHashSet<>();
    private final CableBlock.CableTier tier;
    public boolean isDirty = false;
    private boolean isUpdatingConnections = false;
    
    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE.get(), pos, state);
        this.tier = ((CableBlock) state.getBlock()).getTier();
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) updateConnections(level);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        if (isDirty) updateConnections(lvl);
        
        if (sources.isEmpty()) return;
        
        LinkedHashSet<BlockPos> visitedCables = new LinkedHashSet<>();
        Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> allSources = new LinkedHashMap<>();
        Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> allTargets = new LinkedHashMap<>();
        
        collectNetworkDevices(visitedCables, allSources, allTargets, lvl);
        
        if (allSources.isEmpty() || allTargets.isEmpty()) {
            return;
        }
        
        transferEnergy(allSources, allTargets);
    }
    
    public void updateConnections(Level lvl) {
        if (lvl.isClientSide() || isUpdatingConnections) return;
        
        try {
            isUpdatingConnections = true;
            
            sources.clear();
            targets.clear();
            cables.clear();
            
            for (Direction dir : Direction.values()) {
                BlockPos offset = getBlockPos().relative(dir);
                BlockState neighborState = lvl.getBlockState(offset);
                BlockState state = getBlockState();
                
                if (neighborState.getBlock() instanceof CableBlock cable && tier == cable.getTier()) {
                    cables.add(offset);
                    if (lvl.isAreaLoaded(getBlockPos(), 1) && state.getBlock() instanceof CableBlock) {
                        lvl.setBlockAndUpdate(getBlockPos(), state.setValue(CableBlock.PROPERTY_BY_DIRECTION.get(dir), true));
                    }
                    continue;
                }
                
                BlockCapabilityCache<IEnergyStorage, Direction> cache =
                    BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, (ServerLevel) level, offset, dir.getOpposite(),
                        () -> !this.isRemoved(),
                        () -> this.isDirty = true
                    );
                
                IEnergyStorage storage = cache.getCapability();
                boolean canConnect = false;
                if (storage != null) {
                    if (storage.canExtract()) {
                        sources.put(offset, cache);
                        canConnect = true;
                    }
                    
                    if (storage.canReceive()) {
                        targets.put(offset, cache);
                        canConnect = true;
                    }
                }
                
                if (lvl.isAreaLoaded(getBlockPos(), 1) && state.getBlock() instanceof CableBlock) {
                    lvl.setBlockAndUpdate(getBlockPos(), state.setValue(CableBlock.PROPERTY_BY_DIRECTION.get(dir), canConnect));
                }
            }
        } finally {
            isUpdatingConnections = false;
        }
    }
    
    private void collectNetworkDevices(LinkedHashSet<BlockPos> visitedCables,
                                       Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> allSources,
                                       Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> allTargets,
                                       Level lvl) {
        visitedCables.add(getBlockPos());
        
        allSources.putAll(sources);
        allTargets.putAll(targets);
        
        if (visitedCables.size() < tier.getMaxConnections()) {
            for (BlockPos cablePos : cables) {
                if (!visitedCables.contains(cablePos)) {
                    BlockEntity entity = lvl.getBlockEntity(cablePos);
                    if (entity instanceof CableBlockEntity adjCable) {
                        adjCable.collectNetworkDevices(visitedCables, allSources, allTargets, lvl);
                    }
                }
            }
        }
    }
    
    private void transferEnergy(Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> sources,
                                Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> targets) {
        for (var sourceEntry : sources.entrySet()) {
            BlockPos sourcePos = sourceEntry.getKey();
            IEnergyStorage sourceES = sourceEntry.getValue().getCapability();
            if (sourceES == null) continue;
            
            for (var targetEntry : targets.entrySet()) {
                BlockPos targetPos = targetEntry.getKey();
                if (sourcePos.equals(targetPos)) continue;
                
                IEnergyStorage targetES = targetEntry.getValue().getCapability();
                if (targetES == null) continue;
                
                int transfer = Math.min(sourceES.extractEnergy(tier.getTransferRate(), true), targetES.receiveEnergy(tier.getTransferRate(), true));
                if (transfer > 0) {
                    sourceES.extractEnergy(transfer, false);
                    targetES.receiveEnergy(transfer, false);
                }
            }
        }
    }
}