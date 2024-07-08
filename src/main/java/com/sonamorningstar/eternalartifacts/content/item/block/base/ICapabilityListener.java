package com.sonamorningstar.eternalartifacts.content.item.block.base;

import net.minecraft.world.item.ItemStack;

public interface ICapabilityListener {
    void onFluidContentChange(ItemStack stack);
}
