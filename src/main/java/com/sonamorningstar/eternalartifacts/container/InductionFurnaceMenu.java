package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class InductionFurnaceMenu extends AbstractMachineMenu {
    public InductionFurnaceMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(menuType, id, inv, entity, data);
        if(beInventory != null) {
            addSlot(new SlotItemHandler(beInventory, 0,  40, 35));
            addSlot(new SlotItemHandler(beInventory, 1,  60, 35));
            addSlot(new SlotItemHandler(beInventory, 2,  110, 35));
            addSlot(new SlotItemHandler(beInventory, 3,  130, 35));
        }
    }
}
