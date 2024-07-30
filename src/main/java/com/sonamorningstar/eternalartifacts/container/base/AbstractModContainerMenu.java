package com.sonamorningstar.eternalartifacts.container.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public abstract class AbstractModContainerMenu extends AbstractContainerMenu {
    protected AbstractModContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    protected void addPlayerInventoryAndHotbar(Inventory inventory, int xOff, int yOff) {
        for(int i = 0; i < inventory.items.size(); i++) {
            int x = i % 9;
            int y = i / 9;
            if (i >= 9) addSlot(new Slot(inventory, i, xOff + x * 18, yOff + y * 18));
            else addSlot(new Slot(inventory, i, xOff + x * 18, yOff + 76 + y * 18));
        }
    }

    protected void addPlayerHotbar(Inventory inventory) {
        for(int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    public static OptionalInt openContainer(ServerPlayer player, BlockPos pos) {
        final BlockEntity blockEntity = player.level().getBlockEntity(pos);

        if (!(blockEntity instanceof MenuProvider prov))
            return OptionalInt.empty();

        return player.openMenu(prov, pos);
    }

}
