package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import com.sonamorningstar.eternalartifacts.util.AutomationHelper;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractModContainerMenu extends AbstractContainerMenu {
    public final NonNullList<FluidSlot> fluidSlots = NonNullList.create();

    protected AbstractModContainerMenu(@Nullable MenuType<?> menuType, int id) {
        super(menuType, id);
    }

    protected void addPlayerInventoryAndHotbar(Inventory inventory, int xOff, int yOff) {
        for(int i = 0; i < inventory.items.size(); i++) {
            int x = i % 9;
            int y = i / 9;
            if (i >= 9) addSlot(new Slot(inventory, i, xOff + x * 18, yOff + y * 18));
            else addSlot(new Slot(inventory, i, xOff + x * 18, yOff + 76 + y * 18));
        }
    }

    protected FluidSlot addFluidSlot(FluidSlot slot) {
        fluidSlots.add(slot.index, slot);
        return slot;
    }

    public FluidSlot getFluidSlot(int slot) {
        return this.fluidSlots.get(slot);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 36) {
                if (!this.moveItemStackTo(itemstack1, 36, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
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

    protected ItemStack fillSlotAndStow(FluidSlot slot, ItemStack container, Player player) {
        IFluidHandlerItem containerTank = container.getCapability(Capabilities.FluidHandler.ITEM);
        if (container.isEmpty()) return ItemStack.EMPTY;
        if (containerTank != null) {
            if (container.getCount() == 1) {
                emptyContainer(slot, containerTank, player);
                return containerTank.getContainer();
            }
            else {
                ItemStack singleContainer = container.copyWithCount(1);
                containerTank = singleContainer.getCapability(Capabilities.FluidHandler.ITEM);
                if (containerTank != null) {
                    FluidStack emptied = emptyContainer(slot, containerTank, player);
                    if (emptied.getAmount() > 0) {
                        container.shrink(1);
                        PlayerHelper.giveItemOrPop(player, containerTank.getContainer());
                        /*if(!player.addItem(containerTank.getContainer())) {
                            popStackInLevel(player.level(), player.getX(), player.getY(), player.getZ(), containerTank.getContainer());
                        }*/
                    }
                }
            }
        }
        return container;
    }
    private FluidStack emptyContainer(FluidSlot slot, IFluidHandlerItem containerTank, Player player) {
        FluidStack drainable = containerTank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty()) {
            Fluid fluid = drainable.getFluid();
            CompoundTag fluidTag = drainable.getTag();
            int fillableAmount = slot.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
            if (fillableAmount > 0) {
                FluidStack transferred = new FluidStack(fluid, fillableAmount, fluidTag);
                int drained = containerTank.drain(fillableAmount, IFluidHandler.FluidAction.SIMULATE).getAmount();
                if (drained == fillableAmount) {
                    transferred.getFluid().getPickupSound().ifPresent(sound -> {
                        player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS,1.0F, 1.0F);
                    });
                    slot.fill(transferred.copyWithAmount(drained), IFluidHandler.FluidAction.EXECUTE);
                    return containerTank.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
        return FluidStack.EMPTY;
    }

    protected ItemStack drainSlotAndStow(FluidSlot slot, ItemStack container, Player player) {
        IFluidHandlerItem containerTank = container.getCapability(Capabilities.FluidHandler.ITEM);
        if (container.isEmpty()) return ItemStack.EMPTY;
        if (containerTank != null) {
            if (container.getCount() == 1) {
                fillContainer(slot, containerTank, player);
                return containerTank.getContainer();
            }
            else {
                ItemStack singleContainer = container.copyWithCount(1);
                containerTank = singleContainer.getCapability(Capabilities.FluidHandler.ITEM);
                if (containerTank != null) {
                    int filledAmount = fillContainer(slot, containerTank, player);
                    if (filledAmount > 0) {
                        container.shrink(1);
                        PlayerHelper.giveItemOrPop(player, containerTank.getContainer());
                        /*if (!player.addItem(containerTank.getContainer())) {
                            popStackInLevel(player.level(), player.getX(), player.getY(), player.getZ(), containerTank.getContainer());
                        }*/
                    }
                }
            }
        }
        return container;
    }
    private int fillContainer(FluidSlot slot, IFluidHandlerItem containerTank, Player player) {
        FluidStack fillable = slot.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (!fillable.isEmpty()) {
            Fluid fluid = fillable.getFluid();
            CompoundTag fluidTag = fillable.getTag();
            int drainableAmount = containerTank.fill(fillable, IFluidHandler.FluidAction.SIMULATE);
            if (drainableAmount > 0) {
                FluidStack transferred = new FluidStack(fluid, drainableAmount, fluidTag);
                int drained = slot.drain(transferred.getAmount(), IFluidHandler.FluidAction.SIMULATE).getAmount();
                if (drained == drainableAmount) {
                    transferred.getFluid().getPickupSound().ifPresent(sound -> {
                        player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS,1.0F, 1.0F);
                    });
                    slot.drain(drained, IFluidHandler.FluidAction.EXECUTE);
                    return containerTank.fill(transferred.copyWithAmount(drained), IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
        return 0;
    }
}
