package com.sonamorningstar.eternalartifacts.capabilities.energy;

import lombok.Getter;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ModItemEnergyStorage extends ModEnergyStorage {
    final ItemStack stack;
    @Getter
    private final int maxTransfer;
    public ModItemEnergyStorage(int capacity, int maxTransfer, ItemStack stack) {
        super(capacity, maxTransfer);
        this.stack = stack;
        this.maxTransfer = maxTransfer;
        Tag tag = stack.getOrCreateTag().get("Energy");
        if(tag != null) deserializeNBT(tag);
    }

    @Override
    public void onEnergyChanged() {
        stack.getOrCreateTag().put("Energy", serializeNBT());
    }
}
