package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.network.FluidStackFilterToServer;
import com.sonamorningstar.eternalartifacts.network.FluidTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.ItemTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractPipeFilterMenu extends AbstractModContainerMenu {
	protected final SimpleContainer fakeSlots;
	@Getter
	protected final NonNullList<FilterEntry> filterEntries;
	@Getter
	protected final int attType;
	@Getter @Setter protected boolean isWhitelist;
	@Getter @Setter protected boolean ignoresNbt;
	
	protected AbstractPipeFilterMenu(MenuType<?> type, int id, Inventory inv, int attType) {
		super(type, id);
		this.attType = attType;
		this.fakeSlots = new SimpleContainer(9);
		this.filterEntries = NonNullList.withSize(9, ItemFilterEntry.Empty.create(true));
		this.fakeSlots.addListener(this::slotsChanged);
		
		addPlayerInventoryAndHotbar(inv, 8, 66);
		addFakeSlots(62, 17);
	}
	
	private void addFakeSlots(int xOff, int yOff) {
		for (int i = 0; i < 9; i++) {
			addSlot(new FilterFakeSlot(fakeSlots, filterEntries.get(i), i,
				xOff + (i % 3) * 18, yOff + (i / 3) * 18, false));
		}
	}
	
	@Override
	public void fakeSlotSynch(UpdateFakeSlotToServer pkt) {
		fakeSlots.setItem(pkt.index(), pkt.slotItem());
		filterEntries.set(pkt.index(), new ItemStackEntry(pkt.slotItem(), ignoresNbt));
		saveFilterEntries();
	}
	
	public void itemTagFilterSynch(ItemTagFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filterEntries.set(pkt.index(), new ItemTagEntry(pkt.tag()));
		saveFilterEntries();
	}
	
	public void fluidStackFilterSync(FluidStackFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filterEntries.set(pkt.index(), new FluidStackEntry(pkt.fluidStack(), ignoresNbt));
		saveFilterEntries();
	}
	
	public void fluidTagFilterSync(FluidTagFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filterEntries.set(pkt.index(), new FluidTagEntry(pkt.tag()));
		saveFilterEntries();
	}
	
	@Override
	public boolean clickMenuButton(Player pPlayer, int id) {
		if (id == 0) {
			isWhitelist = !isWhitelist;
			for (FilterEntry entry : filterEntries) {
				entry.setWhitelist(isWhitelist);
			}
			saveFilterEntries();
			return true;
		} else if (id == 1) {
			ignoresNbt = !ignoresNbt;
			for (FilterEntry entry : filterEntries) {
				entry.setIgnoreNBT(ignoresNbt);
			}
			saveFilterEntries();
			return true;
		}
		return super.clickMenuButton(pPlayer, id);
	}
	
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int index) {
		return ItemStack.EMPTY;
	}
	
	protected abstract void saveFilterEntries();
}
