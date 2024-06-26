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

public class BioFurnaceMenu extends AbstractMachineMenu {

    public BioFurnaceMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public BioFurnaceMenu(int pContainerId, Inventory pPlayerInventory, BlockEntity pBlockEntity, ContainerData pData) {
        super(ModMenuTypes.BIOFURNACE.get() ,pContainerId, pPlayerInventory, pBlockEntity, pData);

        IItemHandler ih = level.getCapability(Capabilities.ItemHandler.BLOCK, pBlockEntity.getBlockPos(), null);

        if(ih != null) {
            addSlot(new SlotItemHandler(ih, 0, 80, 35));
        }

    }

}
