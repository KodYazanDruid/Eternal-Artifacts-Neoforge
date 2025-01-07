package com.sonamorningstar.eternalartifacts.capabilities.energy;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.EnergyStorage;

public abstract class ModEnergyStorage extends EnergyStorage {
    public ModEnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract){
        super(capacity, maxReceive, maxExtract);
    };

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extractedEnergy = super.extractEnergy(maxExtract, simulate);
        if(extractedEnergy != 0) onEnergyChanged();
        return extractedEnergy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int receiveEnergy = super.receiveEnergy(maxReceive, simulate);
        if(receiveEnergy != 0) onEnergyChanged();
        return receiveEnergy;
    }

    public int extractEnergyForced(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            if(energyExtracted != 0) onEnergyChanged();
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    public int receiveEnergyForced(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            if(energyReceived != 0) onEnergyChanged();
            energy += energyReceived;
        }
        return energyReceived;
    }

    public void setEnergy(int amount) {
        energy = Mth.clamp(amount,0, capacity);
        onEnergyChanged();
    }

    public abstract void onEnergyChanged();
}
