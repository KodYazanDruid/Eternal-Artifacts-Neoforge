package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class MobLiquifierMenu extends AbstractMachineMenu {
	public MobLiquifierMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
	}
}
