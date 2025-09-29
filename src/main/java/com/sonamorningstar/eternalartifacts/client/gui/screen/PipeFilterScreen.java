package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.PipeFilterMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidPipe;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.FilterablePipeBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class PipeFilterScreen extends AbstractPipeFilterScreen<PipeFilterMenu> {
	public PipeFilterScreen(PipeFilterMenu menu, Inventory pPlayerInventory, Component pTitle) {
		super(menu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void handleCarried(FakeSlot fakeSlot, ItemStack carried, NonNullList<FilterEntry> filters, FilterEntry entry, int index) {
		FilterablePipeBlockEntity<?> pipe = menu.getPipe();
		if (pipe instanceof FluidPipe) {
			IFluidHandlerItem handler = carried.getCapability(Capabilities.FluidHandler.ITEM);
			if (handler != null) {
				FluidStack filtered = handler.getFluidInTank(0).copyWithAmount(1000);
				if (!filtered.isEmpty()) {
					fakeSlot.set(ItemStack.EMPTY);
					filters.set(index, new FluidStackEntry(filtered, menu.isIgnoresNbt()));
					pipe.filterEntries.put(menu.getDir(), filters);
					updateFluid(menu.containerId, index, filtered);
				}
			}
		} else {
			ItemStack carriedCopy = carried.copyWithCount(1);
			fakeSlot.set(carriedCopy);
			filters.set(index, new ItemStackEntry(carriedCopy, menu.isIgnoresNbt()));
			pipe.filterEntries.put(menu.getDir(), filters);
			updateItem(menu.containerId, index, carriedCopy);
		}
	}
	
	@Override
	protected void handleEmptyCarried(FakeSlot fakeSlot, ItemStack stack, NonNullList<FilterEntry> filters, FilterEntry entry, int index, int mouseButton) {
		FilterablePipeBlockEntity<?> pipe = menu.getPipe();
		if (!stack.isEmpty()) {
			if (mouseButton == 0) {
				fakeSlot.set(ItemStack.EMPTY);
				filters.set(index, ItemStackEntry.EMPTY);
				pipe.filterEntries.put(menu.getDir(), filters);
				updateItem(menu.containerId, index, ItemStack.EMPTY);
				
			} else if (mouseButton == 1) {
				toConvert = Either.left(stack.getItem());
				convertingSlot = fakeSlot;
				setupTagPanel();
			}
		} else {
			if (entry instanceof FluidStackEntry fluidStackEntry && mouseButton == 1) {
				toConvert = Either.right(fluidStackEntry.getFilterStack().getFluid());
				convertingSlot = fakeSlot;
				setupTagPanel();
			} else {
				fakeSlot.set(ItemStack.EMPTY);
				filters.set(index, ItemStackEntry.EMPTY);
				pipe.filterEntries.put(menu.getDir(), filters);
				updateItem(menu.containerId, index, ItemStack.EMPTY);
				updateFluid(menu.containerId, index, FluidStack.EMPTY);
			}
		}
	}
}
