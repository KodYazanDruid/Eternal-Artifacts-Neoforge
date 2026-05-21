package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMultiblockMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AbstractMultiblockScreen<M extends AbstractMultiblockMenu> extends AbstractMachineScreen<M>{
	public AbstractMultiblockScreen(M menu, Inventory pPlayerInventory, Component pTitle) {
		super(menu, pPlayerInventory, pTitle);
	}
}
