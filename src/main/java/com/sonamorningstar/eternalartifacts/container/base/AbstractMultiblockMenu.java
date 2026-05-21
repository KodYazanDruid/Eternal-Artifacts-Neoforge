package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMultiblockMenu extends AbstractMachineMenu {
	protected final AbstractMultiblockBlockEntity multiblockEntity;
	public AbstractMultiblockMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, AbstractMultiblockBlockEntity multiblockEntity, ContainerData data) {
		super(menuType, id, inv, multiblockEntity, data);
		this.multiblockEntity = multiblockEntity;
	}
}
