package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.MultiFurnace;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class MultiFurnaceMenu extends AbstractMachineMenu {
	
	public MultiFurnaceMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
	}
	
	@Override
	public boolean clickMenuButton(Player pPlayer, int pId) {
		if (blockEntity instanceof MultiFurnace<?> furnace) {
			furnace.setRecipeTypeId((short) pId);
			return true;
		}
		return false;
	}
}
