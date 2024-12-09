package com.sonamorningstar.eternalartifacts.capabilities.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

public class ModularEnergyStorage extends ModEnergyStorage {

    List<IEnergyStorage> energyHandlers = new ArrayList<>();

    public ModularEnergyStorage(RecipeWrapper inventory) {
        super(0, Integer.MAX_VALUE);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            IEnergyStorage cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if(cap != null) energyHandlers.add(cap);
        }
    }

/*    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        int energyReceived = 0;
        for(IEnergyStorage handler : energyHandlers) {
            energyReceived = handler.receiveEnergy(maxReceive, true);
            if(energyReceived > 0 ) {
                energyReceived = handler.receiveEnergy(maxReceive, simulate);
                onEnergyChanged();
                return energyReceived;
            }
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        int energyExtracted = 0;
        for(IEnergyStorage handler : energyHandlers) {
            energyExtracted = handler.extractEnergy(maxExtract, true);
            if(energyExtracted > 0) {
                energyExtracted = handler.extractEnergy(maxExtract, simulate);
                onEnergyChanged();
                return energyExtracted;
            }
        }
        return energyExtracted;
    }*/

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        int totalReceived = 0;
        for(IEnergyStorage handler : energyHandlers) {
            int simReceived = handler.receiveEnergy(maxReceive, true);
            if (simReceived > 0 ) {
                int received = handler.receiveEnergy(simReceived, simulate);
                onEnergyChanged();
                totalReceived += received;
                return totalReceived;
            }
        }
        return totalReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        int totalExtracted = 0;
        for(IEnergyStorage handler : energyHandlers) {
            int simExtracted = handler.extractEnergy(maxExtract, true);
            if(simExtracted > 0) {
                int extracted = handler.extractEnergy(simExtracted, simulate);
                onEnergyChanged();
                totalExtracted += extracted;
                return totalExtracted;
            }
        }
        return totalExtracted;
    }

    @Override
    public int receiveEnergyForced(int maxReceive, boolean simulate) {return receiveEnergy(maxReceive, simulate);}

    @Override
    public int extractEnergyForced(int maxExtract, boolean simulate) {return extractEnergy(maxExtract, simulate);}

    @Override
    public int getEnergyStored() {
        int totalEnergySize = 0;
        for(IEnergyStorage handler : energyHandlers) {
            totalEnergySize += handler.getEnergyStored();
        }
        this.energy = totalEnergySize;
        return totalEnergySize;
    }

    @Override
    public int getMaxEnergyStored() {
        int totalEnergyStored = 0;
        for(IEnergyStorage handler : energyHandlers) {
            totalEnergyStored += handler.getMaxEnergyStored();
        }
        this.capacity = totalEnergyStored;
        return totalEnergyStored;
    }

    @Override
    public boolean canExtract() {
        for(IEnergyStorage handler : energyHandlers)
            if(handler.canExtract()) return true;
        return false;
    }

    @Override
    public boolean canReceive() {
        for(IEnergyStorage handler : energyHandlers)
            if(handler.canReceive()) return true;
        return false;
    }

    public void onEnergyChanged() {}

    public void reloadEnergyHandlers(RecipeWrapper container) {
        List<IEnergyStorage> newEnergyHandlers = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            IEnergyStorage cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if(cap != null) newEnergyHandlers.add(cap);
        }
        energyHandlers = newEnergyHandlers;
    }


}
