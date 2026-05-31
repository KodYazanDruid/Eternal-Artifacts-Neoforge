package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.DynamoMenu;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ItemDynamoMenu extends DynamoMenu {

	public ItemDynamoMenu(MenuType<?> type, int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
		super(type, pContainerId, inv, entity, data);
		if (beInventory != null) addSlot(new SlotItemHandler(beInventory, 0, 81, 27));
	}
	
}
