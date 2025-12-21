package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ProcessCondition {
    private final Machine<?> machine;
    private final ModItemStorage inventory;
    private final List<Integer> outputSlots;
    private final List<ItemStack> queuedItemStackImports = new ArrayList<>();
    private final List<FluidStack> queuedFluidStackImports = new ArrayList<>();
    private AbstractFluidTank inputTank;
    private AbstractFluidTank outputTank;
    private BooleanSupplier shouldAbort;
    
    public ProcessCondition(Machine<?> machine) {
        this.machine = machine;
        this.inventory = machine.inventory;
        this.outputSlots = machine.outputSlots;
    }

    //region Initializing Handlers
    public ProcessCondition initInputTank(AbstractFluidTank tank) {
        this.inputTank = tank;
        return this;
    }
    public ProcessCondition initOutputTank(AbstractFluidTank tank) {
        this.outputTank = tank;
        return this;
    }
    public ProcessCondition queueImport(ItemStack stack) {
        this.queuedItemStackImports.add(stack);
        return this;
    }
    public ProcessCondition queueImport(FluidStack stack) {
        this.queuedFluidStackImports.add(stack);
        return this;
    }
    //endregion

    //region Transfer Logics.
    public ProcessCondition commitQueuedImports() {
        if (!queuedItemStackImports.isEmpty()) commitQueuedItemStackImports();
        if (!queuedFluidStackImports.isEmpty()) commitQueuedFluidStackImports();
        return this;
    }
    public ProcessCondition commitQueuedItemStackImports() {
        if(shouldAbort == null || !shouldAbort.getAsBoolean()) {
            SimpleContainer container = new SimpleContainer(outputSlots.size());
            for (int i = 0; i < outputSlots.size(); i++) container.setItem(i, inventory.getStackInSlot(outputSlots.get(i)).copy());
            for (int i = 0; i < queuedItemStackImports.size(); i++) {
                if (i >= container.getContainerSize()) {
                    shouldAbort = abort();
                    return this;
                }
                ItemStack remainder = container.addItem(queuedItemStackImports.get(i));
                if (!remainder.isEmpty()) {
                    shouldAbort = abort();
                    return this;
                }
                shouldAbort = () -> !remainder.isEmpty();
            }
        }
        return this;
    }
    
    public ProcessCondition commitQueuedFluidStackImports() {
        if(shouldAbort == null || !shouldAbort.getAsBoolean()) {
            if (outputTank == null) {
                shouldAbort = abort();
                return this;
            }
            SimpleFluidContainer container = new SimpleFluidContainer(outputTank.getTanks());
            container.setFixedSize(true);
            for(int i = 0; i < outputTank.getTanks(); i++) {
                container.setFluidStack(i, outputTank.getFluid(i).copy());
                container.setCapacity(outputTank.getFluid(i).getFluid(), outputTank.getTankCapacity(i));
            }
            for(int i = 0; i < queuedFluidStackImports.size(); i++) {
                if (i >= container.getContainerSize()) {
                    shouldAbort = abort();
                    return this;
                }
                FluidStack queuedStack = queuedFluidStackImports.get(i);
                FluidStack remainder = container.addFluid(queuedStack);
                if (!remainder.isEmpty()) {
                    shouldAbort = abort();
                    return this;
                }
                shouldAbort = () -> !remainder.isEmpty();
            }
        }
        return this;
    }
    
    public ProcessCondition tryExtractItemForced(int count) {
        if (shouldAbort == null || !shouldAbort.getAsBoolean()) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack extracted = inventory.extractItem(i, count, true);
                if (!extracted.isEmpty()) {
                    shouldAbort = noCondition();
                    break;
                } else shouldAbort = abort();
            }
        }
        return this;
    }
    public ProcessCondition tryExtractItemForced(int count, int slot) {
        if (shouldAbort == null || !shouldAbort.getAsBoolean()) {
            ItemStack extracted = inventory.extractItem(slot, count, true);
            if (extracted.isEmpty()) shouldAbort = abort();
            else {
                if (extracted.getCount() < count) shouldAbort = abort();
                else shouldAbort = noCondition();
            }
        }
        return this;
    }
    public ProcessCondition tryExtractFluidForced(int fluidAmount) {
        if(shouldAbort == null || !shouldAbort.getAsBoolean()) {
            FluidStack drained = inputTank.drainForced(fluidAmount, IFluidHandler.FluidAction.SIMULATE);
            if (drained.getAmount() < fluidAmount) shouldAbort = abort();
            else shouldAbort = noCondition();
        }
        return this;
    }
    public ProcessCondition tryExtractFluidForced(FluidStack stack) {
        if(shouldAbort == null || !shouldAbort.getAsBoolean()) {
            FluidStack drained = inputTank.drainForced(stack, IFluidHandler.FluidAction.SIMULATE);
            if(drained.isEmpty()) shouldAbort = abort();
            else shouldAbort = noCondition();
        }
        return this;
    }
    
    public ProcessCondition createCustomCondition(BooleanSupplier custom) {
        if (shouldAbort == null || !shouldAbort.getAsBoolean()) {
            shouldAbort = custom;
        }
        return this;
    }
    //endregion
    
    public static BooleanSupplier noCondition() { return () -> false; }
    public static BooleanSupplier abort() { return () -> true; }

    public boolean shouldAbourt() {
        return shouldAbort == null || shouldAbort.getAsBoolean();
    }

}
