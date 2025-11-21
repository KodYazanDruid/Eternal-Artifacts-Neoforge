package com.sonamorningstar.eternalartifacts.api.machine.config;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Getter
@Setter
public class BatteryBoxExportConfig implements Config {
	public enum ExportMode {EMPTY, FULL, PERCENTAGE_BELOW, PERCENTAGE_ABOVE, PERCENTAGE_EXACT}

	private final String tagKey;
	public final ResourceLocation location;
	
	public float percentage = 50.0F;
	public ExportMode exportMode = ExportMode.EMPTY;
	
	public BatteryBoxExportConfig() {
		this.location = ConfigLocations.getConfigLocation(this.getClass());
		this.tagKey = location.getPath();
	}
	
	public ExportMode cycleNextMode() {
		switch (exportMode) {
			case FULL -> exportMode = ExportMode.EMPTY;
			case EMPTY -> exportMode = ExportMode.PERCENTAGE_BELOW;
			case PERCENTAGE_BELOW -> exportMode = ExportMode.PERCENTAGE_ABOVE;
			case PERCENTAGE_ABOVE -> exportMode = ExportMode.PERCENTAGE_EXACT;
			case PERCENTAGE_EXACT -> exportMode = ExportMode.FULL;
		}
		return exportMode;
	}
	
	@Override
	public void save(CompoundTag tag) {
		CompoundTag configTag = new CompoundTag();
		configTag.putFloat("Percentage", percentage);
		configTag.putInt("ExportMode", exportMode.ordinal());
		tag.put(tagKey, configTag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		CompoundTag configTag = tag.getCompound(tagKey);
		percentage = configTag.getFloat("Percentage");
		exportMode = ExportMode.values()[configTag.getInt("ExportMode")];
	}
	
	@Override
	public void writeToServer(FriendlyByteBuf buf) {
		buf.writeFloat(percentage);
		buf.writeVarInt(exportMode.ordinal());
	}
	
	@Override
	public void readFromClient(FriendlyByteBuf buf) {
		percentage = buf.readFloat();
		exportMode = ExportMode.values()[buf.readVarInt()];
	}
}
