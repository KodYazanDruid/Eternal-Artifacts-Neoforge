package com.sonamorningstar.eternalartifacts.content.recipe.inventory;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
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
        return handler.isFluidValid(index, stack);
    }

    public FluidStack getFluid() {return handler.getFluidInTank(index);}
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (handler instanceof AbstractFluidTank modTank) return modTank.get(index).fill(resource, action);
        else return handler.fill(resource, action);
    }
    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        if (handler instanceof AbstractFluidTank modTank) return modTank.get(index).drain(amount, action);
        else return handler.drain(amount, action);
    }
    public int getMaxSize() {
        return handler.getTankCapacity(index);
    }
}
