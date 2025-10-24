package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class HarvesterMenu extends AbstractMachineMenu {
	public HarvesterMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
		
		if (getBeInventory() != null) {
			var itemCap = getBeInventory();
			for (int i = 0; i < 9; i++) {
				addSlot(new SlotItemHandler(itemCap, i, 56 + (i % 3) * 18,  18 + (i / 3) * 18));
			}
			for (int i = 9; i < 18; i++) {
				final int fixedI = i - 9;
				addSlot(new SlotItemHandler(itemCap, i, 116 + (fixedI % 3) * 18,  18 + (fixedI / 3) * 18));
			}
			addSlot(new SlotItemHandler(itemCap, 18, 159, 58));
		}
		
		addPlayerInventoryAndHotbar(inv, 8, 66);
	}
}
