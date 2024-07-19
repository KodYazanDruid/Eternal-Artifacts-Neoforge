package com.sonamorningstar.eternalartifacts.content.recipe.container;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class SimpleFluidContainer implements Container {
    private final int size;
    @Getter
    private final NonNullList<FluidStack> fluidStacks;
    @Nullable
    private List<ContainerListener> listeners;

    public SimpleFluidContainer(int size) {
        this.size = size;
        this.fluidStacks = NonNullList.withSize(size, FluidStack.EMPTY);
    }

    public SimpleFluidContainer(FluidStack... fluidStacks) {
        this.size = fluidStacks.length;
        this.fluidStacks = NonNullList.of(FluidStack.EMPTY, fluidStacks);
    }

    public void addListener(ContainerListener listener) {
        if (this.listeners == null) this.listeners = Lists.newArrayList();
        this.listeners.add(listener);
    }

    public void removeListener(ContainerListener pListener) {
        if (this.listeners != null) this.listeners.remove(pListener);
    }

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

    @Override
    public int getContainerSize() {
        return this.size;
    }

    private boolean isInBounds(int index) {
        return index < size && index >= 0;
    }

    @Override
    public boolean isEmpty() {
        for(FluidStack fluidStack : this.fluidStacks) {
            if (!fluidStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setChanged() {
        if (this.listeners != null) {
            for(ContainerListener containerlistener : this.listeners) {
                containerlistener.containerChanged(this);
            }
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) { return true; }

    @Override
    public void clearContent() {
        this.fluidStacks.clear();
        this.setChanged();
    }

    //ItemStack related thing which we do not need in this type of container.
    @Override
    public ItemStack getItem(int pSlot) {return null;}
    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {return null;}
    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {return null;}
    @Override
    public void setItem(int pSlot, ItemStack pStack) {}
}
