package com.sonamorningstar.eternalartifacts.client.config;

import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import com.sonamorningstar.eternalartifacts.api.machine.config.ConfigLocations;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ConfigUIRegistry {
	private static final Map<ResourceLocation, ConfigUIFactory<?>> RL_MAP = new HashMap<>();
	private static final Map<Class<? extends Config>, ConfigUIFactory<?>> CLS_MAP = new HashMap<>();
	
	public static <T extends Config> void register(ResourceLocation location, ConfigUIFactory<T> factory) {
		RL_MAP.put(location, factory);
	}
	
	public static <T extends Config> void register(Class<T> configClass, ConfigUIFactory<T> factory) {
		CLS_MAP.put(configClass, factory);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Config> ConfigUIFactory<T> get(Config config) {
		ConfigUIFactory<T> factory = (ConfigUIFactory<T>) RL_MAP.get(config.getLocation());
		if (factory == null) {
			factory = ((ConfigUIFactory<T>) CLS_MAP.get(config.getClass()));
		}
		return factory;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Config> ConfigUIFactory<T> get(ResourceLocation location) {
		return (ConfigUIFactory<T>) RL_MAP.get(location);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Config> ConfigUIFactory<T> get(Class<T> configClass) {
		return (ConfigUIFactory<T>) CLS_MAP.get(configClass);
	}
	
	public static class ConfigUIContext {
		public final SimpleDraggablePanel panel;
		public final AbstractModContainerScreen<?> screen;
		public final AbstractModContainerMenu menu;
		
		public ConfigUIContext(SimpleDraggablePanel panel, AbstractModContainerScreen<?> screen) {
			this.panel = panel;
			this.screen = screen;
			this.menu = screen.getMenu();
		}
	}
	
	@FunctionalInterface
	public interface ConfigUIFactory<T extends Config> {
		void createWidgets(T config, ConfigUIContext ctx);
	}
}
