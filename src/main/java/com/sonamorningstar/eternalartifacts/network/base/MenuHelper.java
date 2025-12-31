package com.sonamorningstar.eternalartifacts.network.base;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helper class for validating and handling menu-related operations in packets
 */
public final class MenuHelper {
    
    private MenuHelper() {}
    
    /**
     * Validate and handle menu with type checking
     */
    public static <T extends AbstractContainerMenu> void handleMenu(
        ServerPlayer player,
        int containerId,
        Class<T> menuClass,
        BiConsumer<ServerPlayer, T> handler
    ) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId == containerId && menuClass.isInstance(menu)) {
            handler.accept(player, menuClass.cast(menu));
        }
    }
    
    /**
     * Validate and handle menu without player parameter
     */
    public static <T extends AbstractContainerMenu> void handleMenu(
        ServerPlayer player,
        int containerId,
        Class<T> menuClass,
        Consumer<T> handler
    ) {
        handleMenu(player, containerId, menuClass, (p, m) -> handler.accept(m));
    }
}

