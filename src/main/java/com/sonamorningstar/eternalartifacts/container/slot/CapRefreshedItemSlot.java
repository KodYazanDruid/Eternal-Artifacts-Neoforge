package com.sonamorningstar.eternalartifacts.container.slot;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Supplier;

public class CapRefreshedItemSlot extends SlotItemHandler {
	private final Supplier<IItemHandler> handlerSupplier;
	
	public CapRefreshedItemSlot(Supplier<IItemHandler> handlerSupplier, int index, int x, int y) {
		super(handlerSupplier.get(), index, x, y);
		this.handlerSupplier = handlerSupplier;
	}
	
	@Override
	public IItemHandler getItemHandler() {
		return handlerSupplier.get();
	}
}