package com.sonamorningstar.eternalartifacts.content.recipe.container;

import com.google.common.collect.Lists;
import com.sonamorningstar.eternalartifacts.content.recipe.container.base.ItemlessContainer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleFluidContainer extends ItemlessContainer {
    private final int size;
    @Setter
    @Getter
    private boolean fixedSize = false;
    @Getter
    protected final NonNullList<FluidStack> fluidStacks;
    protected final Map<Fluid, Integer> capacityMap = new HashMap<>();
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
        return isInBounds(index) ? fluidStacks.get(index) : FluidStack.EMPTY;
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

    public void setFluidStack(int index, FluidStack stack) {
        if (isInBounds(index)) {
            this.fluidStacks.set(index, stack);
            this.setChanged();
        }
    }

    public void setCapacity(Fluid fluid, int capacity) {
        capacityMap.put(fluid, capacity);
    }

    public FluidStack addFluid(FluidStack stack) {
        if (stack.isEmpty()) {
            return FluidStack.EMPTY;
        } else {
            FluidStack fluidStack = stack.copy();
            moveFluidToOccupiedSlotsWithSameType(fluidStack);
            if (fluidStack.isEmpty()) {
                return FluidStack.EMPTY;
            } else {
                this.moveFluidToEmptySlots(fluidStack);
                return fluidStack.isEmpty() ? FluidStack.EMPTY : fluidStack;
            }
        }
    }

    private void moveFluidToEmptySlots(FluidStack stack) {
        for(int i = 0; i < this.size; i++) {
            FluidStack stackInCon = this.getFluidstack(i);
            if (stackInCon.isEmpty()) {
                int transfered = stack.getAmount();
                if (capacityMap.containsKey(stack.getFluid())) {
                    int capacity = capacityMap.get(stack.getFluid());
                    transfered = Mth.clamp(stack.getAmount(),0, capacity);
                }
                FluidStack inserted = stack.copy();
                inserted.setAmount(transfered);
                setFluidStack(i, inserted);
                stack.setAmount(0);
                setChanged();
                return;
            }
        }
    }

    private void moveFluidToOccupiedSlotsWithSameType(FluidStack fluidStack) {
        for(int i = 0; i < this.size; i++) {
            FluidStack stackInCon = this.getFluidstack(i);
            if (stackInCon.isFluidEqual(fluidStack)) {
                moveFluidsBetweenStacks(fluidStack, stackInCon);
                if (fluidStack.isEmpty()) return;
            }
        }
    }

    private void moveFluidsBetweenStacks(FluidStack stack, FluidStack other) {
        int transfered = stack.getAmount();
        if (capacityMap.containsKey(other.getFluid())) {
            int capacity = capacityMap.get(other.getFluid());
            transfered = Mth.clamp(stack.getAmount(),0, capacity - other.getAmount());
        }
        other.grow(transfered);
        stack.shrink(transfered);

        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    protected boolean isInBounds(int index) {
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
}
