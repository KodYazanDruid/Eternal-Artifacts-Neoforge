package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractPipeFilterMenu;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Getter
public class PipeFilterItemMenu extends AbstractPipeFilterMenu {
	protected final short slotId;
	protected final ItemStack filter;
	protected final Player player;
	
	public PipeFilterItemMenu(int id, int attType, Inventory inv, short slotId) {
		super(ModMenuTypes.PIPE_FILTER_ITEM.get(), id, inv, attType);
		this.slotId = slotId;
		this.filter = inv.getItem(slotId);
		this.player = inv.player;
		boolean hasTag = filter.hasTag();
		CompoundTag tag = hasTag ? filter.getTag() : new CompoundTag();
		CompoundTag filterData = tag.getCompound("FilterData");
		ListTag itemEntries = filterData.getList("ItemFilters", 10);
		for (int i = 0; i < itemEntries.size(); i++) {
			CompoundTag entryTag = itemEntries.getCompound(i);
			filterEntries.set(i, ItemFilterEntry.fromNBT(entryTag));
		}
		ListTag fluidEntries = filterData.getList("FluidFilters", 10);
		for (int i = 0; i < fluidEntries.size(); i++) {
			FilterEntry entry = filterEntries.get(i);
			CompoundTag entryTag = fluidEntries.getCompound(i);
			if (!(entry instanceof ItemStackEntry) && !(entry instanceof ItemTagEntry) || entry.isEmpty())
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
			} else if (entry instanceof FluidFilterEntry) {
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
	public boolean stillValid(Player player) {
		return !player.isSpectator() && !player.isDeadOrDying();
	}
	
	public static PipeFilterItemMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
		return new PipeFilterItemMenu(id, buf.readByte(), inv, buf.readShort());
	}
}
