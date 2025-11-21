package com.sonamorningstar.eternalartifacts.api.machine.config;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Getter
@Setter
public class ToggleConfig implements Config {
	private boolean enabled;
	private final String tagKey;
	public final ResourceLocation location;
	
	
	public ToggleConfig(String suffix) {
		this.location = ConfigLocations.getWithSuffix(this.getClass(), suffix);
		this.tagKey = location.getPath();
	}
	
	@Override
	public void save(CompoundTag tag) {
		CompoundTag toggleTag = new CompoundTag();
		toggleTag.putBoolean("Enabled", enabled);
		tag.put(tagKey, toggleTag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		CompoundTag toggleTag = tag.getCompound(tagKey);
		enabled = toggleTag.getBoolean("Enabled");
	}
	
	@Override
	public void writeToServer(FriendlyByteBuf buf) {
		buf.writeBoolean(enabled);
	}
	
	@Override
	public void readFromClient(FriendlyByteBuf buf) {
		enabled = buf.readBoolean();
	}
	
	@Override
	public String toString() {
		return "ToggleConfig{" +
			"tagKey='" + tagKey + '\'' +
			", enabled=" + enabled +
			'}';
	}
}
