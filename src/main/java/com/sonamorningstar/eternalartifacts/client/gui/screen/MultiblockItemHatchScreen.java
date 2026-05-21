package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMultiblockScreen;
import com.sonamorningstar.eternalartifacts.container.MultiblockItemHatchMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MultiblockItemHatchScreen extends AbstractMultiblockScreen<MultiblockItemHatchMenu> {
	public MultiblockItemHatchScreen(MultiblockItemHatchMenu menu, Inventory pPlayerInventory, Component pTitle) {
		super(menu, pPlayerInventory, pTitle);
	}
}
