package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.mixin_helper.RenderOverrides;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.Predicate;

public class RegisterUnrenderableOverridesEvent extends Event implements IModBusEvent {

    public void register(EquipmentSlot slot, Item item) {
        RenderOverrides.addSkipRender(slot, item);
    }
    public void register(EquipmentSlot slot, TagKey<Item> tag) {
        RenderOverrides.addSkipRender(slot, tag);
    }
    public void register(EquipmentSlot slot, Predicate<ItemStack> predicate) {
        RenderOverrides.addSkipRender(slot, predicate);
    }

}
