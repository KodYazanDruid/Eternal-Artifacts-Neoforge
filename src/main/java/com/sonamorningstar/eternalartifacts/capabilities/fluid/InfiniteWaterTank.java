package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class InfiniteWaterTank implements IFluidHandler {
    public static final InfiniteWaterTank INSTANCE = new InfiniteWaterTank();
    private static final FluidStack INFINITE_WATER = new FluidStack(Fluids.WATER, Integer.MAX_VALUE);

    private InfiniteWaterTank(){}

    @Override
    public int getTanks() {return 1;}
    @Override
    public FluidStack getFluidInTank(int tank) {return INFINITE_WATER;}
    @Override
    public int getTankCapacity(int tank) {return Integer.MAX_VALUE;}
    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {return false;}
    @Override
    public int fill(FluidStack resource, FluidAction action) {return 0;}

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.is(Fluids.WATER)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return maxDrain > 0 ? INFINITE_WATER.copyWithAmount(maxDrain) : FluidStack.EMPTY;
    }
}
