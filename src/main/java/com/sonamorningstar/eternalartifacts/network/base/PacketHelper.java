package com.sonamorningstar.eternalartifacts.network.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Utility class for common packet operations
 */
public final class PacketHelper {
    
    private PacketHelper() {}
    
    // Container ID helpers
    public static void writeContainerId(FriendlyByteBuf buffer, int containerId) {
        buffer.writeByte(containerId);
    }
    
    public static int readContainerId(FriendlyByteBuf buffer) {
        return buffer.readByte();
    }
    
    // Position helpers
    public static void writePos(FriendlyByteBuf buffer, BlockPos pos) {
        buffer.writeBlockPos(pos);
    }
    
    public static BlockPos readPos(FriendlyByteBuf buffer) {
        return buffer.readBlockPos();
    }
    
    // Entity ID helpers
    public static void writeEntityId(FriendlyByteBuf buffer, int entityId) {
        buffer.writeVarInt(entityId);
    }
    
    public static int readEntityId(FriendlyByteBuf buffer) {
        return buffer.readVarInt();
    }
    
    // Index helpers
    public static void writeIndex(FriendlyByteBuf buffer, int index) {
        buffer.writeVarInt(index);
    }
    
    public static int readIndex(FriendlyByteBuf buffer) {
        return buffer.readVarInt();
    }
}

