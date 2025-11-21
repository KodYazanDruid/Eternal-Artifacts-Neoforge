package com.sonamorningstar.eternalartifacts.container.slot;

import com.sonamorningstar.eternalartifacts.capabilities.item.DeepItemStorageHandler;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DeepStorageSlotItemHandler extends SlotItemHandler {
    private final DeepItemStorageHandler itemStorage;
    public DeepStorageSlotItemHandler(DeepItemStorageHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.itemStorage = itemHandler;
    }
    
    @Override
    public int getMaxStackSize() {
        return itemStorage.getSlotLimit(0);
    }
    
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return getMaxStackSize();
    }
}
