package com.sonamorningstar.eternalartifacts.capabilities.energy;

import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ModItemEnergyStorage extends ModEnergyStorage {
    final ItemStack stack;
    public ModItemEnergyStorage(int capacity, int maxTransfer, ItemStack stack) {
        super(capacity, maxTransfer);
        this.stack = stack;
        Tag tag = stack.getOrCreateTag().get("Energy");
        if(tag != null) deserializeNBT(tag);
    }

    @Override
    public void onEnergyChanged() {
        stack.getOrCreateTag().put("Energy", serializeNBT());
    }
}
