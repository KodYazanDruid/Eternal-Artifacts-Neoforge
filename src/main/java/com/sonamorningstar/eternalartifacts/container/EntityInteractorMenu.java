package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class EntityInteractorMenu extends AbstractMachineMenu {
	public EntityInteractorMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
		if (beInventory != null) {
			addSlot(new SlotItemHandler(beInventory, 0, 46, 44));
			for (int i = 0; i < 8; i++) {
				int x = i % 4;
				int y = i / 4;
				addSlot(new SlotItemHandler(beInventory, i + 1, 94 + x * 18, 35 + y * 18));
			}
		}
	}
}
