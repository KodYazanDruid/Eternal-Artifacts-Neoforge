package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.network.FluidStackFilterToServer;
import com.sonamorningstar.eternalartifacts.network.FluidTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.ItemTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Getter
public class PipeFilterItemMenu extends AbstractModContainerMenu {
	protected final ItemStack filter;
	protected final Player player;
	protected final int attType;
	protected SimpleContainer fakeSlots;
	protected NonNullList<FilterEntry> filterEntries;
	@Setter
	protected boolean isWhitelist;
	@Setter
	protected boolean ignoresNbt;
	
	public PipeFilterItemMenu(int id, int attType, Inventory inv, ItemStack filter) {
		super(ModMenuTypes.PIPE_FILTER_ITEM.get(), id);
		this.filter = filter;
		this.player = inv.player;
		this.attType = attType;
		this.fakeSlots = new SimpleContainer(9);
		boolean hasTag = filter.hasTag();
		CompoundTag tag = hasTag ? filter.getTag() : new CompoundTag();
		CompoundTag filterData = tag.getCompound("FilterData");
		this.filterEntries = NonNullList.withSize(9, ItemFilterEntry.Empty.create(true));
		ListTag itemEntries = filterData.getList("ItemFilters", 10);
		for (int i = 0; i < itemEntries.size(); i++) {
			CompoundTag entryTag = itemEntries.getCompound(i);
			filterEntries.set(i, ItemFilterEntry.fromNBT(entryTag));
		}
		ListTag fluidEntries = filterData.getList("FluidFilters", 10);
		for (int i = 0; i < fluidEntries.size(); i++) {
			FilterEntry entry = filterEntries.get(i);
			CompoundTag entryTag = fluidEntries.getCompound(i);
			if (!(entry instanceof ItemTagEntry || (entry instanceof ItemStackEntry itemEntry && !itemEntry.getFilterStack().isEmpty())))
				filterEntries.set(i, FluidFilterEntry.fromNBT(entryTag));
		}
		this.isWhitelist = filterData.getBoolean("whitelist");
		this.ignoresNbt = filterData.getBoolean("ignore_nbt");
		for (int i = 0; i < 9; i++) {
			FilterEntry entry = filterEntries.get(i);
			if (entry instanceof ItemStackEntry ise) fakeSlots.setItem(i, ise.getFilterStack());
			else fakeSlots.setItem(i, ItemStack.EMPTY);
			entry.setWhitelist(isWhitelist);
			entry.setIgnoreNBT(ignoresNbt);
		}
		
		this.fakeSlots.addListener(this::slotsChanged);
		addPlayerInventoryAndHotbar(inv, 8, 66);
		addFakeSlots(62, 17);
	}
	
	private void addFakeSlots(int xOff, int yOff) {
		for (int i = 0; i < 9; i++) {
			addSlot(new FilterFakeSlot(fakeSlots, filterEntries.get(i), i, xOff + (i % 3) * 18, yOff + (i / 3) * 18, false));
		}
	}
	
	@Override
	public void fakeSlotSynch(UpdateFakeSlotToServer pkt) {
		fakeSlots.setItem(pkt.index(), pkt.slotItem());
		filterEntries.set(pkt.index(), new ItemStackEntry(pkt.slotItem(), ignoresNbt));
		saveFilterEntries();
	}
	
	public void tagFilterSynch(ItemTagFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filterEntries.set(pkt.index(), new ItemTagEntry(pkt.tag()));
		saveFilterEntries();
	}
	
	public void fluidStackFilter(FluidStackFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filterEntries.set(pkt.index(), new FluidStackEntry(pkt.fluidStack(), ignoresNbt));
		saveFilterEntries();
	}
	public void fluidTagFilter(FluidTagFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filterEntries.set(pkt.index(), new FluidTagEntry(pkt.tag()));
		saveFilterEntries();
	}
	
	public void saveFilterEntries() {
		CompoundTag filterData = new CompoundTag();
		ListTag itemDirList = new ListTag();
		ListTag fluidDirList = new ListTag();
		for (int i = 0; i < 9; i++) {
			FilterEntry entry = filterEntries.get(i);
			if (entry instanceof ItemFilterEntry) {
				itemDirList.add(entry.serializeNBT());
				fluidDirList.add(FluidStackEntry.EMPTY.serializeNBT());
			}
			else if (entry instanceof FluidFilterEntry) {
				itemDirList.add(ItemStackEntry.EMPTY.serializeNBT());
				fluidDirList.add(entry.serializeNBT());
			}
		}
		CompoundTag dirTag = new CompoundTag();
		dirTag.put("ItemFilters", itemDirList);
		dirTag.put("FluidFilters", fluidDirList);
		dirTag.putBoolean("whitelist", isWhitelist);
		dirTag.putBoolean("ignore_nbt", ignoresNbt);
		filterData.put("FilterData", dirTag);
		filter.setTag(filterData);
	}
	
	@Override
	public boolean clickMenuButton(Player pPlayer, int id) {
		if (id == 0) {
			isWhitelist = !isWhitelist;
			saveFilterEntries();
			return true;
		} else if (id == 1) {
			ignoresNbt = !ignoresNbt;
			saveFilterEntries();
			return true;
		}
		return super.clickMenuButton(pPlayer, id);
	}
	
	@Override
	public boolean stillValid(Player player) {
		return !player.isSpectator() && !player.isDeadOrDying();
	}
	
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int index) {
		return ItemStack.EMPTY;
	}
	
	public static PipeFilterItemMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
		return new PipeFilterItemMenu(id, buf.readByte(), inv, buf.readItem());
	}
}
