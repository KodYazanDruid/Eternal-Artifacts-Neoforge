package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.filter.FluidFilterEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FluidFilter extends Item {
	public static final String FILTERS_TAG = "FilterData";
	
	public FluidFilter(Properties pProperties) {
		super(pProperties);
	}
	
	public List<FluidFilterEntry> getFilterEntries(ItemStack filter) {
		if (filter.hasTag()) {
			ListTag filters = filter.getTag().getList(FILTERS_TAG, 10);
			return filters.stream()
				.map(tag -> (CompoundTag) tag)
				.map(FluidFilterEntry::fromNBT)
				.toList();
		}
		return List.of();
	}
	
	public void addFilterEntry(ItemStack filter, FluidFilterEntry filterEntry) {
		ListTag filters = filter.getOrCreateTag().getList(FILTERS_TAG, 10);
		filters.add(filterEntry.serializeNBT());
		filter.getTag().put(FILTERS_TAG, filters);
	}
	
	public void removeFilterEntry(ItemStack filter, FluidFilterEntry filterEntry) {
		ListTag filters = filter.getOrCreateTag().getList(FILTERS_TAG, 10);
		filters.removeIf(tag -> {
			CompoundTag entryTag = (CompoundTag) tag;
			return entryTag.equals(filterEntry.serializeNBT());
		});
		filter.getTag().put(FILTERS_TAG, filters);
	}
}
