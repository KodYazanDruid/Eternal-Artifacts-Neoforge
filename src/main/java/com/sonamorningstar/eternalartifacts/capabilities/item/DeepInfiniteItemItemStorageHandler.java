package com.sonamorningstar.eternalartifacts.capabilities.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DeepInfiniteItemItemStorageHandler extends ItemStackHandler {
	private final ItemStack dsuStack;
	
	public DeepInfiniteItemItemStorageHandler(ItemStack dsuStack) {
		super(1);
		this.dsuStack = dsuStack;
		if (dsuStack.hasTag() && dsuStack.getTag().contains("Inventory")) {
			CompoundTag tag = dsuStack.getTag().getCompound("Inventory");
			deserializeNBT(tag);
		}
	}
	
	@Override
	public int getSlotLimit(int slot) {
		return Integer.MAX_VALUE;
	}
	
	@Override
	protected int getStackLimit(int slot, ItemStack stack) {
		return Integer.MAX_VALUE;
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		CompoundTag tag = serializeNBT();
		dsuStack.getOrCreateTag().put("Inventory", tag);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				saveBiggerStack(stacks.get(i), itemTag);
				nbtTagList.add(itemTag);
			}
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);
		nbt.putInt("Size", stacks.size());
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");
			
			if (slot >= 0 && slot < stacks.size()) {
				stacks.set(slot, loadBiggerStack(itemTags));
			}
		}
		onLoad();
	}
	
	private CompoundTag saveBiggerStack(ItemStack stack, CompoundTag nbt) {
		stack.save(nbt);
		nbt.putInt("RealCount", stack.getCount());
		return nbt;
	}
	private ItemStack loadBiggerStack(CompoundTag nbt) {
		ItemStack stack = ItemStack.of(nbt);
		int count = nbt.getInt("RealCount");
		stack.setCount(count);
		return stack;
	}
}
