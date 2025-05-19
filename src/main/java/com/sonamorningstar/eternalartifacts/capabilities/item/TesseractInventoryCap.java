package com.sonamorningstar.eternalartifacts.capabilities.item;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import static com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract.TransferMode.*;

public class TesseractInventoryCap extends ItemStackHandler {
	private final Tesseract tesseract;
	
	public TesseractInventoryCap(Tesseract tesseract) {
		super(9);
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
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		var network = tesseract.getCachedNetwork();
		if (network != null) {
			network.setSavedData(serializeNBT());
		}
		TesseractNetworks.get(tesseract.getLevel()).getTesseracts().get(network).forEach(Tesseract::invalidateCapabilities);
	}
	
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return tesseract.getTransferMode() != NONE && super.isItemValid(slot, stack);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == INSERT_ONLY) return super.insertItem(slot, stack, simulate);
		else return stack;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		var mode = tesseract.getTransferMode();
		if (mode == BOTH || mode == EXTRACT_ONLY) return super.extractItem(slot, amount, simulate);
		else return ItemStack.EMPTY;
	}
}
