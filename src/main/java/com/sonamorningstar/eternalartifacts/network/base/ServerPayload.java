package com.sonamorningstar.eternalartifacts.network.base;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Interface for server-bound packets with simplified handling.
 */
public interface ServerPayload extends CustomPacketPayload {
    
    /**
     * Handle packet on server side with player
     */
    void handleOnServer(ServerPlayer player);
    
    /**
     * Default handle implementation
     */
    default void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() ->
            ctx.player().ifPresent(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    handleOnServer(serverPlayer);
                }
            })
        );
    }
}

