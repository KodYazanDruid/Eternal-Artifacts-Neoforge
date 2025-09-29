package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.container.ElectricFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ElectricFurnaceScreen extends MultiFurnaceScreen<ElectricFurnaceMenu>{
	public ElectricFurnaceScreen(ElectricFurnaceMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
}
