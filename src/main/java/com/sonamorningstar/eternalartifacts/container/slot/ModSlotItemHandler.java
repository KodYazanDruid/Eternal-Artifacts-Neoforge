package com.sonamorningstar.eternalartifacts.container.slot;

import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ModSlotItemHandler extends SlotItemHandler {
    private final ModItemStorage itemStorage;
    private final int index;
    public ModSlotItemHandler(ModItemStorage itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.itemStorage = itemHandler;
        this.index = index;
    }

    @Override
    public void setChanged() {
        itemStorage.sendUpdate(index);
    }
}
