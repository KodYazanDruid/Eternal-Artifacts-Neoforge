package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import com.sonamorningstar.eternalartifacts.api.machine.config.ConfigLocations;
import com.sonamorningstar.eternalartifacts.event.custom.MachineConfigurationAddEvent;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("unchecked")
public class MachineConfiguration {
    private final Map<ResourceLocation, Config> configs = new HashMap<>();
    
    public <T extends Config> void add(T component) {
        var event = new MachineConfigurationAddEvent(component);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            var cfg = event.getConfig();
            configs.putIfAbsent(cfg.getLocation(), cfg);
        }
    }
    
    public <T extends Config> @Nullable T get(ResourceLocation rl) {
        return (T) configs.get(rl);
    }
    
    public <T extends Config> @Nullable T get(Class<T> cls, String suffix) {
        return (T) configs.get(ConfigLocations.getWithSuffix(cls, suffix));
    }
    
    public <T extends Config> @Nullable T get(Class<T> configClass) {
        return (T) configs.get(ConfigLocations.getConfigLocation(configClass));
    }
    
    public void unregister(ResourceLocation rl) {
        configs.remove(rl);
    }
    
    public <T extends Config> void unregisterAll(Class<T> configClass) {
		configs.entrySet().removeIf(next -> configClass == next.getValue().getClass());
    }
    
    public void save(CompoundTag tag) {
        configs.values().forEach(c -> c.save(tag));
    }
    
    public void load(CompoundTag tag) {
        configs.values().forEach(c -> c.load(tag));
    }
}
