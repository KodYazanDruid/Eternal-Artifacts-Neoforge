package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@Setter
@Getter
public class ItemStackEntry implements ItemFilterEntry {
	private ItemStack filterStack;
	private boolean ignoreNBT;
	private boolean isWhitelist = true;
	
	public static final ItemStackEntry EMPTY = new ItemStackEntry(ItemStack.EMPTY, true);
	
	public ItemStackEntry(ItemStack filterStack, boolean ignoreNBT) {
		this.filterStack = filterStack;
		this.ignoreNBT = ignoreNBT;
	}
	
	@Override
	public boolean matches(ItemStack stack) {
		if (filterStack.isEmpty()) {
			return stack.isEmpty();
		}
		return ignoreNBT
			? stack.getItem() == filterStack.getItem()
			: ItemStack.isSameItemSameTags(stack, filterStack);
	}
	
	@Override
	public boolean isEmpty() {
		return filterStack.isEmpty();
	}
	
	@Override
	public Component getDisplayName() {
		return filterStack.getHoverName();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Type", "Stack");
		tag.put("Stack", filterStack.save(new CompoundTag()));
		tag.putBoolean("IgnoreNBT", ignoreNBT);
		tag.putBoolean("IsWhitelist", isWhitelist);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.filterStack = ItemStack.of(tag.getCompound("Stack"));
		this.ignoreNBT = tag.getBoolean("IgnoreNBT");
		this.isWhitelist = tag.getBoolean("IsWhitelist");
	}
	
	@Override
	public String toString() {
		return "ItemStackEntry{" +
			"filterStack=" + filterStack +
			", ignoreNBT=" + ignoreNBT +
			", isWhitelist=" + isWhitelist +
			'}';
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buff) {
		buff.writeUtf("Stack");
		buff.writeItem(filterStack);
		buff.writeBoolean(ignoreNBT);
		buff.writeBoolean(isWhitelist);
	}
	
	@Override
	public void fromNetwork(FriendlyByteBuf buff) {
		this.filterStack = buff.readItem();
		this.ignoreNBT = buff.readBoolean();
		this.isWhitelist = buff.readBoolean();
	}
}