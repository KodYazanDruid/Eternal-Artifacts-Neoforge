package com.sonamorningstar.eternalartifacts.api.item;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@Getter
public class InfiniteItemStack {
	private final ItemStack stack;
	private long count;
	
	public InfiniteItemStack(ItemStack stack, long count) {
		this.stack = stack.copyWithCount(1);
		this.count = count;
	}
	
	public void addCount(long amount) {
		this.count += amount;
	}
	
	public void removeCount(long amount) {
		this.count -= amount;
		if (this.count < 0) this.count = 0;
	}
	
	public boolean isEmpty() {
		return stack.isEmpty() || this.count <= 0;
	}
}
