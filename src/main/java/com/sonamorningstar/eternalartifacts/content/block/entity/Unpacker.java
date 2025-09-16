package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Unpacker extends GenericMachine {
	public Unpacker(BlockPos pos, BlockState blockState) {
		super(ModMachines.UNPACKER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		outputSlots.add(1);
		setInventory(() -> createBasicInventory(2, (slot, stack) ->
			slot == 0 && Recycler.ingredientBreakdown.containsKey(stack.getItem()) ||
			slot == 1 && !outputSlots.contains(slot)));
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		Recycler.initializeRecipeMapAsync(level);
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		ItemStack input = inventory.getStackInSlot(0);
		if (!input.isEmpty() && Recycler.ingredientBreakdown.containsKey(input.getItem())) {
			condition.tryExtractItemForced(1, 0);
			ItemStack output = Recycler.ingredientBreakdown.get(input.getItem()).toStack();
			if (!output.isEmpty()) condition.queueImport(output);
		}
		condition.commitQueuedImports();
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		Recycler.initializeRecipeMapAsync(lvl);
		super.tickServer(lvl, pos, st);
		
		progress(() -> {
			ItemStack input = inventory.getStackInSlot(0);
			ItemStack packed = Recycler.ingredientBreakdown.get(input.getItem()).toStack();
			inventory.extractItem(0, 1, false);
			inventory.insertItemForced(1, packed, false);
		});
	}
	
}
