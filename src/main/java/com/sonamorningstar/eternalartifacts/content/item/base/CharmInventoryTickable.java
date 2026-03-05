package com.sonamorningstar.eternalartifacts.content.item.base;

import net.minecraft.world.item.ItemStack;

public interface CharmInventoryTickable {
	
	default int getTickedSlot() {
		return -1;
	}
	
	default boolean shouldTick(ItemStack charm, int charmSlot) {
		return true;
	}
	
	default boolean isSelected(ItemStack charm, int charmSlot) {
		return true;
	}
}
