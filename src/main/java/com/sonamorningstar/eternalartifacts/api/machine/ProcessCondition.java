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
    private BooleanSupplier supplier;
    
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
        if(supplier == null || !supplier.getAsBoolean()) {
            SimpleContainer container = new SimpleContainer(outputSlots.size());
            for (int i = 0; i < outputSlots.size(); i++) container.setItem(i, inventory.getStackInSlot(outputSlots.get(i)).copy());
            for (int i = 0; i < queuedItemStackImports.size(); i++) {
                if (i >= container.getContainerSize()) {
                    supplier = preventWorking();
                    return this;
                }
                ItemStack remainder = container.addItem(queuedItemStackImports.get(i));
                if (!remainder.isEmpty()) {
                    supplier = preventWorking();
                    return this;
                }
                supplier = () -> !remainder.isEmpty();
            }
        }
        return this;
    }
    
    public ProcessCondition commitQueuedFluidStackImports() {
        if(supplier == null || !supplier.getAsBoolean()) {
            if (outputTank == null) {
                supplier = preventWorking();
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
                    supplier = preventWorking();
                    return this;
                }
                FluidStack queuedStack = queuedFluidStackImports.get(i);
                FluidStack remainder = container.addFluid(queuedStack);
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
    public ProcessCondition tryExtractItemForced(int count, int slot) {
        if (supplier == null || !supplier.getAsBoolean()) {
            ItemStack extracted = inventory.extractItem(slot, count, true);
            if (extracted.isEmpty()) supplier = preventWorking();
            else {
                if (extracted.getCount() < count) supplier = preventWorking();
                else supplier = noCondition();
            }
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
    public ProcessCondition tryExtractFluidForced(FluidStack stack) {
        if(supplier == null || !supplier.getAsBoolean()) {
            FluidStack drained = inputTank.drainForced(stack, IFluidHandler.FluidAction.SIMULATE);
            if(drained.isEmpty()) supplier = preventWorking();
            else supplier = noCondition();
        }
        return this;
    }
    
    public ProcessCondition createCustomCondition(BooleanSupplier custom) {
        if (supplier == null || !supplier.getAsBoolean()) {
            supplier = custom;
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
