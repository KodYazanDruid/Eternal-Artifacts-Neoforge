package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AlchemicalBrewerMenu extends AbstractMachineMenu {
	public AlchemicalBrewerMenu(MenuType<?> type, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(type, id, inv, entity, data);
		if (beInventory != null) {
			addSlot(new SlotItemHandler(beInventory, 0,  60, 44));
		}
	}
}
