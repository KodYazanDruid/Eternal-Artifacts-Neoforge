package com.sonamorningstar.eternalartifacts.capabilities.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class ModItemStorage extends ItemStackHandler {
    private final List<IntConsumer> listeners = new ArrayList<>();
    public ModItemStorage(int size) {
        super(size);
    }
    
    public void addListener(IntConsumer listener) {
        listeners.add(listener);
    }

    public ItemStack insertItemForced(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    public List<ItemStack> toList() {
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < getSlots(); i++) {
            items.add(this.getStackInSlot(i));
        }
        return items;
    }
    
    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        for (IntConsumer listener : listeners) {
            listener.accept(slot);
        }
    }
    
    public void sendUpdate(int slot) {
        onContentsChanged(slot);
    }

}
