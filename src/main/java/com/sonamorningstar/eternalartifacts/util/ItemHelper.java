package com.sonamorningstar.eternalartifacts.util;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemHelper {

    /**
     * I modified some methods in {@link net.neoforged.neoforge.items.ItemHandlerHelper}
     * to make work with {@link ModItemStorage}
     * easier.
     */
    public static ItemStack insertItemForced(ModItemStorage destination, ItemStack stack, boolean simulate) {
        if (destination == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < destination.getSlots(); i++) {
            stack = destination.insertItemForced(i, stack, simulate);
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }

        return stack;
    }
    public static Pair<ItemStack, List<Integer>> insertItemForced(ModItemStorage destination, ItemStack stack, boolean simulate, List<Integer> slots) {
        List<Integer> insertedSlots = new ArrayList<>();
        if (destination == null || stack.isEmpty())
            return Pair.of(stack, Collections.emptyList());

        for (int i = 0; i < destination.getSlots(); i++) {
            if(slots.contains(i)){
                stack = destination.insertItemForced(i, stack, simulate);
                if (stack.isEmpty()) {
                    insertedSlots.add(i);
                    return Pair.of(ItemStack.EMPTY, insertedSlots);
                }
            }
        }

        return Pair.of(stack, insertedSlots);
    }

    public static ItemStack insertItemStackedForced(ModItemStorage inventory, ItemStack stack, boolean simulate) {
        if (inventory == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return insertItemForced(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlots();

        // go through the inventory and try to fill up already existing items
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (ItemHandlerHelper.canItemStacksStackRelaxed(slot, stack)) {
                stack = inventory.insertItemForced(i, stack, simulate);
                if (stack.isEmpty()) {
                    break;
                }
            }
        }
        // insert remainder into empty slots
        if (!stack.isEmpty()) {
            // find empty slot
            for (int i = 0; i < sizeInventory; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    stack = inventory.insertItemForced(i, stack, simulate);
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }
        return stack;
    }
    public static Pair<ItemStack, List<Integer>> insertItemStackedForced(ModItemStorage inventory, ItemStack stack, boolean simulate, List<Integer> slots) {
        //List<Integer> slotsList = Arrays.stream(slots).toList();
        List<Integer> insertedSlots = new ArrayList<>();
        if (inventory == null || stack.isEmpty())
            return Pair.of(stack, Collections.emptyList());

        if (!stack.isStackable()) {
            return insertItemForced(inventory, stack, simulate, slots);
        }

        int sizeInventory = inventory.getSlots();

        for (int i = 0; i < sizeInventory; i++) {
            if(slots.contains(i)) {
                ItemStack slot = inventory.getStackInSlot(i);
                if (ItemHandlerHelper.canItemStacksStackRelaxed(slot, stack)) {
                    stack = inventory.insertItemForced(i, stack, simulate);
                    if (stack.isEmpty()) {
                        insertedSlots.add(i);
                        break;
                    }
                }
            }
        }
        if (!stack.isEmpty()) {
            for (int i = 0; i < sizeInventory; i++) {
                if (inventory.getStackInSlot(i).isEmpty() && slots.contains(i)) {
                    stack = inventory.insertItemForced(i, stack, simulate);
                    if (stack.isEmpty()) {
                        insertedSlots.add(i);
                        break;
                    }
                }
            }
        }
        return Pair.of(stack, insertedSlots);
    }

}
