package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.container.slot.FluidSlot;
import com.sonamorningstar.eternalartifacts.network.SendStringToServer;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import lombok.Setter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractModContainerMenu extends AbstractContainerMenu {
    public final NonNullList<FluidSlot> fluidSlots = NonNullList.create();
    public final Inventory inventory;
    @Setter
    public @Nullable MachineConfiguration machineConfigs = null;

    protected AbstractModContainerMenu(@Nullable MenuType<?> menuType, int id, Inventory inventory) {
        super(menuType, id);
        this.inventory = inventory;
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
        fluidSlots.add(slot);
        return slot;
    }

    public FluidSlot getFluidSlot(int slot) {
        return this.fluidSlots.get(slot);
    }
    
    public void handleFluidTankTransfer(int tankNo, int button) {
        Player player = inventory.player;
        FluidSlot slot = fluidSlots.get(tankNo);
        IFluidHandlerItem containerHandler = getCarried().getCapability(Capabilities.FluidHandler.ITEM);
        IItemHandler playerInventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if (containerHandler != null && playerInventory != null) {
            switch (button) {
                case 0 -> setCarried(drainSlotAndStow(slot, getCarried(), player));
                case 1 -> setCarried(fillSlotAndStow(slot, getCarried(), player));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot instanceof FakeSlot) return ItemStack.EMPTY;
        if (slot != null && slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            ret = slotItem.copy();
            if (index < 36) {
                if (!this.moveItemStackTo(slotItem, 36, slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return ret;
    }
    
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int start, int end, boolean isReverse) {
        boolean ret = false;
        int index = isReverse ? end - 1 : start;
        
        if (stack.isStackable()) {
            while (!stack.isEmpty() && (isReverse ? index >= start : index < end)) {
                Slot slot = this.slots.get(index);
                ItemStack slotStack = slot.getItem();
                
                if (!slotStack.isEmpty()
                    && slot.mayPlace(stack)
                    && ItemStack.isSameItemSameTags(stack, slotStack)) {
                    
                    int maxSize = Math.min(slot.getMaxStackSize(stack), slotStack.getMaxStackSize());
                    int canMove = Math.min(maxSize - slotStack.getCount(), stack.getCount());
                    
                    if (canMove > 0) {
                        ItemStack newSlotStack = slotStack.copy();
                        newSlotStack.grow(canMove);
                        
                        ItemStack newStack = stack.copy();
                        newStack.shrink(canMove);
                        
                        slot.setByPlayer(newSlotStack);
                        stack.setCount(newStack.getCount());
                        updateSlot(slot);
                        
                        ret = true;
                    }
                }
                
                index = isReverse ? index - 1 : index + 1;
            }
        }
        
        if (!stack.isEmpty()) {
            index = isReverse ? end - 1 : start;
            
            while (isReverse ? index >= start : index < end) {
                Slot slot = this.slots.get(index);
                
                if (slot.getItem().isEmpty() && slot.mayPlace(stack)) {
                    int max = slot.getMaxStackSize(stack);
                    
                    if (stack.getCount() > max) {
                        slot.setByPlayer(stack.split(max));
                    } else {
                        slot.setByPlayer(stack.split(stack.getCount()));
                    }
                    
                    updateSlot(slot);
                    ret = true;
                    break;
                }
                
                index = isReverse ? index - 1 : index + 1;
            }
        }
        
        return ret;
    }
    
    //region Packet send from client to server.
    public void fakeSlotSynch(UpdateFakeSlotToServer pkt) {
    
    }
    public void receiveStringPkt(SendStringToServer pkt) {
    
    }
    //endregion
    
    protected void updateSlot(Slot slot) {
        if (slot instanceof SlotItemHandler sih) {
            if (sih.getItemHandler() instanceof CharmStorage charms) {
                charms.onContentsChanged(sih.getSlotIndex());
            }
        } else slot.setChanged();
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
                    if (emptied.getAmount() > 0 ) {
                        container.shrink(1);
                        PlayerHelper.giveItemOrPop(player, containerTank.getContainer());
                    }
                }
            }
        }
        return container;
    }
    private FluidStack emptyContainer(FluidSlot slot, IFluidHandlerItem containerTank, Player player) {
        FluidStack drainable = containerTank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty() && slot.mayPlace(drainable)) {
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
                    if (filledAmount > 0 ) {
                        container.shrink(1);
                        PlayerHelper.giveItemOrPop(player, containerTank.getContainer());
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
