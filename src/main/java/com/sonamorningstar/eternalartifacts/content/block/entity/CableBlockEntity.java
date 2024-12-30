package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import mekanism.common.lib.collection.HashList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;

public class CableBlockEntity extends ModBlockEntity implements ITickableServer {
    public final IEnergyStorage energy;

    private final LinkedHashSet<CableConsumer> sources = new HashList<>();
    private final LinkedHashSet<CableConsumer> targets = new HashList<>();
    private final LinkedHashSet<BlockPos> cables = new HashList<>();

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE.get(), pos, state);
        this.energy = new CableEnergyProvider();
    }

    public void updateConnections(Level lvl) {
        if (!lvl.isClientSide()) {
            sources.clear();
            targets.clear();
            cables.clear();
            for (Direction dir : Direction.values()) {
                BlockPos offset = getBlockPos().relative(dir);
                if (lvl.getBlockEntity(offset) instanceof CableBlockEntity) cables.add(offset);
                else {
                    CableConsumer consumer = new CableConsumer(this, offset, dir.getOpposite(), ((ServerLevel) lvl));
                    IEnergyStorage storage = consumer.getEnergyStorage();
                    if (storage != null) {
                        if (storage.canExtract()) {
                            if(!targets.contains(consumer)) sources.add(consumer);
                        }
                        if (storage.canReceive()) {
                            if (!sources.contains(consumer)) targets.add(consumer);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        //updateConnections(lvl);
        traverseConsumers(sources, targets);
        for (BlockPos cable : cables) {
            BlockEntity adj = lvl.getBlockEntity(cable);
            if (adj instanceof CableBlockEntity adjCable) traverseConsumers(adjCable.sources, adjCable.targets);
        }
    }

    private static void traverseConsumers(LinkedHashSet<CableConsumer> sources, LinkedHashSet<CableConsumer> targets) {
        for (CableConsumer source : sources) {
            IEnergyStorage sourceES = source.getEnergyStorage();
            if (sourceES == null) continue;
            for (CableConsumer target : targets) {
                IEnergyStorage targetES = target.getEnergyStorage();
                if (targetES == null) continue;
                int transfer = Math.min(sourceES.extractEnergy(Integer.MAX_VALUE, true), targetES.receiveEnergy(Integer.MAX_VALUE, true));
                sourceES.extractEnergy(transfer, false);
                targetES.receiveEnergy(transfer, false);
            }
        }
    }

    private static class CableConsumer{
        private final CableBlockEntity cable;
        private final BlockPos pos;
        private final Direction direction;
        private final ServerLevel level;
        private final BlockCapabilityCache<IEnergyStorage, Direction> cache;

        public CableConsumer(CableBlockEntity cable, BlockPos pos, Direction direction, ServerLevel level) {
            this.cable = cable;
            this.pos = pos;
            this.direction = direction;
            this.level = level;
            this.cache = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, level, pos, direction, () -> !cable.isRemoved(), () -> {
                BlockState state = cable.getBlockState();
                if (state.getBlock() instanceof CableBlock cableBlock) cableBlock.updateConnections(state, level, cable.getBlockPos());
            });
        }

        @Nullable
        public IEnergyStorage getEnergyStorage() {
            //return level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction);
            return cache.getCapability();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CableConsumer that)) return false;
            return Objects.equals(level, that.level) && Objects.equals(pos, that.pos) && direction == that.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, direction, level);
        }
    }

    private static class CableEnergyProvider implements IEnergyStorage {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {return 0;}
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {return 0;}

        @Override
        public int getEnergyStored() {return 0;}
        @Override
        public int getMaxEnergyStored() {return 0;}

        @Override
        public boolean canExtract() {return false;}
        @Override
        public boolean canReceive() {return false;}
    }
}
