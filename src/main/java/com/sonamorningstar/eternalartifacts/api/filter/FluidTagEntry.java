package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

@Setter
@Getter
public class FluidTagEntry implements FluidFilterEntry {
	private TagKey<Fluid> tag;
	private boolean isWhitelist = true;
	
	public FluidTagEntry(TagKey<Fluid> tag) {
		this.tag = tag;
	}
	
	@Override
	public boolean matches(FluidStack stack) {
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
	public void deserializeNBT(CompoundTag nbt) {
		this.tag = TagKey.create(Registries.FLUID, new ResourceLocation(nbt.getString("TagKey")));
		this.isWhitelist = nbt.getBoolean("IsWhitelist");
	}
	
	@Override
	public String toString() {
		return "FluidTagEntry{" +
			"tag=" + tag +
			", isWhitelist=" + isWhitelist +
			'}';
	}
	
	@Override
	public boolean isIgnoreNBT() {
		return true;
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
		this.tag = TagKey.create(Registries.FLUID, new ResourceLocation(buff.readUtf()));
		buff.readBoolean();
		this.isWhitelist = buff.readBoolean();
	}
}
