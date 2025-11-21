package com.sonamorningstar.eternalartifacts.api.machine.config;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Setter
@Getter
public class ReverseToggleConfig implements Config {
	private boolean disabled;
	private final String tagKey;
	public final ResourceLocation location;
	
	public ReverseToggleConfig(String suffix) {
		this.location = ConfigLocations.getWithSuffix(this.getClass(), suffix);
		this.tagKey = location.getPath();
	}
	
	@Override
	public void save(CompoundTag tag) {
		CompoundTag toggleTag = new CompoundTag();
		toggleTag.putBoolean("Disabled", disabled);
		tag.put(tagKey, toggleTag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		CompoundTag toggleTag = tag.getCompound(tagKey);
		disabled = toggleTag.getBoolean("Disabled");
	}
	
	@Override
	public void writeToServer(FriendlyByteBuf buf) {
		buf.writeBoolean(disabled);
	}
	
	@Override
	public void readFromClient(FriendlyByteBuf buf) {
		disabled = buf.readBoolean();
	}
	
	@Override
	public String toString() {
		return "ReverseToggleConfig{" +
			"tagKey='" + tagKey + '\'' +
			", disabled=" + disabled +
			'}';
	}
}
