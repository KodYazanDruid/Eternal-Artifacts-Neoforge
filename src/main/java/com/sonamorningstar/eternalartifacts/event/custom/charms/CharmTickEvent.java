package com.sonamorningstar.eternalartifacts.event.custom.charms;

import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@Getter
public class CharmTickEvent extends LivingEvent implements ICancellableEvent {
    private final ItemStack charm;
    private final int slot;
    public CharmTickEvent(LivingEntity e, ItemStack charm, int slot) {
        super(e);
        this.charm = charm;
        this.slot = slot;
    }
}
