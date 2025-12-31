package com.sonamorningstar.eternalartifacts.network.base;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Base class for server-bound packets.
 * Automatically handles player extraction and async execution.
 */
public abstract class AbstractServerPayload implements CustomPacketPayload {
    
    /**
     * Write packet data to buffer
     */
    public abstract void write(FriendlyByteBuf buffer);
    
    /**
     * Handle packet on server side
     */
    protected abstract void handleOnServer(ServerPlayer player);
    
    /**
     * Final handle method that extracts player and runs async
     */
    public final void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() ->
            ctx.player().ifPresent(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    handleOnServer(serverPlayer);
                }
            })
        );
    }
}

