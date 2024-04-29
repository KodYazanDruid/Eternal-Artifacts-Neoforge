package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BookDuplicatorMenu extends AbstractMachineMenu {

    public BookDuplicatorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BookDuplicatorMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.BOOK_DUPLICATOR.get(), pContainerId, inv, entity);

        if(beInventory != null) {
            addSlot(new SlotItemHandler(beInventory, 0, 44, 16));
            addSlot(new SlotItemHandler(beInventory, 1, 44, 42));
            addSlot(new SlotItemHandler(beInventory, 2, 80, 42));
        }

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }



}
