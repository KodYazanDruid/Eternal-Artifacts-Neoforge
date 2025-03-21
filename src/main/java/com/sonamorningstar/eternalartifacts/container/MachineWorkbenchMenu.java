package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MachineWorkbenchMenu extends AbstractMachineMenu {
	
	public MachineWorkbenchMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		this(id, inv,
			inv.player.level().getBlockEntity(extraData.readBlockPos()),
			new SimpleContainerData(2)
		);
	}
	
	public MachineWorkbenchMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(ModMenuTypes.MACHINE_WORKBENCH.get(), id, inv, entity, data);
		if (getBeInventory() != null)
			addSlot(new SlotItemHandler(getBeInventory(), 0, 155, 5));
	}
}
