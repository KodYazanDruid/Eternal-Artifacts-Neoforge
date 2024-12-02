package com.sonamorningstar.eternalartifacts.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;


public class FakeSlotItemHandler extends SlotItemHandler {
    public FakeSlotItemHandler(IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);
    }

    /*@Override
    public boolean mayPlace(ItemStack pStack) {return false;}
    @Override
    public boolean mayPickup(Player pPlayer) {return false;}*/

    /*@Override
    public ItemStack getItem() {
        super.getItem();
        return getItemHandler().getStackInSlot(getSlotIndex());
    }*/

    /*@Override
    public void set(ItemStack stack) {
        fakeItem = stack;
        setChanged();
    }

    @Override
    public ItemStack remove(int amount) {
        fakeItem.shrink(amount);
        return fakeItem;
    }*/

    @Override
    public boolean isFake() {
        return true;
    }

/*    @Override
    public void setChanged() {
        *//*if (holder != null) {
            if (holder.getItem() instanceof BlueprintItem) {
                var list = BlueprintItem.getFakeItems(holder);
                list.set(getSlotIndex(), fakeItem);
                BlueprintItem.updateFakeItems(holder, list);
            }
        }*//*
    }*/
}
