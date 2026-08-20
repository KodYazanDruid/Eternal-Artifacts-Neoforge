package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SmartJukeboxMenu extends AbstractMachineMenu {
    public SmartJukeboxMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMachines.SMART_JUKEBOX.getMenu(), id, inv, entity, data);
        if (beInventory != null) {
            addSlot(new SlotItemHandler(beInventory, 0, 80, 28));
            addSlot(new SlotItemHandler(beInventory, 1, 44, 50));
            addSlot(new SlotItemHandler(beInventory, 2, 62, 50));
            addSlot(new SlotItemHandler(beInventory, 3, 80, 50));
            addSlot(new SlotItemHandler(beInventory, 4, 98, 50));
            addSlot(new SlotItemHandler(beInventory, 5, 116, 50));
        }
    }
}
