package com.sonamorningstar.eternalartifacts.content.item.base;

import net.minecraft.world.item.ItemStack;

public interface IActiveStack {

    boolean isActive(ItemStack stack);
}
