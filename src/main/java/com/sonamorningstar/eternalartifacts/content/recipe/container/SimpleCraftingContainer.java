package com.sonamorningstar.eternalartifacts.content.recipe.container;

import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SimpleCraftingContainer implements CraftingContainer {
    private final ModItemStorage inventory;
    private final List<Integer> outputSlots;

    public SimpleCraftingContainer(ModItemStorage inventory, List<Integer> outputSlots) {
        this.inventory = inventory;
        this.outputSlots = outputSlots;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (outputSlots.contains(i) || inventory.getSlots() <= i) continue;
            items.add(inventory.getStackInSlot(i));
        }
        return items;
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        return getItems().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < 9 ? inventory.getStackInSlot(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack extracted = getItem(slot);
        if (!extracted.isEmpty()) {
            setChanged();
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
            return extracted;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack extracted = inventory.getStackInSlot(pSlot);
        if (!extracted.isEmpty()) {
            inventory.setStackInSlot(pSlot, ItemStack.EMPTY);
            return extracted;
        }
        return extracted;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        inventory.setStackInSlot(pSlot, pStack);
        setChanged();
    }

    @Override
    public void setChanged() {
        inventory.sendUpdate(-1);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return !pPlayer.isDeadOrDying();
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    @Override
    public void fillStackedContents(StackedContents contents) {
        for(ItemStack stack : getItems()) {
            contents.accountStack(stack);
        }
    }
}
