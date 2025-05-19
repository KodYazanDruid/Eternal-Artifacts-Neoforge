package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract.TransferMode.*;

public class TesseractFluidCap extends ModFluidStorage {
	private final Tesseract tesseract;
	public TesseractFluidCap(Tesseract tesseract) {
		super(8000);
		this.tesseract = tesseract;
		var network = tesseract.getCachedNetwork();
		if (network != null) {
			CompoundTag tag = network.getSavedData();
			if (tag != null) {
				deserializeNBT(tag);
			}
		}
	}
	
	@Override
	public boolean isFluidValid(FluidStack stack) {
		return tesseract.getTransferMode() != NONE && super.isFluidValid(stack);
	}
	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return tesseract.getTransferMode() != NONE && super.isFluidValid(tank, stack);
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == INSERT_ONLY) return super.fill(resource, action);
		else return 0;
	}
	@Override
	public int fillForced(FluidStack resource, FluidAction action) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH ||mode == INSERT_ONLY)return super.fillForced(resource, action);
		else return 0;
	}
	
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == EXTRACT_ONLY) return super.drain(maxDrain, action);
		else return FluidStack.EMPTY;
	}
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == EXTRACT_ONLY) return super.drain(resource, action);
		else return FluidStack.EMPTY;
	}
	@Override
	public FluidStack drainForced(int maxDrain, FluidAction action) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == EXTRACT_ONLY) return super.drainForced(maxDrain, action);
		else return FluidStack.EMPTY;
	}
	@Override
	public FluidStack drainForced(FluidStack resource, FluidAction action) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == EXTRACT_ONLY) return super.drainForced(resource, action);
		else return FluidStack.EMPTY;
	}
	
	@Override
	protected void onContentsChanged() {
		var network = tesseract.getCachedNetwork();
		if (network != null) {
			network.setSavedData(serializeNBT());
		}
		TesseractNetworks.get(tesseract.getLevel()).getTesseracts().get(network).forEach(Tesseract::invalidateCapabilities);
	}
}
