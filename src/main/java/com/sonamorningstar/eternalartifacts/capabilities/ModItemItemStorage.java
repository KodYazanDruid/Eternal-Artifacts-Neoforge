package com.sonamorningstar.eternalartifacts.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ModItemItemStorage extends ModItemStorage {
    final ItemStack stack;
    final int size;
    public ModItemItemStorage(ItemStack stack, int size) {
        super(size);
        this.stack = stack;
        this.size = size;
        CompoundTag tag = stack.getOrCreateTag().getCompound("Inventory");
        int nbtSize = tag.getInt("Size");
        if (nbtSize > 0) tag.putInt("Size", size);
        deserializeNBT(tag);
    }

    @Override
    protected void onContentsChanged(int slot) {
        CompoundTag tag = serializeNBT();
        int nbtSize = tag.getInt("Size");
        if (nbtSize > 0) tag.putInt("Size", size);
        stack.getOrCreateTag().put("Inventory", tag);

    }
}
