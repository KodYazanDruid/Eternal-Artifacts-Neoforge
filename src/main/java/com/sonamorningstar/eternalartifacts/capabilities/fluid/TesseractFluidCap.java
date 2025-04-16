package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public record TesseractFluidCap(Tesseract tesseract) implements IFluidHandler {
	@Override
	public int getTanks() {
		return 0;
	}
	
	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}
	
	@Override
	public int getTankCapacity(int tank) {
		return 0;
	}
	
	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return true;
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}
	
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}
}
