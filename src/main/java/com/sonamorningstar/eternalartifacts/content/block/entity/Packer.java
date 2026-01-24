package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.ItemWithCount;
import com.sonamorningstar.eternalartifacts.api.machine.PackerRecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Packer extends GenericMachine {
	
	public Packer(BlockPos pos, BlockState blockState) {
		super(ModMachines.PACKER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		outputSlots.add(1);
		setInventory(() -> createBasicInventory(2, (slot, stack) ->
			slot == 0 && PackerRecipeCache.contains(stack.getItem()) ||
				slot == 1 && !outputSlots.contains(slot)));
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		ItemStack input = inventory.getStackInSlot(0);
		if (!input.isEmpty() && PackerRecipeCache.contains(input.getItem())) {
			ItemWithCount itemWithCount = PackerRecipeCache.get(input.getItem());
			condition.tryExtractItemForced(itemWithCount.count(), 0);
			ItemStack output = itemWithCount.single();
			if (!output.isEmpty()) condition.queueImport(output);
			condition.commitQueuedImports();
		}
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		progress(() -> {
			ItemStack input = inventory.getStackInSlot(0);
			ItemWithCount itemWithCount = PackerRecipeCache.get(input.getItem());
			ItemStack unpacked = itemWithCount.single();
			inventory.extractItem(0, itemWithCount.count(), false);
			inventory.insertItemForced(1, unpacked, false);
		});
	}

}
