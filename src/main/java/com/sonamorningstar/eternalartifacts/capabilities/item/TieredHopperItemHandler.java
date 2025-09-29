package com.sonamorningstar.eternalartifacts.capabilities.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.neoforged.neoforge.items.VanillaHopperItemHandler;

public class TieredHopperItemHandler extends VanillaHopperItemHandler {
	private final HopperBlockEntity hopper;
	private final int baseCooldown;
	public TieredHopperItemHandler(HopperBlockEntity hopper, int baseCooldown) {
		super(hopper);
		this.hopper = hopper;
		this.baseCooldown = baseCooldown;
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (simulate) {
			return super.insertItem(slot, stack, true);
		} else {
			boolean wasEmpty = getInv().isEmpty();
			
			int originalStackSize = stack.getCount();
			stack = super.insertItem(slot, stack, false);
			
			if (wasEmpty && originalStackSize > stack.getCount()) {
				if (!hopper.isOnCustomCooldown()) {
					hopper.setCooldown(baseCooldown);
				}
			}
			
			return stack;
		}
	}
}
