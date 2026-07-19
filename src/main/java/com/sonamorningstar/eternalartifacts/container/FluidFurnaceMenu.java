package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FluidSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class FluidFurnaceMenu extends AbstractMachineMenu {
	public FluidFurnaceMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
		if (getBeInventory() != null) {
			addSlot(new SlotItemHandler(getBeInventory(), 0, 56, 17));
			addSlot(new SlotItemHandler(getBeInventory(), 1, 116, 35));
		}
		if (getBeTank() instanceof  AbstractFluidTank aft) {
			addFluidSlot(new FluidSlot(() -> aft, 0, 55, 52));
		}
	}
}
