package com.sonamorningstar.eternalartifacts.content.recipe.inventory;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidSlot {
    private final IFluidHandler handler;
    public final int index;
    public final int x;
    public final int y;

    public FluidSlot(IFluidHandler handler, int index, int x, int y) {
        this.handler = handler;
        this.index = index;
        this.x = x;
        this.y = y;
    }

    public boolean mayPlace(FluidStack stack) {
        if (stack.isEmpty()) return false;
        return handler.isFluidValid(0, stack);
    }

    public FluidStack getFluid() {return handler.getFluidInTank(0);}
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return handler.fill(resource, action);
    }
    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        return handler.drain(amount, action);
    }
    public int getMaxSize() {
        return handler.getTankCapacity(0);
    }
}
