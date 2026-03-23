package com.sonamorningstar.eternalartifacts.api.machine.config;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Getter
@Setter
public class RedstoneOutputThreshold implements Config {
	public enum ThresholdMode {BELOW, BELOW_EQUAL, ABOVE, ABOVE_EQUAL, EXACT, IGNORE}
	
	private final String tagKey;
	public final ResourceLocation location;
	
	public int threshold;
	public ThresholdMode mode = ThresholdMode.IGNORE;
	
	public RedstoneOutputThreshold() {
		this.location = ConfigLocations.getConfigLocation(this.getClass());
		this.tagKey = location.getPath();
	}
	
	public ThresholdMode cycleNextMode() {
		switch (mode) {
			case BELOW -> mode = ThresholdMode.BELOW_EQUAL;
			case BELOW_EQUAL -> mode = ThresholdMode.ABOVE;
			case ABOVE -> mode = ThresholdMode.ABOVE_EQUAL;
			case ABOVE_EQUAL -> mode = ThresholdMode.EXACT;
			case EXACT -> mode = ThresholdMode.IGNORE;
			case IGNORE -> mode = ThresholdMode.BELOW;
		}
		return mode;
	}
	
	@Override
	public void save(CompoundTag tag) {
		tag.putInt("Threshold", threshold);
		tag.putInt("Mode", mode.ordinal());
	}
	
	@Override
	public void load(CompoundTag tag) {
		threshold = tag.getInt("Threshold");
		mode = ThresholdMode.values()[tag.getInt("Mode")];
	}
	
	@Override
	public void writeToServer(FriendlyByteBuf buf) {
		buf.writeInt(threshold);
		buf.writeVarInt(mode.ordinal());
	}
	
	@Override
	public void readFromClient(FriendlyByteBuf buf) {
		threshold = buf.readInt();
		mode = ThresholdMode.values()[buf.readVarInt()];
	}
}
