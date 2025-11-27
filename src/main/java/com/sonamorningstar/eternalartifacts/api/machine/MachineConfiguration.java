package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import com.sonamorningstar.eternalartifacts.api.machine.config.ConfigLocations;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class MachineConfiguration {
    private final Map<ResourceLocation, Config> configs = new HashMap<>();
    
    public <T extends Config> void add(T component) {
        configs.putIfAbsent(component.getLocation(), component);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Config> @Nullable T get(ResourceLocation rl) {
        return (T) configs.get(rl);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Config> @Nullable T get(Class<T> configClass) {
        return (T) configs.get(ConfigLocations.getConfigLocation(configClass));
    }
    
    public <T extends Config> void unregister(ResourceLocation rl) {
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
