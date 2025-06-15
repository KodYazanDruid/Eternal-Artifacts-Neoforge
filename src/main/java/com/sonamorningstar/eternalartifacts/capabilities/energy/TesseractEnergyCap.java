package com.sonamorningstar.eternalartifacts.capabilities.energy;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import net.minecraft.nbt.CompoundTag;

import static com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract.TransferMode.*;

public class TesseractEnergyCap extends ModEnergyStorage {
	private final Tesseract tesseract;
	
	public TesseractEnergyCap(Tesseract tesseract) {
		super(512000, 256000, 256000);
		this.tesseract = tesseract;
		var network = tesseract.getCachedTesseractNetwork();
		if (network != null) {
			CompoundTag tag = network.getSavedData();
			if (tag != null) {
				deserializeNBT(tag.get("Energy"));
			}
		}
	}
	
	@Override
	public boolean canReceive() {
		var mode = tesseract.getTransferMode();
		if (mode == NONE) return false;
		return (mode == BOTH || mode == INSERT_ONLY) && super.canReceive();
	}
	
	@Override
	public boolean canExtract() {
		var mode = tesseract.getTransferMode();
		if (mode == NONE) return false;
		return (mode == BOTH || mode == EXTRACT_ONLY) && super.canExtract();
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == INSERT_ONLY) return super.receiveEnergy(maxReceive, simulate);
		else return 0;
	}
	@Override
	public int receiveEnergyForced(int maxReceive, boolean simulate) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == INSERT_ONLY) return super.receiveEnergyForced(maxReceive, simulate);
		else return 0;
	}
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == EXTRACT_ONLY) return super.extractEnergy(maxExtract, simulate);
		else return 0;
	}
	@Override
	public int extractEnergyForced(int maxExtract, boolean simulate) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == EXTRACT_ONLY) return super.extractEnergyForced(maxExtract, simulate);
		else return 0;
	}
	
	@Override
	public void onEnergyChanged() {
		var network = tesseract.getCachedTesseractNetwork();
		if (network != null) {
			CompoundTag tag = new CompoundTag();
			tag.put("Energy", serializeNBT());
			network.setSavedData(tag);
		}
		TesseractNetworks.get(tesseract.getLevel()).getTesseracts().get(network).forEach(Tesseract::invalidateCapabilities);
	}
}
