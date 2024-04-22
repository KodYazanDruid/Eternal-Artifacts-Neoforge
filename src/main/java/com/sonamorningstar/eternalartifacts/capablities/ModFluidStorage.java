package com.sonamorningstar.eternalartifacts.capablities;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class ModFluidStorage extends FluidTank {
    public ModFluidStorage(int capacity) {
        this(capacity, e -> true);
    }

    public ModFluidStorage(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    //public int getFluidAmount() { return super.getFluidAmount(); }

    public FluidStack getFluidStack() {
        return this.getFluid();
    }

    public IFluidHandler getFluidHandler() {
        return this;
    }
}
