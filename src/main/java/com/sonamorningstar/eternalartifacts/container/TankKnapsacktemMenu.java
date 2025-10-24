package com.sonamorningstar.eternalartifacts.container;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

@Getter
public class TankKnapsacktemMenu extends TankKnapsackMenu {
	private final short slotId;
	public TankKnapsacktemMenu(int id, Inventory inv, short slotId) {
		super(id, inv, inv.getItem(slotId));
		this.slotId = slotId;
	}
	
	public static TankKnapsacktemMenu fromNetworkItem(int id, Inventory inventory, FriendlyByteBuf buff) {
		return new TankKnapsacktemMenu(id, inventory, buff.readShort());
	}
}
