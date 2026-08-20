package com.sonamorningstar.eternalartifacts.event.custom.charms;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.charm.CharmAttributes;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RegisterCharmAttributesEvent extends Event implements IModBusEvent {
    private final Set<CharmAttributes> attributes;
    private final List<Function<CharmStorage.DynamicAttributeContext, Pair<Attribute, AttributeModifier>>> dynamicAttributeProviders;

    public RegisterCharmAttributesEvent(Set<CharmAttributes> attributes, List<Function<CharmStorage.DynamicAttributeContext, Pair<Attribute, AttributeModifier>>> dynamicAttributeProviders) {
        this.attributes = attributes;
        this.dynamicAttributeProviders = dynamicAttributeProviders;
    }

    public void register(CharmAttributes attribute) {
        if (attributes.contains(attribute)) {
            throw new IllegalArgumentException("Charm attribute already registered for this item or tag: " + attribute.getHolder());
        }
        attributes.add(attribute);
    }
    
    public void registerDynamicAttributeProvider(Function<CharmStorage.DynamicAttributeContext, Pair<Attribute, AttributeModifier>> provider) {
        dynamicAttributeProviders.add(provider);
    }
}
