package com.sonamorningstar.eternalartifacts.event.custom.charms;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.logging.Level;

@Getter
public class CharmTickEvent extends PlayerEvent implements ICancellableEvent {
    private final ItemStack charm;
    private final int slot;
    public CharmTickEvent(Player e, ItemStack charm, int slot) {
        super(e);
        this.charm = charm;
        this.slot = slot;
    }
}
