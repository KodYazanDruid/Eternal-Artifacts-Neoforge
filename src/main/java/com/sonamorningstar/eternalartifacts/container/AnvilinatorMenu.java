package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AnvilinatorMenu extends AbstractMachineMenu {

    public AnvilinatorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }
    public AnvilinatorMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ANVILINATOR.get(), pContainerId, inv, entity, data);

        IItemHandler ih = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
        if(ih != null) {
            addSlot(new SlotItemHandler(ih, 0, 47, 52));
            addSlot(new SlotItemHandler(ih, 1, 96, 52));
            addSlot(new SlotItemHandler(ih, 2, 154, 52));
            addSlot(new SlotItemHandler(ih, 3, 47, 21));
        }

    }
}
