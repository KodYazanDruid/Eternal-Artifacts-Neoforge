package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class Cable extends AbstractPipeBlockEntity<IEnergyStorage> implements ITickableServer {
    private final CableBlock.CableTier tier;
    
    public Cable(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE.get(), IEnergyStorage.class, pos, state);
        this.tier = ((CableBlock) state.getBlock()).getTier();
    }
    
    @Override
    protected int getMaxConnections() {
        return tier.getMaxConnections();
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
    protected void doTransfer(Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> sources,
                              Map<BlockPos, BlockCapabilityCache<IEnergyStorage, Direction>> targets) {
        int maxExtractable = 0;
        Map<BlockPos, Integer> sourceExtractions = new LinkedHashMap<>();
        
        for (var sourceEntry : sources.entrySet()) {
            IEnergyStorage sourceES = sourceEntry.getValue().getCapability();
            if (sourceES == null) continue;
            
            int canExtract = sourceES.extractEnergy(tier.getTransferRate() - maxExtractable, true);
            if (canExtract > 0) {
                sourceExtractions.put(sourceEntry.getKey(), canExtract);
                maxExtractable += canExtract;
                
                if (maxExtractable >= tier.getTransferRate()) break;
            }
        }
        
        if (maxExtractable == 0) return;
        
        int maxReceivable = 0;
        Map<BlockPos, Integer> targetReceptions = new LinkedHashMap<>();
        
        for (var targetEntry : targets.entrySet()) {
            IEnergyStorage targetES = targetEntry.getValue().getCapability();
            if (targetES == null) continue;
            
            int canReceive = targetES.receiveEnergy(maxExtractable - maxReceivable, true);
            if (!sourceExtractions.containsKey(targetEntry.getKey()) && canReceive > 0) {
                targetReceptions.put(targetEntry.getKey(), canReceive);
                maxReceivable += canReceive;
                
                if (maxReceivable >= maxExtractable) break;
            }
        }
        
        if (maxReceivable == 0) return;
        
        int transferAmount = Math.min(maxExtractable, maxReceivable);
        if (transferAmount <= 0) return;
        
        int extracted = 0;
        for (var entry : sourceExtractions.entrySet()) {
            BlockPos pos = entry.getKey();
            int toExtract = Math.min(entry.getValue(), transferAmount - extracted);
            
            IEnergyStorage sourceES = sources.get(pos).getCapability();
            if (sourceES != null) {
                extracted += sourceES.extractEnergy(toExtract, false);
                if (extracted >= transferAmount) break;
            }
        }
        
        int distributed = 0;
        for (var entry : targetReceptions.entrySet()) {
            BlockPos pos = entry.getKey();
            int toReceive = Math.min(entry.getValue(), extracted - distributed);
            
            IEnergyStorage targetES = targets.get(pos).getCapability();
            if (targetES != null) {
                distributed += targetES.receiveEnergy(toReceive, false);
                if (distributed >= extracted) break;
            }
        }
    }
    
    public int extractEnergyFromSources(int maxAmount, boolean simulate) {
        int totalExtracted = 0;
        Map<BlockPos, Integer> sourceExtractions = new LinkedHashMap<>();
        
        for (var sourceEntry : allSources.entrySet()) {
            if (totalExtracted >= maxAmount) break;
            
            IEnergyStorage sourceES = sourceEntry.getValue().getCapability();
            if (sourceES == null) continue;
            
            int canExtract = sourceES.extractEnergy(maxAmount - totalExtracted, true);
            if (canExtract > 0) {
                sourceExtractions.put(sourceEntry.getKey(), canExtract);
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
    
}