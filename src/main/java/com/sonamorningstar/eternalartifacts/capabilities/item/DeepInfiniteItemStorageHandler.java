package com.sonamorningstar.eternalartifacts.capabilities.item;

import com.sonamorningstar.eternalartifacts.api.item.InfiniteItemStack;
import com.sonamorningstar.eternalartifacts.content.block.entity.DeepItemStorageUnit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class DeepInfiniteItemStorageHandler extends DeepItemStorageHandler {
	protected InfiniteItemStack storedStack = new InfiniteItemStack(ItemStack.EMPTY, 0);
	
	public DeepInfiniteItemStorageHandler(DeepItemStorageUnit unit) {
		super(unit);
	}
	
	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		storedStack = new InfiniteItemStack(stack, stack.getCount());
	}
	
	@Override
	public int getSlots() {
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return storedStack.getStack().copyWithCount((int) Math.min(storedStack.getCount(), Integer.MAX_VALUE));
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		if (storedStack.isEmpty()) {
			if (!simulate) {
				storedStack = new InfiniteItemStack(stack, stack.getCount());
			}
			return ItemStack.EMPTY;
		}
		if (!ItemStack.isSameItemSameTags(storedStack.getStack(), stack)) {
			return stack;
		}
		if (!simulate) {
			storedStack.addCount(stack.getCount());
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (storedStack.isEmpty() || amount <= 0) return ItemStack.EMPTY;
		int toExtract = (int) Math.min(storedStack.getCount(), amount);
		ItemStack extracted = storedStack.getStack().copyWithCount(toExtract);
		if (!simulate) {
			storedStack.removeCount(toExtract);
		}
		return extracted;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		CompoundTag stackTag = new CompoundTag();
		storedStack.getStack().save(stackTag);
		stackTag.putLong("Count", storedStack.getCount());
		tag.put("StoredStack", stackTag);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		CompoundTag stackTag = nbt.getCompound("StoredStack");
		ItemStack stack = ItemStack.of(stackTag);
		long count = stackTag.getLong("Count");
		storedStack = new InfiniteItemStack(stack, count);
	}
}
