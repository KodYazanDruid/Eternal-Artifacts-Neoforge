package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class OilRefineryMenu extends AbstractMachineMenu {

    public OilRefineryMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public OilRefineryMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.OIL_REFINERY.get(), pContainerId, inv, entity, data);
        if (getBeInventory() != null) {
            addSlot(new SlotItemHandler(beInventory, 0, 140, 20));
            addSlot(new SlotItemHandler(beInventory, 1, 140, 40));
            addSlot(new SlotItemHandler(beInventory, 2, 140, 60));
        }
        outputSlots.add(0);
        outputSlots.add(1);
        outputSlots.add(2);
    }
}
