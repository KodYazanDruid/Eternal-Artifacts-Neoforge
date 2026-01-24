package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.ItemWithCount;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.api.machine.RecyclerRecipeCache;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Recycler extends GenericMachine {
	public Recycler(BlockPos pos, BlockState blockState) {
		super(ModMachines.RECYCLER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		for (int i = 1; i < 10; i++) {
			outputSlots.add(i);
			screenInfo.setSlotPosition(90 + (i - 1) % 3 * 18, 22 + (i - 1) / 3 * 18, i);
		}
		screenInfo.setSlotPosition(36, 40, 0);
		screenInfo.setArrowXOffset(-16);
		setInventory(() -> createRecipeFinderInventory(10,
			(slot, stack) -> slot == 0 && RecyclerRecipeCache.hasRecipe(stack.getItem()) ||
				slot >= 1 && slot <= 9 && !outputSlots.contains(slot))
		);
	}
	
	@Override
	protected void findRecipe() {
		ItemStack input = inventory.getStackInSlot(0);
		var crafting = RecyclerRecipeCache.getRecipe(input.getItem());
		if (crafting != null) RecipeCache.cacheRecipe(this, crafting);
		else RecipeCache.clearRecipes(this);
	}
	
	private List<ItemStack> getRecycleOutputs(Recipe<?> recipe) {
		var original = recipe.getIngredients().stream()
			.filter(i -> !i.isEmpty())
			.map(i -> i.getItems()[0])
			.toList();
		
		List<ItemStack> materials = new ArrayList<>();
		ItemStack input = inventory.getStackInSlot(0);
		if (input.isEmpty() || original.isEmpty()) return materials;
		
		float damagePercent = input.isDamageableItem()
			? (float) (input.getMaxDamage() - input.getDamageValue()) / input.getMaxDamage()
			: 1f;
		
		List<ItemStack> allParts = new ArrayList<>();
		for (ItemStack base : original) {
			allParts.addAll(flattenToSmallParts(base.copy()));
		}
		
		int totalParts = allParts.stream().mapToInt(ItemStack::getCount).sum();
		int giveParts = Math.max(1, Math.round(totalParts * damagePercent));
		
		return distributeParts(allParts, giveParts);
	}
	
	private List<ItemStack> distributeParts(List<ItemStack> allParts, int target) {
		List<ItemStack> result = new ArrayList<>();
		int remaining = target;
		
		for (ItemStack stack : allParts) {
			if (remaining <= 0) break;
			
			int take = Math.min(stack.getCount(), remaining);
			if (take > 0) {
				ItemStack copy = stack.copy();
				copy.setCount(take);
				result.add(copy);
				remaining -= take;
			}
		}
		
		return result;
	}
	
	private List<ItemStack> flattenToSmallParts(ItemStack stack) {
		List<ItemStack> result = new ArrayList<>();
		ItemStack current = stack.copy();
		
		while (true) {
			ItemWithCount breakdown = RecyclerRecipeCache.getBreakdown(current.getItem());
			if (breakdown == null) {
				result.add(current);
				break;
			}
			
			current = new ItemStack(breakdown.item(), current.getCount() * breakdown.count());
		}
		
		return result;
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		if (recipe != null) {
			var recs = getRecycleOutputs(recipe);
			for (ItemStack stack : recs) {
				condition.queueImport(stack.copy());
			}
			condition.commitQueuedImports();
		}
		super.setProcessCondition(condition, recipe);
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		progress(() -> {
			var recs = getRecycleOutputs(getCachedRecipe());
			for (ItemStack stack : recs) {
				ItemHelper.insertItemForced(inventory, stack.copy(), false, outputSlots);
			}
			inventory.extractItem(0, 1, false);
		});
	}
}
