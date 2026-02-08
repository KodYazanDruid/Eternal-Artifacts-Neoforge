package com.sonamorningstar.eternalartifacts.container.slot;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.function.Supplier;

public class FluidSlot {
	private final Supplier<AbstractFluidTank> handlerGetter;
    public final int index;
    public final int x;
    public final int y;

    public FluidSlot(Supplier<AbstractFluidTank> handlerGetter, int index, int x, int y) {
        this.handlerGetter = handlerGetter;
        this.index = index;
        this.x = x;
        this.y = y;
    }
    
    public AbstractFluidTank getHandler() {
        return handlerGetter.get();
    }
	
	public boolean mayPlace(FluidStack stack) {
        if (stack.isEmpty()) return false;
        return getHandler().isFluidValid(index, stack);
    }
    
    public void setFluid(FluidStack stack) {
        getHandler().setFluid(stack, index);
    }
    
    public void setFluidSilent(FluidStack stack) {
        getHandler().setFluidSilent(stack, index);
    }

    public FluidStack getFluid() {return getHandler().getFluidInTank(index);}
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return getHandler().get(index).fill(resource, action);
    }
    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
       return getHandler().get(index).drain(amount, action);
    }
    public int getMaxSize() {
        return getHandler().getTankCapacity(index);
    }
}
