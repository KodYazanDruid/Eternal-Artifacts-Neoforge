package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.api.machine.GenericScreenInfo;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class GenericMachineMenu extends AbstractMachineMenu {
    public int arrowX;
    public int arrowY = 41;
    public int slotArrowPadding = 3;
    public final GenericScreenInfo screenInfo;
    public GenericMachineMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(menuType, id, inv, entity, data);
        int marginX = 7;
        this.screenInfo = ((GenericMachineBlockEntity) this.getBlockEntity()).getScreenInfo();
        if (beEnergy != null) marginX += 18;
        if (beTank != null) marginX += 18;
        this.arrowX = screenInfo.isOverrideArrowPos() ? screenInfo.getArrowX() : marginX + 56;
        this.arrowX += screenInfo.getArrowXOffset();
        this.arrowY = screenInfo.isOverrideArrowPos() ? screenInfo.getArrowY() : arrowY;
        if (beInventory != null) {
            if (screenInfo.isShouldBindSlots()){
                int inputIndex = getBeInventory().getSlots() - outputSlots.size();
                for (int i = 0; i < beInventory.getSlots(); i++) {
                    if (outputSlots.contains(i))
                        addSlot(new SlotItemHandler(beInventory, i, arrowX + 25 + slotArrowPadding + (outputSlots.indexOf(i) * 18), arrowY-1));
                    else {
                        addSlot(new SlotItemHandler(beInventory, i, arrowX - slotArrowPadding - (inputIndex * 18), arrowY-1));
                        inputIndex--;
                    }
                }
            } else{
                screenInfo.getSlotPositions().forEach((slot, pair) -> {
                    addSlot(new SlotItemHandler(beInventory, slot, pair.getFirst(), pair.getSecond()));
                });
            }
        }
    }
}
