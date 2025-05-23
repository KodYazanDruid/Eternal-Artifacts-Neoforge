package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.ElectricFurnace;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceMenu extends AbstractMachineMenu {
	public ElectricFurnaceMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
		if (beInventory != null) {
			addSlot(new SlotItemHandler(beInventory, 0, 61, 40));
			addSlot(new SlotItemHandler(beInventory, 1, 107, 40));
		}
	}
	
	@Override
	public boolean clickMenuButton(Player pPlayer, int pId) {
		if (blockEntity instanceof ElectricFurnace furnace) {
			furnace.setRecipeTypeId((short) pId);
			return true;
		}
		return false;
	}
}
