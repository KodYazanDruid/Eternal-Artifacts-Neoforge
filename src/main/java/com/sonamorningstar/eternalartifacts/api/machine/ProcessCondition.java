package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.capabilities.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ProcessCondition {
    private ModItemStorage inventory;
    private List<Integer> outputSlots = new ArrayList<>();
    private List<ItemStack> queuedItemStacks = new ArrayList<>();
    private List<FluidStack> queuedFluidStacks = new ArrayList<>();
    private AbstractFluidTank inputTank;
    private AbstractFluidTank outputTank;
    private List<AbstractFluidTank> outputTanks = new ArrayList<>();
    private BooleanSupplier supplier;

    //region Initializing Handlers
    public ProcessCondition initInventory(ModItemStorage inventory) {
        this.inventory = inventory;
        return this;
    }
    public ProcessCondition initOutputSlots(List<Integer> slots) {
        this.outputSlots.addAll(slots);
        return this;
    }
    public ProcessCondition initInputTank(AbstractFluidTank tank) {
        this.inputTank = tank;
        return this;
    }
    public ProcessCondition initOutputTank(AbstractFluidTank tank) {
        this.outputTank = tank;
        this.outputTanks.add(tank);
        return this;
    }
    public ProcessCondition queueItemStack(ItemStack stack) {
        this.queuedItemStacks.add(stack);
        return this;
    }
    public ProcessCondition queueFluidStack(FluidStack stack) {
        this.queuedFluidStacks.add(stack);
        return this;
    }
    //endregion

    //region Transfer Logics.
    public ProcessCondition tryInsertForced(ItemStack... stacks) {
        if(supplier == null || !supplier.getAsBoolean()) {
            SimpleContainer container = new SimpleContainer(outputSlots.size());
            for (int i = 0; i < outputSlots.size(); i++) container.setItem(i, inventory.getStackInSlot(outputSlots.get(i)).copy());
            for (ItemStack stack : stacks) {
                ItemStack remainder = container.addItem(stack);
                if (!remainder.isEmpty()) {
                    supplier = preventWorking();
                    return this;
                }
                supplier = () -> !remainder.isEmpty();
            }
        }
        return this;
    }
    public ProcessCondition tryInsertForced(ItemStack stack, int slot) {
        if(!outputSlots.contains(slot)) supplier = preventWorking();
        if(supplier == null || !supplier.getAsBoolean()) {
            ItemStack remainder = inventory.insertItemForced(slot, stack, true);
            supplier = () -> !remainder.isEmpty();
        }
        return this;
    }

    public ProcessCondition commitQueuedItemStacks() {
        if(supplier == null || !supplier.getAsBoolean()) {
            SimpleContainer container = new SimpleContainer(outputSlots.size());
            for (int i = 0; i < outputSlots.size(); i++) container.setItem(i, inventory.getStackInSlot(outputSlots.get(i)).copy());
            for (ItemStack stack : queuedItemStacks) {
                ItemStack remainder = container.addItem(stack);
                if (!remainder.isEmpty()) {
                    supplier = preventWorking();
                    return this;
                }
                supplier = () -> !remainder.isEmpty();
            }
        }
        return this;
    }

    public ProcessCondition tryExtractItemForced(int count) {
        if (supplier == null || !supplier.getAsBoolean()) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack extracted = inventory.extractItem(i, count, true);
                if (!extracted.isEmpty()) {
                    supplier = noCondition();
                    break;
                } else supplier = preventWorking();
            }
        }
        return this;
    }

    public ProcessCondition tryExtractForced(int count, int slot) {
        if (supplier == null || !supplier.getAsBoolean()) {
            ItemStack extracted = inventory.extractItem(slot, count, true);
            supplier = extracted::isEmpty;
        }
        return this;
    }
    public ProcessCondition tryInsertForced(FluidStack stack) {
        if(supplier == null || !supplier.getAsBoolean()) {
            int inserted = outputTank.fillForced(stack, IFluidHandler.FluidAction.SIMULATE);
            if (inserted != stack.getAmount()) supplier = preventWorking();
            else supplier = noCondition();
        }
        return this;

        /*if(supplier == null || !supplier.getAsBoolean()) {
            SimpleFluidContainer container = new SimpleFluidContainer(outputTanks.size());
            for (int i = 0; i < outputTanks.size(); i++) container.setFluidStack(i, outputTanks.get(i).getFluidInTank(0));
            for (FluidStack stack : stacks) {
                FluidStack inserted = container.addFluid(stack);
                if (inserted.isEmpty()) {
                    supplier = preventWorking();
                    return this;
                }
                supplier = inserted::isEmpty;
            }
        }
        return this;*/
    }
    public ProcessCondition commitQueuedFluidStacks() {
        if(supplier == null || !supplier.getAsBoolean()) {
            SimpleFluidContainer container = new SimpleFluidContainer(outputTanks.size());
            for (int i = 0; i < outputTanks.size(); i++) container.setFluidStack(i, outputTanks.get(i).getFluidInTank(0));
            for (FluidStack stack : queuedFluidStacks) {
                FluidStack inserted = container.addFluid(stack);
                if (inserted.isEmpty()) {
                    supplier = preventWorking();
                    return this;
                }
                supplier = inserted::isEmpty;
            }
        }
        return this;
    }
    public ProcessCondition tryExtractForced(FluidStack stack) {
        if(supplier == null || !supplier.getAsBoolean()) {
            FluidStack drained = inputTank.drainForced(stack, IFluidHandler.FluidAction.SIMULATE);
            if(drained.isEmpty()) supplier = preventWorking();
            else supplier = noCondition();
        }
        return this;
    }
    public ProcessCondition tryExtractFluidForced(int fluidAmount) {
        if(supplier == null || !supplier.getAsBoolean()) {
            FluidStack drained = inputTank.drainForced(fluidAmount, IFluidHandler.FluidAction.SIMULATE);
            if (drained.getAmount() < fluidAmount) supplier = preventWorking();
            else supplier = noCondition();
        }
        return this;
    }
    //endregion

    public static BooleanSupplier noCondition() { return () -> false; }
    public static BooleanSupplier preventWorking() { return () -> true; }

    public boolean getResult() {
        return supplier == null || supplier.getAsBoolean();
    }

}
