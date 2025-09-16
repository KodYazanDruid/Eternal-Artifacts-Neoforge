package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

@Getter
public class Cable extends AbstractPipeBlockEntity<IEnergyStorage> implements ITickableServer {
    private final CableBlock.CableTier tier;
    
    private final Map<BlockPos, Map<BlockPos, Integer>> positionToSourceDistances = new HashMap<>();
    
    public Cable(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE.get(), IEnergyStorage.class, pos, state);
        this.tier = ((CableBlock) state.getBlock()).getTier();
    }
    
    @Override
    protected int getMaxRange() {
        return tier.getMaxRange();
    }
    
    @Override
    protected boolean shouldPipesConnect(BlockState neighborState, Direction direction) {
        return super.shouldPipesConnect(neighborState, direction) && neighborState.getBlock() instanceof CableBlock cable &&
            tier == cable.getTier();
    }
    
    @Override
    protected BlockCapabilityCache<IEnergyStorage, Direction> createCache(BlockPos pos, Direction dir) {
        return BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, (ServerLevel) level, pos, dir.getOpposite(),
            () -> !this.isRemoved(),
            () -> this.isDirty = true
        );
    }
    
    @Override
    protected boolean fillSourcesAndTargets(Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> sources,
                                            Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> targets,
                                            BlockCapabilityCache<IEnergyStorage, Direction> cache,
                                            BlockPos pos, Direction dir) {
        IEnergyStorage cap = cache.getCapability();
        boolean ret = false;
        if (cap != null){
            if (cap.canExtract()) {
                sources.put(pos, cache);
                ret = true;
            } else if (cap.canReceive()) {
                targets.put(pos, cache);
                ret = true;
            }
        }
        return ret;
    }
    
    @Override
    public void collectNetworkDevicesWithDistances(Level level) {
        super.collectNetworkDevicesWithDistances(level);
        positionToSourceDistances.clear();
    }
    
    @Override
    protected void doTransfer(Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> sources,
                              Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> targets) {
        int maxTransferRate = tier.getTransferRate();
        
        Map<BlockPos, Integer> allDistances = new HashMap<>();
        sources.forEach((sourcePos, cap) -> {
            Map<BlockPos, Integer> distances = sourceToTargetDistances.getOrDefault(sourcePos, Map.of());
            distances.forEach((targetPos, dist) -> {
                allDistances.merge(targetPos, dist, Math::min);
            });
        });
        
        if (allDistances.isEmpty()) return;
        
        List<BlockPos> availableTargets = allDistances.keySet().stream()
                .filter(targets::containsKey)
                .toList();
        
        if (availableTargets.isEmpty()) return;
        
        int maxExtractable = 0;
        Map<BlockPos, Integer> sourceExtractions = new LinkedHashMap<>();
        
        for (BlockPos sourcePos : sources.keySet()) {
            if (maxExtractable >= maxTransferRate) break;
            
            IEnergyStorage sourceES = sources.get(sourcePos).getCapability();
            if (sourceES == null) continue;
            
            int canExtract = sourceES.extractEnergy(maxTransferRate - maxExtractable, true);
            if (canExtract > 0) {
                sourceExtractions.put(sourcePos, canExtract);
                maxExtractable += canExtract;
            }
        }
        
        if (maxExtractable == 0) return;
        
        int targetCount = availableTargets.size();
        int energyPerTarget = Math.max(1, maxExtractable / targetCount);
        int remainder = maxExtractable % targetCount;
        
        Map<BlockPos, Integer> targetReceptions = new LinkedHashMap<>();
        int totalReceivable = 0;
        
        for (int i = 0; i < availableTargets.size(); i++) {
            BlockPos targetPos = availableTargets.get(i);
            IEnergyStorage targetES = targets.get(targetPos).getCapability();
            if (targetES == null) continue;
            
            int toReceive = energyPerTarget;
            if (i < remainder) {
                toReceive++;
            }
            
            int canReceive = targetES.receiveEnergy(toReceive, true);
            if (canReceive > 0) {
                targetReceptions.put(targetPos, canReceive);
                totalReceivable += canReceive;
            }
        }
        
        if (totalReceivable == 0) return;
        
        int transferAmount = Math.min(maxExtractable, totalReceivable);
        int extracted = 0;
        
        for (var entry : sourceExtractions.entrySet()) {
            if (extracted >= transferAmount) break;
            
            BlockPos pos = entry.getKey();
            int toExtract = Math.min(entry.getValue(), transferAmount - extracted);
            
            IEnergyStorage sourceES = sources.get(pos).getCapability();
            if (sourceES != null) {
                extracted += sourceES.extractEnergy(toExtract, false);
            }
        }
        
        if (extracted <= 0) return;
        
        List<BlockPos> receiveTargets = new ArrayList<>(targetReceptions.keySet());
        int remaining = extracted;
        
        for (int i = 0; i < receiveTargets.size() && remaining > 0; i++) {
            BlockPos targetPos = receiveTargets.get(i);
            IEnergyStorage targetES = targets.get(targetPos).getCapability();
            if (targetES == null) continue;
            
            int baseAmount = extracted * targetReceptions.get(targetPos) / totalReceivable;
            int toReceive = Math.min(baseAmount, remaining);
            
            if (toReceive > 0) {
                int received = targetES.receiveEnergy(toReceive, false);
                remaining -= received;
            }
        }
        
        if (remaining > 0) {
            for (BlockPos targetPos : receiveTargets) {
                if (remaining <= 0) break;
                
                IEnergyStorage targetES = targets.get(targetPos).getCapability();
                if (targetES == null) continue;
                
                int received = targetES.receiveEnergy(remaining, false);
                remaining -= received;
            }
        }
    }
    
    public int extractEnergyFromReachableSources(BlockPos cablePos, int maxAmount, boolean simulate) {
        Map<BlockPos, Integer> sourceDistances = positionToSourceDistances.computeIfAbsent(cablePos, pos -> {
            Map<BlockPos, Integer> distances = new HashMap<>();
            
            for (BlockPos sourcePos : allSources.keySet()) {
                int distance = calculateDistanceToPosition(sourcePos, pos);
                if (distance != Integer.MAX_VALUE && distance <= getMaxRange()) {
                    distances.put(sourcePos, distance);
                }
            }
            
            return distances;
        });
        
        if (sourceDistances.isEmpty()) return 0;
        
        List<BlockPos> reachableSources = sourceDistances.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .toList();
        
        Map<BlockPos, Integer> sourceExtractions = new LinkedHashMap<>();
        int totalExtracted = 0;
        
        for (BlockPos sourcePos : reachableSources) {
            if (totalExtracted >= maxAmount) break;
            
            IEnergyStorage sourceES = allSources.get(sourcePos).getCapability();
            if (sourceES == null) continue;
            
            int canExtract = sourceES.extractEnergy(maxAmount - totalExtracted, true);
            if (canExtract > 0) {
                sourceExtractions.put(sourcePos, canExtract);
                totalExtracted += canExtract;
            }
        }
        
        if (simulate || totalExtracted == 0) return totalExtracted;
        
        int actualExtracted = 0;
        for (var entry : sourceExtractions.entrySet()) {
            BlockPos pos = entry.getKey();
            int toExtract = Math.min(entry.getValue(), maxAmount - actualExtracted);
            
            IEnergyStorage sourceES = allSources.get(pos).getCapability();
            if (sourceES != null) {
                actualExtracted += sourceES.extractEnergy(toExtract, false);
                if (actualExtracted >= maxAmount) break;
            }
        }
        
        return actualExtracted;
    }
    
    private int calculateDistanceToPosition(BlockPos sourcePos, BlockPos targetPos) {
        if (targetPos.equals(sourcePos)) return 0;
        
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, Integer> distances = new HashMap<>();
        Set<BlockPos> visited = new HashSet<>();
        
        queue.add(sourcePos);
        distances.put(sourcePos, 0);
        visited.add(sourcePos);
        
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            int currentDistance = distances.get(current);
            
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                
                if (neighbor.equals(targetPos)) {
                    return currentDistance + 1;
                }
                
                if (!visited.contains(neighbor) && networkPipes.contains(neighbor)) {
                    queue.add(neighbor);
                    distances.put(neighbor, currentDistance + 1);
                    visited.add(neighbor);
                }
            }
        }
        
        return Integer.MAX_VALUE;
    }
    
}