package com.sonamorningstar.eternalartifacts.container;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public abstract class AbstractMachineMenu extends AbstractContainerMenu {
    protected final IItemHandler beInventory;
    protected final Level level;
    protected final BlockEntity blockEntity;

    public AbstractMachineMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inv, BlockEntity entity) {
        super(pMenuType, pContainerId);
        this.level = inv.player.level();
        this.blockEntity = entity;
        this.beInventory = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < beInventory.getSlots()) {
                if (!this.moveItemStackTo(itemstack1, beInventory.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, beInventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, blockEntity.getBlockState().getBlock());
    }

    public void addPlayerInventory(Inventory inventory) {
        for(int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; ++l) {
                addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    public void addPlayerHotbar(Inventory inventory) {
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

    public int getEnergyProgress() {
        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, blockEntity.getBlockPos(), null);
        if(energyStorage != null){
            int stored =energyStorage.getEnergyStored();
            int max = energyStorage.getMaxEnergyStored();
            int barHeight = 50;
            return max != 0 && stored != 0 ? stored * barHeight / max : 0;
        } else return 0;
    }
}
