package com.sonamorningstar.eternalartifacts.network.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Base class for block entity-related server packets.
 * Automatically validates and casts to correct block entity type.
 */
public abstract class AbstractBlockEntityPayload<T extends BlockEntity> extends AbstractServerPayload {
    
    protected final BlockPos pos;
    
    protected AbstractBlockEntityPayload(BlockPos pos) {
        this.pos = pos;
    }
    
    /**
     * Get the expected block entity class
     */
    protected abstract Class<T> getBlockEntityClass();
    
    /**
     * Handle packet with validated block entity
     */
    protected abstract void handleBlockEntity(ServerPlayer player, T blockEntity);
    
    @Override
    protected final void handleOnServer(ServerPlayer player) {
        BlockEntity entity = player.level().getBlockEntity(pos);
        if (entity != null && getBlockEntityClass().isInstance(entity)) {
            handleBlockEntity(player, getBlockEntityClass().cast(entity));
        }
    }
    
    /**
     * Helper method to write position
     */
    protected void writePos(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }
    
    /**
     * Helper method to read position
     */
    protected static BlockPos readPos(FriendlyByteBuf buffer) {
        return buffer.readBlockPos();
    }
}

