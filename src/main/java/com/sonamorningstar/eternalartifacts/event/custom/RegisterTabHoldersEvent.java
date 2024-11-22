package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.registrar.TabType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public class RegisterTabHoldersEvent extends Event implements IModBusEvent {
    private final Map<Item, TabType<?>> tabHolders;

    public RegisterTabHoldersEvent(Map<Item, TabType<?>> tabHolders) {
        this.tabHolders = tabHolders;
    }

    public void register(Item item, TabType<?> tab) {
        if (tabHolders.containsKey(item)) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            throw new IllegalArgumentException("Tab holder already registered for this item: " +id);
        }
        tabHolders.put(item, tab);
    }

}
