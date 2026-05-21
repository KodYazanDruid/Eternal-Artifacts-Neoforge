package com.sonamorningstar.eternalartifacts.capabilities.item;

import net.minecraft.world.item.ItemStack;
import org.antlr.v4.runtime.misc.IntegerList;

public class MultiblockPartItemHandler<H extends ModItemStorage> extends ModItemStorage {
	private final IntegerList allowedSlots;
	public MultiblockPartItemHandler(H parent, IntegerList allowedSlots) {
		super(parent.getSlots());
		this.allowedSlots = allowedSlots;
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return allowedSlots.contains(slot) ? super.insertItem(slot, stack, simulate) : stack;
	}
	
	@Override
	public ItemStack insertItemForced(int slot, ItemStack stack, boolean simulate) {
		return allowedSlots.contains(slot) ? super.insertItemForced(slot, stack, simulate) : stack;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return allowedSlots.contains(slot) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
	}
}
