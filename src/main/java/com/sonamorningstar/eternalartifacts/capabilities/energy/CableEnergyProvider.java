package com.sonamorningstar.eternalartifacts.capabilities.energy;

import com.sonamorningstar.eternalartifacts.content.block.entity.CableBlockEntity;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

@RequiredArgsConstructor
public class CableEnergyProvider implements IEnergyStorage {
    private final CableBlockEntity cable;
    @Nullable
    private final Direction direction;

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return cable.receiveEnergy(maxReceive, simulate, direction);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return cable.pushEnergy(maxExtract, simulate, direction);
    }

    @Override
    public int getEnergyStored() {return 0;}
    @Override
    public int getMaxEnergyStored() {return 0;}
    @Override
    public boolean canExtract() {return false;}
    @Override
    public boolean canReceive() {return false;}
}
