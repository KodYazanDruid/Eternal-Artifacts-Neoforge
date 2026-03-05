package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ItemCollector extends GenericMachine implements WorkingAreaProvider {
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
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		if (!redstoneChecks(lvl)) return;
		
		lvl.getEntitiesOfClass(ItemEntity.class, getWorkingArea(pos)).stream().filter(e -> !e.isRemoved() && !e.hasPickUpDelay())
			.forEach(e -> {
				if (canWork(energy)) {
					ItemStack stackCopy = e.getItem().copy();
					ItemStack remainder = ItemHelper.insertItemStackedForced(inventory, e.getItem(), false);
					if (remainder.isEmpty()) {
						e.setItem(ItemStack.EMPTY);
						e.discard();
					} else e.setItem(remainder);
					
					if (!ItemStack.isSameItemSameTags(stackCopy, e.getItem())) {
						spendEnergy(energy);
					}
				}
			});
	}
}
