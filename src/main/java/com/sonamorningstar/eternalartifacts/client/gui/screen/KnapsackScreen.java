package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.KnapsackMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class KnapsackScreen extends AbstractModContainerScreen<KnapsackMenu> {
    public KnapsackScreen(KnapsackMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
}
