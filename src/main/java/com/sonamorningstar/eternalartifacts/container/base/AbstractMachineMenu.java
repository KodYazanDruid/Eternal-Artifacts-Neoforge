package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractMachineMenu extends AbstractModContainerMenu {
    @Getter
    protected final IItemHandler beInventory;
    @Getter
    protected final IEnergyStorage beEnergy;
    @Getter
    protected final IFluidHandler beTank;
    protected final Level level;
    @Getter
    protected final BlockEntity blockEntity;
    protected final List<Integer> outputSlots = new ArrayList<>();
    public final ContainerData data;
    public final MenuType<?> menuType;

    public AbstractMachineMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(menuType, id);
        this.menuType = menuType;
        this.level = inv.player.level();
        this.blockEntity = entity;
        this.beInventory = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        this.beEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        this.beTank = level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        this.data = data;
        addPlayerInventoryAndHotbar(inv, 8, 66);
        addDataSlots(data);
        if (blockEntity instanceof SidedTransferMachineBlockEntity<?> sided) outputSlots.addAll(sided.outputSlots);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
         ItemStack itemstack = ItemStack.EMPTY;
         Slot slot = this.slots.get(index);
         if (slot != null && slot.hasItem()) {
             ItemStack itemstack1 = slot.getItem();
             itemstack = itemstack1.copy();
             //Clicked from player inventory
             if (index < 36) {
                 for(int i = index; i < this.slots.size(); i++) {
                     if(outputSlots.contains(i - 36)) return ItemStack.EMPTY;
                     else if (!this.moveItemStackTo(itemstack1, 36, this.slots.size(), false)) {
                         return ItemStack.EMPTY;
                     }
                 }
                 //Clicked from opened container
             } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
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

    public int getEnergyProgress() {
        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, blockEntity.getBlockPos(), null);
        if(energyStorage != null){
            int stored = energyStorage.getEnergyStored();
            int max = energyStorage.getMaxEnergyStored();
            int barHeight = 50;
            return max != 0 && stored != 0 ? stored * barHeight / max : 0;
        } else return 0;
    }

    public int getFluidProgress(int slot, int height) {
        IFluidHandler tank = level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), null);
        if(tank != null){
            int amount = tank.getFluidInTank(slot).getAmount();
            int max = tank.getTankCapacity(slot);
            return max != 0 && amount != 0 ? amount * height / max : 0;
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

    public static OptionalInt openContainer(Player player, BlockPos pos) {
        final BlockEntity blockEntity = player.level().getBlockEntity(pos);
        if (!(blockEntity instanceof MenuProvider prov)) return OptionalInt.empty();
        return player.openMenu(prov, pos);
    }
}
