package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BottlerMenu extends AbstractMachineMenu {
	public BottlerMenu(MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
		if (beInventory != null) {
			addSlot(new SlotItemHandler(beInventory, 0,  40, 24));
			addSlot(new SlotItemHandler(beInventory, 1,  81, 24));
			addSlot(new SlotItemHandler(beInventory, 2,  122, 24));
		}
	}
}
