package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.PipeFilterItemMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;

public class PipeFilterItemScreen extends AbstractPipeFilterScreen<PipeFilterItemMenu> {
	public PipeFilterItemScreen(PipeFilterItemMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
		if (slotId == menu.getSlotId()) return;
		super.slotClicked(slot, slotId, mouseButton, type);
	}
	
	@Override
	protected void handleCarried(FakeSlot fakeSlot, ItemStack carried, NonNullList<FilterEntry> filters, FilterEntry entry, int index) {
		if (entry instanceof ItemStackEntry itemEntry && hasCapAndMatches(itemEntry, carried)) {
			FluidStack filtered = carried.getCapability(Capabilities.FluidHandler.ITEM).getFluidInTank(0).copyWithAmount(1000);
			filters.set(index, new FluidStackEntry(filtered, menu.isIgnoresNbt()));
			updateFluid(menu.containerId, index, filtered);
		} else {
			ItemStack carriedCopy = carried.copyWithCount(1);
			fakeSlot.set(carriedCopy);
			filters.set(index, new ItemStackEntry(carriedCopy, menu.isIgnoresNbt()));
			updateItem(menu.containerId, index, carriedCopy);
		}
	}
	
	@Override
	protected void handleEmptyCarried(FakeSlot fakeSlot, ItemStack stack, NonNullList<FilterEntry> filters, FilterEntry entry, int index, int mouseButton) {
		if (!stack.isEmpty()) {
			if (mouseButton == 0) {
				fakeSlot.set(ItemStack.EMPTY);
				filters.set(index, ItemStackEntry.EMPTY);
				updateItem(menu.containerId, index, ItemStack.EMPTY);
			} else if (mouseButton == 1) {
				toConvert = Either.left(stack.getItem());
				convertingSlot = fakeSlot;
				setupTagPanel();
			}
		} else {
			if(entry instanceof FluidStackEntry fluidEntry && mouseButton == 1) {
				toConvert = Either.right(fluidEntry.getFilterStack().getFluid());
				convertingSlot = fakeSlot;
				setupTagPanel();
			} else {
				fakeSlot.set(ItemStack.EMPTY);
				filters.set(index, ItemStackEntry.EMPTY);
				updateItem(menu.containerId, index, ItemStack.EMPTY);
				updateFluid(menu.containerId, index, FluidStack.EMPTY);
			}
		}
	}
}
