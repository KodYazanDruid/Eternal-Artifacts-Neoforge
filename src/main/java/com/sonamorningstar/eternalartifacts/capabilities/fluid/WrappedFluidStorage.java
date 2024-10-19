package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public record WrappedFluidStorage(IFluidHandler tank,
                                  Predicate<Direction> extract,
                                  BiPredicate<Direction, FluidStack> insert,
                                  Direction ctx) implements IFluidHandler {

    @Override
    public int getTanks() {
        return this.tank.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.tank.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tank.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return insert.test(ctx, stack) && this.tank.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return insert.test(ctx, resource) ? this.tank.fill(resource, action) : 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return extract.test(ctx) ? this.tank.drain(resource, action) : FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return extract.test(ctx) ? this.tank.drain(maxDrain, action) : FluidStack.EMPTY;
    }
}
