package com.sonamorningstar.eternalartifacts.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ModItemItemStorage extends ModItemStorage {
    final ItemStack stack;
    public ModItemItemStorage(ItemStack stack, int size) {
        super(size);
        this.stack = stack;
        CompoundTag tag = stack.getOrCreateTag().getCompound("Inventory");
        deserializeNBT(tag);
    }

    @Override
    protected void onContentsChanged(int slot) {
        stack.getOrCreateTag().put("Inventory", serializeNBT());
    }
}
