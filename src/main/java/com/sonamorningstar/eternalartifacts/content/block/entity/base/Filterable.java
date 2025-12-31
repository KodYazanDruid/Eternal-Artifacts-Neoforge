package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.filter.FluidFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.FluidStackEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemStackEntry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Collections;

/**
 * Interface for machines that support item and/or fluid filtering
 */
public interface Filterable {
	int FILTER_SIZE = 9;
	
	NonNullList<ItemFilterEntry> getItemFilters();
	NonNullList<FluidFilterEntry> getFluidFilters();
	
	boolean isItemFilterWhitelist();
	void setItemFilterWhitelist(boolean whitelist);
	
	boolean isFluidFilterWhitelist();
	void setFluidFilterWhitelist(boolean whitelist);
	
	boolean isItemFilterIgnoreNBT();
	void setItemFilterIgnoreNBT(boolean ignoreNBT);
	
	boolean isFluidFilterIgnoreNBT();
	void setFluidFilterIgnoreNBT(boolean ignoreNBT);
	
	/**
	 * Silent setters for loading from NBT without triggering setChanged()
	 */
	default void setItemFilterWhitelistSilent(boolean whitelist) {
		setItemFilterWhitelist(whitelist);
	}
	
	default void setFluidFilterWhitelistSilent(boolean whitelist) {
		setFluidFilterWhitelist(whitelist);
	}
	
	default void setItemFilterIgnoreNBTSilent(boolean ignoreNBT) {
		setItemFilterIgnoreNBT(ignoreNBT);
	}
	
	default void setFluidFilterIgnoreNBTSilent(boolean ignoreNBT) {
		setFluidFilterIgnoreNBT(ignoreNBT);
	}
	
	default boolean matchesItemFilter(ItemStack stack) {
		NonNullList<ItemFilterEntry> filters = getItemFilters();
		boolean isWhitelist = isItemFilterWhitelist();
		boolean shouldSkip = false;
		
		for (ItemFilterEntry filter : filters) {
			boolean matches = filter.matches(stack);
			if (!isWhitelist && matches) {
				// Blacklist: eşleşirse skip et
				shouldSkip = true;
				break;
			}
			if (isWhitelist && matches) {
				// Whitelist: eşleşirse skip etme
				shouldSkip = false;
				break;
			} else if (isWhitelist) {
				// Whitelist: eşleşmezse skip et
				shouldSkip = true;
			}
		}
		return !shouldSkip;
	}
	
	default boolean matchesFluidFilter(FluidStack stack) {
		NonNullList<FluidFilterEntry> filters = getFluidFilters();
		boolean isWhitelist = isFluidFilterWhitelist();
		boolean shouldSkip = false;
		
		for (FluidFilterEntry filter : filters) {
			boolean matches = filter.matches(stack);
			if (!isWhitelist && matches) {
				// Blacklist: eşleşirse skip et
				shouldSkip = true;
				break;
			}
			if (isWhitelist && matches) {
				// Whitelist: eşleşirse skip etme
				shouldSkip = false;
				break;
			} else if (isWhitelist) {
				// Whitelist: eşleşmezse skip et
				shouldSkip = true;
			}
		}
		return !shouldSkip;
	}
	
	default void saveFilters(CompoundTag tag) {
		CompoundTag filterData = new CompoundTag();
		
		// Save item filters with index
		ListTag itemFilterList = new ListTag();
		NonNullList<ItemFilterEntry> itemFilters = getItemFilters();
		for (int i = 0; i < itemFilters.size(); i++) {
			ItemFilterEntry entry = itemFilters.get(i);
			if (!entry.isEmpty()) {
				CompoundTag entryTag = entry.serializeNBT();
				entryTag.putInt("Slot", i);
				itemFilterList.add(entryTag);
			}
		}
		filterData.put("ItemFilters", itemFilterList);
		filterData.putBoolean("ItemWhitelist", isItemFilterWhitelist());
		filterData.putBoolean("ItemIgnoreNBT", isItemFilterIgnoreNBT());
		
		// Save fluid filters with index
		ListTag fluidFilterList = new ListTag();
		NonNullList<FluidFilterEntry> fluidFilters = getFluidFilters();
		for (int i = 0; i < fluidFilters.size(); i++) {
			FluidFilterEntry entry = fluidFilters.get(i);
			if (!entry.isEmpty()) {
				CompoundTag entryTag = entry.serializeNBT();
				entryTag.putInt("Slot", i);
				fluidFilterList.add(entryTag);
			}
		}
		filterData.put("FluidFilters", fluidFilterList);
		filterData.putBoolean("FluidWhitelist", isFluidFilterWhitelist());
		filterData.putBoolean("FluidIgnoreNBT", isFluidFilterIgnoreNBT());
		
		tag.put("FilterData", filterData);
	}
	
	default void loadFilters(CompoundTag tag) {
		if (!tag.contains("FilterData")) return;
		
		CompoundTag filterData = tag.getCompound("FilterData");
		
		// Load item filters with index
		if (filterData.contains("ItemFilters")) {
			ListTag itemFilterList = filterData.getList("ItemFilters", 10);
			NonNullList<ItemFilterEntry> itemFilters = getItemFilters();
			// Listeyi varsayılan boş değerlerle temizle
			Collections.fill(itemFilters, ItemStackEntry.EMPTY);
			// Index'e göre yükle
			for (int i = 0; i < itemFilterList.size(); i++) {
				CompoundTag entryTag = itemFilterList.getCompound(i);
				int slot = entryTag.getInt("Slot");
				if (slot >= 0 && slot < itemFilters.size()) {
					itemFilters.set(slot, ItemFilterEntry.fromNBT(entryTag));
				}
			}
			setItemFilterWhitelistSilent(filterData.getBoolean("ItemWhitelist"));
			setItemFilterIgnoreNBTSilent(filterData.getBoolean("ItemIgnoreNBT"));
		}
		
		// Load fluid filters with index
		if (filterData.contains("FluidFilters")) {
			ListTag fluidFilterList = filterData.getList("FluidFilters", 10);
			NonNullList<FluidFilterEntry> fluidFilters = getFluidFilters();
			// Listeyi varsayılan boş değerlerle temizle
			Collections.fill(fluidFilters, FluidStackEntry.EMPTY);
			// Index'e göre yükle
			for (int i = 0; i < fluidFilterList.size(); i++) {
				CompoundTag entryTag = fluidFilterList.getCompound(i);
				int slot = entryTag.getInt("Slot");
				if (slot >= 0 && slot < fluidFilters.size()) {
					fluidFilters.set(slot, FluidFilterEntry.fromNBT(entryTag));
				}
			}
			setFluidFilterWhitelistSilent(filterData.getBoolean("FluidWhitelist"));
			setFluidFilterIgnoreNBTSilent(filterData.getBoolean("FluidIgnoreNBT"));
		}
	}
}
