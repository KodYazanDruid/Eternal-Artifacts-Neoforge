package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Collections;

/**
 * Interface for machines that support item and/or fluid filtering
 */
public interface Filterable {
	int FILTER_SIZE = 9;
	
	default NonNullList<ItemFilterEntry> getItemFilters() {
		return NonNullList.create();
	}
	default NonNullList<FluidFilterEntry> getFluidFilters() {
		return NonNullList.create();
	}
	default NonNullList<BlockFilterEntry> getBlockFilters() {
		return NonNullList.create();
	}
	
	default boolean isItemFilterWhitelist() { return true; }
	default void setItemFilterWhitelistAndUpdate(boolean whitelist) {
		setItemFilterWhitelistSilent(whitelist);
		broadcastChanges();
	}
	
	default boolean isFluidFilterWhitelist() { return true; }
	default void setFluidFilterWhitelistAndUpdate(boolean whitelist) {
		setFluidFilterWhitelistSilent(whitelist);
		broadcastChanges();
	}
	
	default boolean isBlockFilterWhitelist() { return true; }
	default void setBlockFilterWhitelistAndUpdate(boolean whitelist) {
		setBlockFilterWhitelistSilent(whitelist);
		broadcastChanges();
	}
	
	default boolean isItemFilterIgnoreNBT() { return true; }
	default void setItemFilterIgnoreNBTAndUpdate(boolean ignoreNBT) {
		setItemFilterIgnoreNBTSilent(ignoreNBT);
		broadcastChanges();
	}
	
	default boolean isFluidFilterIgnoreNBT() { return true; }
	default void setFluidFilterIgnoreNBTAndUpdate(boolean ignoreNBT) {
		setFluidFilterIgnoreNBTSilent(ignoreNBT);
		broadcastChanges();
	}
	
	default boolean isBlockFilterIgnoreProperties() { return true; }
	default void setBlockFilterIgnorePropertiesAndUpdate(boolean ignoreProperties) {
		setBlockFilterIgnorePropertiesSilent(ignoreProperties);
		broadcastChanges();
	}
	
	default void setItemFilterWhitelistSilent(boolean whitelist) {}
	default void setFluidFilterWhitelistSilent(boolean whitelist) {}
	default void setBlockFilterWhitelistSilent(boolean whitelist) {}
	default void setItemFilterIgnoreNBTSilent(boolean ignoreNBT) {}
	default void setFluidFilterIgnoreNBTSilent(boolean ignoreNBT) {}
	default void setBlockFilterIgnorePropertiesSilent(boolean ignoreProperties) {}
	
	default void broadcastChanges() {
		if (this instanceof ModBlockEntity mbe) mbe.sendUpdate();
	}
	
	default boolean matchesBlockFilter(BlockState state) {
		NonNullList<BlockFilterEntry> filters = getBlockFilters();
		boolean isWhitelist = isBlockFilterWhitelist();
		boolean shouldSkip = false;
		
		for (BlockFilterEntry filter : filters) {
			boolean matches = filter.matches(state);
			if (!isWhitelist && matches) {
				shouldSkip = true;
				break;
			}
			if (isWhitelist && matches) {
				shouldSkip = false;
				break;
			} else if (isWhitelist) {
				shouldSkip = true;
			}
		}
		
		return !shouldSkip;
	}
	
	default boolean matchesItemFilter(ItemStack stack) {
		NonNullList<ItemFilterEntry> filters = getItemFilters();
		boolean isWhitelist = isItemFilterWhitelist();
		boolean shouldSkip = false;
		
		for (ItemFilterEntry filter : filters) {
			boolean matches = filter.matches(stack);
			if (!isWhitelist && matches) {
				shouldSkip = true;
				break;
			}
			if (isWhitelist && matches) {
				shouldSkip = false;
				break;
			} else if (isWhitelist) {
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
				shouldSkip = true;
				break;
			}
			if (isWhitelist && matches) {
				shouldSkip = false;
				break;
			} else if (isWhitelist) {
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
		
		// Save block filters with index
		ListTag blockFilterList = new ListTag();
		NonNullList<BlockFilterEntry> blockFilters = getBlockFilters();
		for (int i = 0; i < blockFilters.size(); i++) {
			BlockFilterEntry entry = blockFilters.get(i);
			if (!entry.isEmpty()) {
				CompoundTag entryTag = entry.serializeNBT();
				entryTag.putInt("Slot", i);
				blockFilterList.add(entryTag);
			}
		}
		filterData.put("BlockFilters", blockFilterList);
		filterData.putBoolean("BlockWhitelist", isBlockFilterWhitelist());
		filterData.putBoolean("BlockIgnoreProperties", isBlockFilterIgnoreProperties());
		
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
		
		// Load block filters with index
		if (filterData.contains("BlockFilters")) {
			ListTag blockFilterList = filterData.getList("BlockFilters", 10);
			NonNullList<BlockFilterEntry> blockFilters = getBlockFilters();
			// Listeyi varsayılan boş değerlerle temizle
			Collections.fill(blockFilters, BlockStateEntry.EMPTY);
			// Index'e göre yükle
			for (int i = 0; i < blockFilterList.size(); i++) {
				CompoundTag entryTag = blockFilterList.getCompound(i);
				int slot = entryTag.getInt("Slot");
				if (slot >= 0 && slot < blockFilters.size()) {
					blockFilters.set(slot, BlockFilterEntry.fromNBT(entryTag));
				}
			}
			setBlockFilterWhitelistSilent(filterData.getBoolean("BlockWhitelist"));
			setBlockFilterIgnorePropertiesSilent(filterData.getBoolean("BlockIgnoreProperties"));
		}
	}
}
