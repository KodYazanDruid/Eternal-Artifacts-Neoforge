package com.sonamorningstar.eternalartifacts.capabilities;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class ModFluidStorage extends FluidTank {
    public ModFluidStorage(int capacity) {
        this(capacity, e -> true);
    }

    public ModFluidStorage(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    public int fillForced(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() /*|| !isFluidValid(resource)*/) {
            return 0;
        }
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    public FluidStack drainForced(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.EMPTY;
        }
        return drainForced(resource.getAmount(), action);
    }

    public FluidStack drainForced(int maxDrain, FluidAction action) {
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

}
