package com.sonamorningstar.eternalartifacts.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record ItemWithCount(Item item, int count) {
	public ItemStack toStack() {
		return new ItemStack(item, count);
	}
	
	public ItemStack single() {
		return new ItemStack(item, 1);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ItemWithCount that = (ItemWithCount) o;
		return count == that.count && Objects.equals(item, that.item);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(item, count);
	}
}