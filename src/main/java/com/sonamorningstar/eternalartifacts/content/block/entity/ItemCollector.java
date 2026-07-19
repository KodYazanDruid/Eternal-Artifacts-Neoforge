package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.ItemFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemStackEntry;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

@Getter
public class ItemCollector extends GenericMachine implements WorkingAreaProvider, Filterable {
	private final NonNullList<ItemFilterEntry> itemFilters = NonNullList.withSize(9, ItemStackEntry.EMPTY);
	private boolean itemFilterWhitelist = false;
	private boolean itemFilterIgnoreNBT = true;
	
	public ItemCollector(BlockPos pos, BlockState blockState) {
		super(ModMachines.ITEM_COLLECTOR, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setInventory(() -> createBasicInventory(24, false));
		for (int i = 0; i < 24; i++) {
			outputSlots.add(i);
			screenInfo.setSlotPosition(26 + (i % 8) * 18, 21 + (i / 8) * 18, i);
		}
		screenInfo.setShouldDrawArrow(false);
		screenInfo.setShouldDrawInventoryTitle(false);
	}
	
	@Override
	public AABB getWorkingArea(BlockPos anchor) {
		return new AABB(anchor).inflate(4);
	}
	
	@Override
	public boolean hasItemFilters() {return true;}
	
	@Override
	public void setItemFilterWhitelistSilent(boolean whitelist) {
		this.itemFilterWhitelist = whitelist;
	}
	
	@Override
	public void setItemFilterIgnoreNBTSilent(boolean ignoreNBT) {
		this.itemFilterIgnoreNBT = ignoreNBT;
	}
	
	@Override
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		if (!redstoneChecks(lvl) || !canWork(energy)) return;
		
		lvl.getEntitiesOfClass(ItemEntity.class, getWorkingArea(pos)).stream()
			.filter(e -> !e.isRemoved() && !e.hasPickUpDelay() && matchesItemFilter(e.getItem()))
			.forEach(e -> {
				ItemStack stackCopy = e.getItem().copy();
				ItemStack remainder = ItemHelper.insertItemStackedForced(inventory, e.getItem(), false);
				if (remainder.isEmpty()) {
					e.setItem(ItemStack.EMPTY);
					e.discard();
				} else e.setItem(remainder);
				
				if (!ItemStack.isSameItemSameTags(stackCopy, e.getItem())) {
					spendEnergy(energy);
				}
			});
	}
}
