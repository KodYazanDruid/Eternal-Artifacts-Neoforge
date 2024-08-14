package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class MachineProcessCondition {
    private ModItemStorage inventory;
    private int[] outputSlots = new int[]{};
    private final List<ModFluidStorage> outputTanks = new ArrayList<>();
    private final List<ModFluidStorage> inputTanks = new ArrayList<>();
    private BooleanSupplier supplier;

    //region Initializing Handlers
    public MachineProcessCondition initInventory(ModItemStorage inventory) {
        this.inventory = inventory;
        return this;
    }
    public MachineProcessCondition initOutputSlots(int... slots) {
        this.outputSlots = slots;
        return this;
    }
    public MachineProcessCondition initInputTanks(ModFluidStorage... tanks) {
        this.inputTanks.addAll(List.of(tanks));
        return this;
    }
    public MachineProcessCondition initInputTanks(List<ModFluidStorage> tanks) {
        this.inputTanks.addAll(tanks);
        return this;
    }
    public MachineProcessCondition initOutputTanks(ModFluidStorage... tanks) {
        this.outputTanks.addAll(List.of(tanks));
        return this;
    }
    public MachineProcessCondition initOutputTanks(List<ModFluidStorage> tanks) {
        this.outputTanks.addAll(tanks);
        return this;
    }
    //endregion

    //region Transfer Logics.
    public MachineProcessCondition tryInsertForced(ItemStack... stacks) {
        if(inventory == null) supplier = preventWorking();
        if(supplier == null || !supplier.getAsBoolean()) {
            for(ItemStack stack : stacks) {
                ItemStack remainder = ItemHelper.insertItemStackedForced(inventory, stack, true, outputSlots);
                supplier = () -> !remainder.isEmpty();
            }
        }
        return this;
    }
    public MachineProcessCondition tryInsertForced(ItemStack stack, int slot) {
        if(inventory == null) supplier = preventWorking();
        if(!Arrays.stream(outputSlots).boxed().toList().contains(slot)) supplier = preventWorking();
        if(supplier == null || !supplier.getAsBoolean()) {
            ItemStack remainder = inventory.insertItemForced(slot, stack, true);
            supplier = () -> !remainder.isEmpty();
        }
        return this;
    }

    public MachineProcessCondition tryInsertForced(FluidStack stack) {
        if(outputTanks.isEmpty()) supplier = preventWorking();
        if(supplier == null || !supplier.getAsBoolean()) {
            for (ModFluidStorage tank : outputTanks) {
                int remainder = tank.fillForced(stack, IFluidHandler.FluidAction.SIMULATE);
                if (remainder > 0) {
                    supplier = preventWorking();
                    break;
                } else supplier = noCondition();
            }
        }
        return this;
    }
    public MachineProcessCondition tryExtractForced(FluidStack stack) {
        if(inputTanks.isEmpty()) supplier = preventWorking();
        if(supplier == null || !supplier.getAsBoolean()) {
            for (ModFluidStorage tank : inputTanks) {
                FluidStack drained = tank.drainForced(stack, IFluidHandler.FluidAction.SIMULATE);
                if(drained.isEmpty()) {
                    supplier = preventWorking();
                    break;
                } else supplier = noCondition();
            }
        }
        return this;
    }
    public MachineProcessCondition tryExtractForced(int fluidAmount) {
        if(inputTanks.isEmpty()) supplier = preventWorking();
        if(supplier == null || !supplier.getAsBoolean()) {
            for(ModFluidStorage tank : inputTanks) {
                FluidStack drained = tank.drainForced(fluidAmount, IFluidHandler.FluidAction.SIMULATE);
                if (drained.getAmount() < fluidAmount) {
                    supplier = preventWorking();
                    break;
                } else supplier = noCondition();
            }
        }
        return this;
    }
    //endregion

    public static BooleanSupplier noCondition() { return () -> false; }
    public static BooleanSupplier preventWorking() { return () -> true; }

    public boolean getResult() { return supplier == null || supplier.getAsBoolean(); }

}
