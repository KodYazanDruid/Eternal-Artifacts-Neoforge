package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Setter
@Getter
public class BlockTagEntry implements BlockFilterEntry {
	private TagKey<Block> tag;
	private boolean isWhitelist = true;
	
	public BlockTagEntry(TagKey<Block> tag) {
		this.tag = tag;
	}
	
	@Override
	public boolean matches(BlockState state) {
		return state.is(tag);
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
	public boolean isIgnoreNBT() {
		return true; // Tag filtreleri property'leri yoksayar
	}
	
	@Override
	public void setIgnoreNBT(boolean ignoreNBT) {
		// Tag filtrelerinde property kontrolü yoktur
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
		this.tag = TagKey.create(Registries.BLOCK, new ResourceLocation(tagData.getString("TagKey")));
		this.isWhitelist = tagData.getBoolean("IsWhitelist");
	}
	
	@Override
	public String toString() {
		return "BlockTagEntry{" +
			"tag=" + tag +
			", isWhitelist=" + isWhitelist +
			'}';
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buff) {
		buff.writeUtf("Tag");
		buff.writeResourceLocation(tag.location());
		buff.writeBoolean(isWhitelist);
	}
	
	@Override
	public void fromNetwork(FriendlyByteBuf buff) {
		this.tag = TagKey.create(Registries.BLOCK, buff.readResourceLocation());
		this.isWhitelist = buff.readBoolean();
	}
}

