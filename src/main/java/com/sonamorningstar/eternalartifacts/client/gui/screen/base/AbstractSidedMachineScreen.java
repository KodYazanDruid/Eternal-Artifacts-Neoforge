package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractSidedMachineScreen<T extends AbstractMachineMenu> extends AbstractMachineScreen<T> {
    public AbstractSidedMachineScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
