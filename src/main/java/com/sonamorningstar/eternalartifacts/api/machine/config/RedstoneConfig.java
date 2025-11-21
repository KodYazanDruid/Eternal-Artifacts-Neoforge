package com.sonamorningstar.eternalartifacts.api.machine.config;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Setter
@Getter
public class RedstoneConfig implements Config {
	public enum Mode { IGNORE, HIGH, LOW }
	private Mode mode = Mode.IGNORE;
	private final String tagKey;
	public final ResourceLocation location;
	
	public RedstoneConfig() {
		this.location = ConfigLocations.getConfigLocation(this.getClass());
		this.tagKey = location.getPath();
	}
	
	public Mode cycleMode() {
		switch (mode) {
			case IGNORE -> mode = Mode.HIGH;
			case HIGH -> mode = Mode.LOW;
			case LOW -> mode = Mode.IGNORE;
		}
		return mode;
	}
	
	@Override
	public void save(CompoundTag tag) {
		CompoundTag redstoneTag = new CompoundTag();
		redstoneTag.putInt("RedstoneMode", mode.ordinal());
		tag.put(tagKey, redstoneTag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		CompoundTag redstoneTag = tag.getCompound(tagKey);
		mode = Mode.values()[redstoneTag.getInt("RedstoneMode")];
	}
	
	@Override
	public void writeToServer(FriendlyByteBuf buf) {
		buf.writeEnum(mode);
	}
	
	@Override
	public void readFromClient(FriendlyByteBuf buf) {
		mode = buf.readEnum(Mode.class);
	}
	
	@Override
	public String toString() {
		return "RedstoneConfig{" +
			"mode=" + mode +
			", tagKey='" + tagKey + '\'' +
			'}';
	}
}