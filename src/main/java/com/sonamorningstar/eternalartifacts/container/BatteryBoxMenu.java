package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BatteryBoxMenu extends AbstractMachineMenu {
    public BatteryBoxMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public BatteryBoxMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.BATTERY_BOX.get(), pContainerId, inv, entity, data);

        if(beInventory != null) {
            addSlot(new SlotItemHandler(beInventory, 0, 80, 35));
            addSlot(new SlotItemHandler(beInventory, 1, 98, 35));
            addSlot(new SlotItemHandler(beInventory, 2, 80, 53));
            addSlot(new SlotItemHandler(beInventory, 3, 98, 53));
        }
    }
}
