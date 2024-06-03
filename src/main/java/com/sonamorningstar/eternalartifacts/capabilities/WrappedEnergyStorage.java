package com.sonamorningstar.eternalartifacts.capabilities;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.function.Predicate;

public record WrappedEnergyStorage(IEnergyStorage energy,
                                   Predicate<Direction> extract,
                                   Predicate<Direction> insert,
                                   Direction ctx) implements IEnergyStorage {

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return insert.test(ctx) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return extract.test(ctx) ? this.energy.extractEnergy(maxExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return this.energy.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return extract.test(ctx) && this.energy.canExtract();
    }

    @Override
    public boolean canReceive() {
        return insert.test(ctx) && this.energy.canReceive();
    }
}
