package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.item.base.EnergyConsumerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Equipable;
import org.jetbrains.annotations.Nullable;

public class PortableFurnaceItem extends EnergyConsumerItem implements Equipable, MenuProvider {
	public PortableFurnaceItem(Properties props) {
		super(props);
	}
	
	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.CHEST;
	}
	
	@Override
	public Component getDisplayName() {
		return getDefaultInstance().getDisplayName();
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return null;
	}
}
