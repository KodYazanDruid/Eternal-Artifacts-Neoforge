package com.sonamorningstar.eternalartifacts.container;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public abstract class AbstractMachineMenu extends AbstractContainerMenu {
    protected final IItemHandler beInventory;
    protected final Level level;
    @Getter
    protected final BlockEntity blockEntity;
    protected final List<Integer> outputSlots = new ArrayList<>();
    public final ContainerData data;

    public AbstractMachineMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(pMenuType, pContainerId);
        this.level = inv.player.level();
        this.blockEntity = entity;
        this.beInventory = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        //Player inventory
        if (pIndex < 36) {
            for(int i = 0; i < beInventory.getSlots(); i++) {
                if(outputSlots.contains(i)) return ItemStack.EMPTY;
                else if (!moveItemStackTo(sourceStack, 36, 36 + beInventory.getSlots(), false)) {
                    return ItemStack.EMPTY;
                }
            }
        //Machine inventory
        } else if (pIndex < 36 + beInventory.getSlots()) {
            if (!moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex: " + pIndex);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
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
            int stored = energyStorage.getEnergyStored();
            int max = energyStorage.getMaxEnergyStored();
            int barHeight = 50;
            return max != 0 && stored != 0 ? stored * barHeight / max : 0;
        } else return 0;
    }

    public int getFluidProgress(int slot) {
        IFluidHandler tank = level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), null);
        if(tank != null){
            int amount = tank.getFluidInTank(slot).getAmount();
            int max = tank.getTankCapacity(slot);
            int barHeight = 50;
            return max != 0 && amount != 0 ? amount * barHeight / max : 0;
        } else return 0;
    }

    public int getScaledProgress(int size) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);

        return maxProgress != 0 && progress != 0 ? progress * size / maxProgress : 0;
    }

    public boolean isWorking() {
        return data.get(0) > 0;
    }
}
