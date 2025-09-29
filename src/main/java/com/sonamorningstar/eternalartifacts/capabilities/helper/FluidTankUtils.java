package com.sonamorningstar.eternalartifacts.capabilities.helper;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidTankUtils {
	
	public static boolean isFluidHandlerEmpty(IFluidHandler handler) {
		for (int i = 0; i < handler.getTanks(); i++) {
			if (!handler.getFluidInTank(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isFluidHandlerFull(IFluidHandler handler) {
		for (int i = 0; i < handler.getTanks(); i++) {
			if (handler.getFluidInTank(i).getAmount() < handler.getTankCapacity(i)) {
				return false;
			}
		}
		return true;
	}
}
