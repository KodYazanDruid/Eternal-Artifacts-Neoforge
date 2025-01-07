package com.sonamorningstar.eternalartifacts.event.custom.charms;

import com.sonamorningstar.eternalartifacts.api.charm.CharmAttributes;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Set;

public class RegisterCharmAttributesEvent extends Event implements IModBusEvent {
    private final Set<CharmAttributes> attributes;

    public RegisterCharmAttributesEvent(Set<CharmAttributes> attributes) {
        this.attributes = attributes;
    }

    public void register(CharmAttributes attribute) {
        if (attributes.contains(attribute)) {
            throw new IllegalArgumentException("Charm attribute already registered for this item or tag: " + attribute.getHolder());
        }
        attributes.add(attribute);
    }
}
