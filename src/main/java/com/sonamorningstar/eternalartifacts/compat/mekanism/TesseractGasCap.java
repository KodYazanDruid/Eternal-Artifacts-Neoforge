package com.sonamorningstar.eternalartifacts.compat.mekanism;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;

import static com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract.TransferMode.*;

public class TesseractGasCap implements IGasHandler {
	private static final long CAPACITY = 256000;
	private final Tesseract tesseract;
	private GasStack stored = GasStack.EMPTY;
	
	public TesseractGasCap(Tesseract tesseract) {
		this.tesseract = tesseract;
		var network = tesseract.getCachedTesseractNetwork();
		if (network != null) {
			CompoundTag tag = network.getSavedData();
			if (tag != null) {
				deserializeNBT(tag);
			}
		}
	}
	
	public void deserializeNBT(CompoundTag tag) {
		if (tag.contains("Gas")) {
			stored = GasStack.readFromNBT(tag.getCompound("Gas"));
		}
	}
	
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if (!stored.isEmpty()) {
			CompoundTag gasTag = new CompoundTag();
			stored.write(gasTag);
			tag.put("Gas", gasTag);
		}
		return tag;
	}
	
	@Override
	public int getTanks() {
		return 1;
	}
	
	@Override
	public GasStack getChemicalInTank(int tank) {
		return tank == 0 ? stored : GasStack.EMPTY;
	}
	
	@Override
	public void setChemicalInTank(int tank, GasStack stack) {
		if (tank == 0) {
			stored = stack;
			onContentsChanged();
		}
	}
	
	@Override
	public long getTankCapacity(int tank) {
		return tank == 0 ? CAPACITY : 0;
	}
	
	@Override
	public boolean isValid(int tank, GasStack stack) {
		return tank == 0 && tesseract.getTransferMode() != NONE;
	}
	
	@Override
	public GasStack insertChemical(int tank, GasStack stack, Action action) {
		if (tank != 0 || stack.isEmpty()) return stack;
		
		var mode = tesseract.getTransferMode();
		if (mode != BOTH && mode != INSERT_ONLY) return stack;
		
		if (stored.isEmpty()) {
			long toInsert = Math.min(stack.getAmount(), CAPACITY);
			if (action.execute()) {
				stored = copyWithAmount(stack, toInsert);
				onContentsChanged();
			}
			return stack.getAmount() > toInsert ? copyWithAmount(stack, stack.getAmount() - toInsert) : GasStack.EMPTY;
		} else if (stored.isTypeEqual(stack)) {
			long availableSpace = CAPACITY - stored.getAmount();
			long toInsert = Math.min(stack.getAmount(), availableSpace);
			if (toInsert <= 0) return stack;
			if (action.execute()) {
				stored.grow(toInsert);
				onContentsChanged();
			}
			return stack.getAmount() > toInsert ? copyWithAmount(stack, stack.getAmount() - toInsert) : GasStack.EMPTY;
		}
		return stack;
	}
	
	@Override
	public GasStack extractChemical(int tank, long amount, Action action) {
		if (tank != 0 || amount <= 0 || stored.isEmpty()) return GasStack.EMPTY;
		
		var mode = tesseract.getTransferMode();
		if (mode != BOTH && mode != EXTRACT_ONLY) return GasStack.EMPTY;
		
		long toExtract = Math.min(amount, stored.getAmount());
		GasStack extracted = copyWithAmount(stored, toExtract);
		if (action.execute()) {
			stored.shrink(toExtract);
			if (stored.isEmpty()) {
				stored = GasStack.EMPTY;
			}
			onContentsChanged();
		}
		return extracted;
	}
	
	private GasStack copyWithAmount(GasStack stack, long amount) {
		GasStack copy = stack.copy();
		copy.setAmount(amount);
		return copy;
	}
	
	protected void onContentsChanged() {
		var network = tesseract.getCachedTesseractNetwork();
		if (network != null) {
			network.setSavedData(serializeNBT());
		}
		var tesseracts = TesseractNetworks.get(tesseract.getLevel())
			.getTesseracts()
			.getOrDefault(network, new HashSet<>());
		tesseracts.forEach(Tesseract::invalidateCapabilities);
	}
}

