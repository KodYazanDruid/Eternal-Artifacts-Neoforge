package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class AdvancedCrafterMenu extends AbstractMachineMenu {
	public AdvancedCrafterMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
		if (getBeInventory() != null) {
			var itemCap = getBeInventory();
			for (int i = 0; i < 9; i++) {
				addSlot(new SlotItemHandler(itemCap, i, 44 + (i % 3) * 18,  18 + (i / 3) * 18));
			}
			addSlot(new SlotItemHandler(itemCap, 9, 126, 36));
			addSlot(new SlotItemHandler(itemCap, 10, 104, 16));
		}
		outputSlots.add(9);
	}
	
	@Override
	public boolean clickMenuButton(Player pPlayer, int pId) {
		return super.clickMenuButton(pPlayer, pId);
	}
}
