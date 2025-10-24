package com.sonamorningstar.eternalartifacts.content.recipe.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class InventoryRemoteSlotHandler extends SlotItemHandler {
	public InventoryRemoteSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
	public void set(ItemStack stack) {
		super.set(stack);
	}
	
	@Override
	public void initialize(ItemStack stack) {
		super.initialize(stack);
	}
}
