package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.CableEnergyProvider;
import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class CableBlockEntity extends ModBlockEntity {
    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE.get(), pos, state);
    }

    public int receiveEnergy(int maxReceive, boolean simulate, @Nullable Direction dir) {
        if(level != null && !level.isClientSide() && dir != null && hasConnection(dir)) {
            for (Direction neighbor : getConnections()) {
                if (neighbor == dir) continue;
                BlockPos relativePos = getBlockPos().relative(neighbor);
                BlockState relativeState = level.getBlockState(relativePos);
                BlockEntity relativeBE = level.getBlockEntity(relativePos);
                IEnergyStorage relativeES = level.getCapability(Capabilities.EnergyStorage.BLOCK, relativePos, relativeState, relativeBE, dir);
                if (relativeES != null) {
                    return relativeES.receiveEnergy(maxReceive, simulate);
                }
            }
        }
        return 0;
    }

    public int pushEnergy(int maxExtract, boolean simulate, @Nullable Direction dir) {
        if(level != null && !level.isClientSide() && dir != null && hasConnection(dir)) {
            for (Direction neighbor : getConnections()) {
                if (neighbor == dir) continue;
                BlockPos relativePos = getBlockPos().relative(neighbor);
                BlockState relativeState = level.getBlockState(relativePos);
                BlockEntity relativeBE = level.getBlockEntity(relativePos);
                IEnergyStorage relativeES = level.getCapability(Capabilities.EnergyStorage.BLOCK, relativePos, relativeState, relativeBE, dir);
                if (relativeES != null) {
                    return relativeES.extractEnergy(maxExtract, simulate);
                }
            }
        }
        return 0;
    }

    public static CableEnergyProvider getCapability(CableBlockEntity cable, @Nullable Direction dir) {
        if (dir == null || cable.hasConnection(dir)) return new CableEnergyProvider(cable, dir);
        return null;
    }

    private boolean hasConnection(Direction dir) {
        return getConnections().contains(dir);
    }

    private List<Direction> getConnections() {
        if (level == null) return List.of();
        CableBlock cable = ((CableBlock) getBlockState().getBlock());
        return cable.getConnections(getBlockPos(), level);
    }


}
