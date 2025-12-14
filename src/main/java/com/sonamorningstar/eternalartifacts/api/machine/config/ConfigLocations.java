package com.sonamorningstar.eternalartifacts.api.machine.config;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public final class ConfigLocations {
	//make an event for this
	public static final Map<Class<? extends Config>, ResourceLocation> CONFIG_LOCATIONS = Map.of(
		SideConfig.class, new ResourceLocation(MODID, "side_config"),
		RedstoneConfig.class, new ResourceLocation(MODID, "redstone_config"),
		AutoTransferConfig.class, new ResourceLocation(MODID, "auto_transfer_config"),
		ToggleConfig.class, new ResourceLocation(MODID, "toggle_config"),
		ReverseToggleConfig.class, new ResourceLocation(MODID, "reverse_toggle_config"),
		BatteryBoxExportConfig.class, new ResourceLocation(MODID, "battery_box_export_config")
	);
	
	public static ResourceLocation getConfigLocation(Config config) {
		return getConfigLocation(config.getClass());
	}
	
	public static ResourceLocation getConfigLocation(Class<? extends Config> configClass) {
		return CONFIG_LOCATIONS.get(configClass);
	}
	
	public static ResourceLocation getWithSuffix(Class<? extends Config> configClass, String suffix) {
		ResourceLocation base = getConfigLocation(configClass);
		return new ResourceLocation(base.getNamespace(), base.getPath() + "_" + suffix);
	}
}
