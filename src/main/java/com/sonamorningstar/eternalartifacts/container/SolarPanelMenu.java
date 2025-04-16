package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SolarPanelMenu extends AbstractMachineMenu {
	public SolarPanelMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
		this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
	}
	
	public SolarPanelMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(ModMenuTypes.SOLAR_PANEL.get(), id, inv, entity, data);
	}
	
	
}
