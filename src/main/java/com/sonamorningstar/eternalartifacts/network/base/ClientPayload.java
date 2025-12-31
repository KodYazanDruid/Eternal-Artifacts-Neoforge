package com.sonamorningstar.eternalartifacts.network.base;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Interface for client-bound packets with simplified handling.
 */
public interface ClientPayload extends CustomPacketPayload {
    
    /**
     * Handle packet on client side
     */
    @OnlyIn(Dist.CLIENT)
    void handleOnClient(Minecraft minecraft);
    
    /**
     * Default handle implementation
     */
    default void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() ->
            handleOnClient(Minecraft.getInstance())
        );
    }
}

