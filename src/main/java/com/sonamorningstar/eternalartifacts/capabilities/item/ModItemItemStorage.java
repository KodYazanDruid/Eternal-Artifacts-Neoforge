package com.sonamorningstar.eternalartifacts.capabilities.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ModItemItemStorage extends ModItemStorage {
    final ItemStack stack;
    final int size;
    @Nullable
    public Player player;
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
        super.onContentsChanged(slot);
        CompoundTag tag = serializeNBT();
        int nbtSize = tag.getInt("Size");
        if (nbtSize > 0) tag.putInt("Size", size);
        stack.getOrCreateTag().put("Inventory", tag);

    }
}
