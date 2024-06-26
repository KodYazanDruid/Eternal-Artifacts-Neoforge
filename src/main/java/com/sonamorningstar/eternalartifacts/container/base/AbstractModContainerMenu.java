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

    protected void addPlayerInventory(Inventory inventory) {
        for(int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; ++l) {
                addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
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
