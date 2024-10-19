package com.sonamorningstar.eternalartifacts.content.recipe.container;

import com.google.common.collect.Lists;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFluidContainer implements Container {
    private final int size;
    @Getter
    private final NonNullList<FluidStack> fluidStacks;
    @Getter
    private final NonNullList<ItemStack> itemStacks;
    @Nullable
    private List<ContainerListener> listeners;

    public ItemFluidContainer(List<ItemStack> itemStacks, List<FluidStack> fluidStacks) {
        this.size = itemStacks.size() + fluidStacks.size();
        this.fluidStacks = NonNullList.of(FluidStack.EMPTY, fluidStacks.toArray(FluidStack[]::new));
        this.itemStacks = NonNullList.of(ItemStack.EMPTY, itemStacks.toArray(ItemStack[]::new));
    }

    public ItemFluidContainer(ModItemStorage inventory, AbstractFluidTank tank) {
        this(inventory.toList(), tank.toList());
    }


    public void addListener(ContainerListener pListener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }

        this.listeners.add(pListener);
    }

    public void removeListener(ContainerListener pListener) {
        if (this.listeners != null) {
            this.listeners.remove(pListener);
        }
    }

    @Override
    public int getContainerSize() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        for(FluidStack fluidStack : this.fluidStacks) {
            if (!fluidStack.isEmpty()) {
                return false;
            }
        }
        for(ItemStack itemStack : this.itemStacks) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isInBounds(int index) {
        return index < size && index >= 0;
    }

    //region Item Stuff
    @Override
    public ItemStack getItem(int slot) {
        return this.itemStacks.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.itemStacks, slot, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = this.itemStacks.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.itemStacks.set(slot, ItemStack.EMPTY);
            return stack;
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.itemStacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > stack.getMaxStackSize()) {
            stack.setCount(stack.getMaxStackSize());
        }
        setChanged();
    }
    //endregion

    //region Fluid Stuff
    public FluidStack getFluidstack(int index) {
        return fluidStacks.get(index);
    }
    public FluidStack shrinkFluidStack(int index, int amount) {
        if (!isInBounds(index)) return FluidStack.EMPTY;
        FluidStack fluidStack = fluidStacks.get(index);
        if (fluidStack.isEmpty()) return FluidStack.EMPTY;
        int shrinkAmount = fluidStack.getAmount() - amount;
        if (shrinkAmount >= 0 ) {
            fluidStack.setAmount(fluidStack.getAmount() - amount);
            if(fluidStack.getAmount() == 0) fluidStack = FluidStack.EMPTY;
            setChanged();
            return fluidStack;
        }else return FluidStack.EMPTY;
    }

    public FluidStack removeFluidStack(int index) {
        if(isInBounds(index)) {
            setChanged();
            return fluidStacks.remove(index);
        }
        return FluidStack.EMPTY;
    }

    public FluidStack removeFluidStackNoUpdate(int index) {
        if(isInBounds(index)){
            return fluidStacks.remove(index);
        }
        return FluidStack.EMPTY;
    }
    //endregion

    @Override
    public void setChanged() {
        if (listeners != null) {
            for (ContainerListener listener : listeners) {
                listener.containerChanged(this);
            }
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {return true;}

    @Override
    public void clearContent() {
        this.itemStacks.clear();
        this.fluidStacks.clear();
        setChanged();
    }
}
