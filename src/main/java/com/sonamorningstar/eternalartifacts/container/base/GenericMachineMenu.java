package com.sonamorningstar.eternalartifacts.container.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class GenericMachineMenu extends AbstractMachineMenu{
    public int arrowX;
    public int arrowY = 41;
    public int slotArrowPadding = 3;
    public GenericMachineMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(menuType, id, inv, entity, data);
        int marginX = 7;
        if (beEnergy != null) marginX += 18;
        if (beTank != null) marginX += 18;
        this.arrowX = marginX + 56;
        int y = 40;
        if (beInventory != null) {
            int inputIndex = getBeInventory().getSlots() - outputSlots.size();
            for (int i = 0; i < beInventory.getSlots(); i++) {
                if (outputSlots.contains(i)) addSlot(new SlotItemHandler(beInventory, i, arrowX + 25 + slotArrowPadding + (outputSlots.indexOf(i) * 18), y));
                else {
                    addSlot(new SlotItemHandler(beInventory, i, arrowX - slotArrowPadding - (inputIndex * 18), y));
                    inputIndex--;
                }
            }
        }
    }
}
