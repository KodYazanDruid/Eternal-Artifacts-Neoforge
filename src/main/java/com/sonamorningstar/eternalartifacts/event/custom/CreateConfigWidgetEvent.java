package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import com.sonamorningstar.eternalartifacts.api.machine.config.ConfigLocations;
import com.sonamorningstar.eternalartifacts.client.config.ConfigUIRegistry;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class CreateConfigWidgetEvent extends Event implements IModBusEvent {
	
	public <T extends Config> void register(Class<T> configClass, ConfigUIRegistry.ConfigUIFactory<T> factory) {
		ConfigUIRegistry.register(configClass, factory);
	}
	
	public <T extends Config> void register(Class<T> configClass, String subType, ConfigUIRegistry.ConfigUIFactory<T> factory) {
		ConfigUIRegistry.register(ConfigLocations.getWithSuffix(configClass, subType), factory);
	}
}
