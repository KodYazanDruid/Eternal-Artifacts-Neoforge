package com.sonamorningstar.eternalartifacts.capabilities.energy;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;

@Getter
public class MachineItemEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundTag> {
    private final ItemStack stack;
    private int maxExtract;
    private int maxReceive;
    private int energyStored;
    private int maxEnergyStored;
    public MachineItemEnergyStorage(ItemStack stack) {
        this.stack = stack;
        CompoundTag tag = stack.getOrCreateTag();
        if (stack.hasTag()) {
            CompoundTag energyTag = tag.getCompound("Energy");
            deserializeNBT(energyTag);
        }

    }

    public void onEnergyChanged() {
        stack.getOrCreateTag().put("Energy", serializeNBT());
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;

        int energyReceived = Math.min(maxEnergyStored - energyStored, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energyStored += energyReceived;
            if (energyReceived > 0) onEnergyChanged();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

        int energyExtracted = Math.min(energyStored, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energyStored -= energyExtracted;
            if (energyExtracted > 0) onEnergyChanged();
        }
        return energyExtracted;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag energyNbt = new CompoundTag();
        energyNbt.putInt("EnergyAmount", getEnergyStored());
        energyNbt.putInt("MaxExtract", getMaxExtract());
        energyNbt.putInt("MaxReceive", getMaxReceive());
        energyNbt.putInt("MaxEnergyStored", getMaxEnergyStored());
        return energyNbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energyStored = nbt.getInt("EnergyAmount");
        maxExtract = nbt.getInt("MaxExtract");
        maxReceive = nbt.getInt("MaxReceive");
        maxEnergyStored = nbt.getInt("MaxEnergyStored");
    }
}
