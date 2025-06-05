package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.filter.ItemFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemStackEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemTagEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemFilter extends Item {
	public static final String FILTERS_TAG = "FilterData";
	
	public ItemFilter(Properties pProperties) {
		super(pProperties);
	}
	
	public List<ItemFilterEntry> getFilterEntries(ItemStack filter) {
		if (filter.hasTag()) {
			ListTag filters = filter.getTag().getList(FILTERS_TAG, 10);
			return filters.stream()
				.map(tag -> (CompoundTag) tag)
				.map(ItemFilterEntry::fromNBT)
				.toList();
		}
		return List.of();
	}
	
	public void addFilterEntry(ItemStack filter, ItemFilterEntry filterEntry) {
		ListTag filters = filter.getOrCreateTag().getList(FILTERS_TAG, 10);
		filters.add(filterEntry.serializeNBT());
		filter.getTag().put(FILTERS_TAG, filters);
	}
	
	public void removeFilterEntry(ItemStack filter, ItemFilterEntry filterEntry) {
		ListTag filters = filter.getOrCreateTag().getList(FILTERS_TAG, 10);
		filters.removeIf(tag -> {
			CompoundTag entryTag = (CompoundTag) tag;
			return entryTag.equals(filterEntry.serializeNBT());
		});
		filter.getTag().put(FILTERS_TAG, filters);
	}
}
