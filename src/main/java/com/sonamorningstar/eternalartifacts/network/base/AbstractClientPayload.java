package com.sonamorningstar.eternalartifacts.network.base;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Base class for client-bound packets.
 * Automatically handles client-side execution.
 */
public abstract class AbstractClientPayload implements CustomPacketPayload {
    
    /**
     * Write packet data to buffer
     */
    public abstract void write(FriendlyByteBuf buffer);
    
    /**
     * Handle packet on client side
     */
    @OnlyIn(Dist.CLIENT)
    protected abstract void handleOnClient(Minecraft minecraft);
    
    /**
     * Final handle method that runs on client
     */
    public final void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() ->
            handleOnClient(Minecraft.getInstance())
        );
    }
}

