package com.sonamorningstar.eternalartifacts.network.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helper class for validating and handling block entity-related operations in packets
 */
public final class BlockEntityHelper {
    
    private BlockEntityHelper() {}
    
    /**
     * Validate and handle block entity with type checking
     */
    public static <T extends BlockEntity> void handleBlockEntity(
        ServerPlayer player,
        BlockPos pos,
        Class<T> blockEntityClass,
        BiConsumer<ServerPlayer, T> handler
    ) {
        BlockEntity entity = player.level().getBlockEntity(pos);
        if (entity != null && blockEntityClass.isInstance(entity)) {
            handler.accept(player, blockEntityClass.cast(entity));
        }
    }
    
    /**
     * Validate and handle block entity without player parameter
     */
    public static <T extends BlockEntity> void handleBlockEntity(
        ServerPlayer player,
        BlockPos pos,
        Class<T> blockEntityClass,
        Consumer<T> handler
    ) {
        handleBlockEntity(player, pos, blockEntityClass, (p, be) -> handler.accept(be));
    }
}

