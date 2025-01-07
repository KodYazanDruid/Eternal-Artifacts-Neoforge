package com.sonamorningstar.eternalartifacts.content.item.base;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyRendererItem extends Item {
    public EnergyRendererItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            return energy.getEnergyStored() > 0;
        }
        return false;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float charge = getChargeLevel(stack);
        return (int) Math.round(charge * 13.0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x880808;
    }

    private static float getChargeLevel(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energy != null ? energy.getEnergyStored() / (float) energy.getMaxEnergyStored() : 0;
    }
}
