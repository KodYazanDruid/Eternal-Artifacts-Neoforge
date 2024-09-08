package com.sonamorningstar.eternalartifacts.content.recipe.container.base;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public abstract class ItemlessContainer implements Container {
    @Override
    public ItemStack getItem(int pSlot) {return null;}
    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {return null;}
    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {return null;}
    @Override
    public void setItem(int pSlot, ItemStack pStack) {}
}
