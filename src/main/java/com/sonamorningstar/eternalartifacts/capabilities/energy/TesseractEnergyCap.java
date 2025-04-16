package com.sonamorningstar.eternalartifacts.capabilities.energy;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Set;

public record TesseractEnergyCap(Tesseract tesseract) implements IEnergyStorage {
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		/*if (tesseract.getLevel().isClientSide()) return 0;
		Set<Tesseract> tesseracts = TesseractNetworks.get(tesseract.getLevel()).getTesseracts(tesseract.getCachedNetwork());
		tesseracts.stream().filter(t -> !t.equals(tesseract)).forEach(t -> {
			IEnergyStorage storage = t.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, t.getBlockPos(), null);
		});*/
		return 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}
	
	@Override
	public int getEnergyStored() {
		return 0;
	}
	
	@Override
	public int getMaxEnergyStored() {
		return 0;
	}
	
	@Override
	public boolean canExtract() {
		return true;
	}
	
	@Override
	public boolean canReceive() {
		return true;
	}
}
