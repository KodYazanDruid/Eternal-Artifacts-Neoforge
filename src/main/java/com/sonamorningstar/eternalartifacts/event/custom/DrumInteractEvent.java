package com.sonamorningstar.eternalartifacts.event.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
@RequiredArgsConstructor
public class DrumInteractEvent extends Event implements ICancellableEvent {
    private final FluidStack content;
    private final Player player;
    private final BlockState state;
    @Setter
    private int fuseTime = 0;
    @Setter
    private float radius = 4.0f;

    public void setDefaultFuseTime() { fuseTime = 80; }
}
