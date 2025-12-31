package com.sonamorningstar.eternalartifacts.network.base;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Base class for menu-related server packets.
 * Automatically validates container ID and casts to correct menu type.
 */
public abstract class AbstractMenuPayload<T extends AbstractContainerMenu> extends AbstractServerPayload {
    
    protected final int containerId;
    
    protected AbstractMenuPayload(int containerId) {
        this.containerId = containerId;
    }
    
    /**
     * Get the expected menu class
     */
    protected abstract Class<T> getMenuClass();
    
    /**
     * Handle packet with validated menu
     */
    protected abstract void handleMenu(ServerPlayer player, T menu);
    
    @Override
    protected final void handleOnServer(ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId == containerId && getMenuClass().isInstance(menu)) {
            handleMenu(player, getMenuClass().cast(menu));
        }
    }
    
    /**
     * Helper method to write container ID
     */
    protected void writeContainerId(FriendlyByteBuf buffer) {
        buffer.writeByte(containerId);
    }
    
    /**
     * Helper method to read container ID
     */
    protected static int readContainerId(FriendlyByteBuf buffer) {
        return buffer.readByte();
    }
}

