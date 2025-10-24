package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Setter
@Getter
public class ItemTagEntry implements ItemFilterEntry {
	private TagKey<Item> tag;
	private boolean isWhitelist = true;
	
	public ItemTagEntry(TagKey<Item> tag) {
		this.tag = tag;
	}
	
	@Override
	public boolean matches(ItemStack stack) {
		return stack.is(tag);
	}
	
	@Override
	public boolean isEmpty() {
		return tag == null;
	}
	
	@Override
	public Component getDisplayName() {
		return Component.literal("#" + tag.location());
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tagData = new CompoundTag();
		tagData.putString("Type", "Tag");
		tagData.putString("TagKey", tag.location().toString());
		tagData.putBoolean("IsWhitelist", isWhitelist);
		return tagData;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tagData) {
		this.tag = TagKey.create(Registries.ITEM, new ResourceLocation(tagData.getString("TagKey")));
		this.isWhitelist = tagData.getBoolean("IsWhitelist");
	}
	
	@Override
	public String toString() {
		return "ItemTagEntry{" +
			"tag=" + tag +
			", isWhitelist=" + isWhitelist +
			'}';
	}
	
	@Override
	public boolean isIgnoreNBT() {
		return true; // Tag entries always ignore NBT
	}
	
	@Override
	public void setIgnoreNBT(boolean isNbtTolerant) {
	
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buff) {
		buff.writeUtf("Tag");
		buff.writeUtf(tag.location().toString());
		buff.writeBoolean(true); // Always ignore NBT for Tag entries
		buff.writeBoolean(isWhitelist);
	}
	
	@Override
	public void fromNetwork(FriendlyByteBuf buff) {
		this.tag = TagKey.create(Registries.ITEM, new ResourceLocation(buff.readUtf()));
		buff.readBoolean(); // Ignore NBT is not applicable for Tag entries
		this.isWhitelist = buff.readBoolean();
	}
}

