package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Recycler extends GenericMachine {
	public static boolean isRecipeMapInitialized = false;
	public static boolean isBreakdownMapInitialized = false;
	public static boolean isInitializingRecipeMap = false;
	public static boolean isInitializingBreakdownMap = false;
	
	public static Map<Item, CraftingRecipe> recipeMap = new HashMap<>();
	public static Map<Item, ItemWithCount> ingredientBreakdown = new HashMap<>();
	public record ItemWithCount(Item item, int count) {
		public ItemStack toStack() {
			return new ItemStack(item, count);
		}
		
		public ItemStack single() {
			return new ItemStack(item, 1);
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ItemWithCount that = (ItemWithCount) o;
			return count == that.count && Objects.equals(item, that.item);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(item, count);
		}
	}
	
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
			(slot, stack) -> slot == 0 && recipeMap.containsKey(stack.getItem()) ||
				slot >= 1 && slot <= 9 && !outputSlots.contains(slot))
		);
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		initializeRecipeMapAsync(level);
	}
	
	public static void initializeRecipeMapAsync(Level level) {
		if (!isInitializingRecipeMap && !isRecipeMapInitialized && level != null && !level.isClientSide()) {
			isInitializingRecipeMap = true;
			new Thread(() -> initializeRecipeMap(level)).start();
		}
		if (!isBreakdownMapInitialized && !isInitializingBreakdownMap && level != null && !level.isClientSide()) {
			isInitializingBreakdownMap = true;
			new Thread(() -> initializeSmallPartsMap(level)).start();
		}
	}
	
	private static void initializeRecipeMap(Level level) {
		try {
			recipeMap.clear();
			var recs = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
			recs.stream().filter(r -> {
				var result = r.value().getResultItem(level.registryAccess());
				return result != null && !result.isEmpty() && result.is(ModTags.Items.RECYCLABLE);
			}).forEach(r -> {
				var result = r.value().getResultItem(level.registryAccess());
				if (result != null) recipeMap.put(result.getItem(), r.value());
			});
		} catch (Exception e) {
			EternalArtifacts.LOGGER.error("Error initializing Recycler recipe map", e);
			isRecipeMapInitialized = false;
		} finally {
			isInitializingRecipeMap = false;
			isRecipeMapInitialized = true;
		}
	}
	
	private static void initializeSmallPartsMap(Level level) {
		try {
			ingredientBreakdown.clear();
			RecipeManager manager = level.getRecipeManager();
			var recs = manager.getAllRecipesFor(RecipeType.CRAFTING);
			for (var r : recs) {
				var recipe = r.value();
				ItemStack result = recipe.getResultItem(level.registryAccess());
				if (result == null || result.isEmpty()) continue;
				if (!result.getCraftingRemainingItem().isEmpty()) continue;
				List<Ingredient> ingredients = recipe.getIngredients();
				if (ingredients.size() != 1) continue;
				Ingredient ing = ingredients.get(0);
				if (ing.isEmpty()) continue;
				if (result.getCount() > 1) {
					for (ItemStack item : ing.getItems()) {
						ingredientBreakdown.put(item.getItem(), new ItemWithCount(result.getItem(), result.getCount()));
					}
				}
			}
		} catch (Exception e) {
			EternalArtifacts.LOGGER.error("Error initializing Small Parts map", e);
			isBreakdownMapInitialized = false;
		} finally {
			isInitializingBreakdownMap = false;
			isBreakdownMapInitialized = true;
		}
	}
	
	@Override
	protected void findRecipe() {
		ItemStack input = inventory.getStackInSlot(0);
		var crafting = recipeMap.get(input.getItem());
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
			ItemWithCount breakdown = findBreakdown(current);
			if (breakdown == null) {
				result.add(current);
				break;
			}
			
			current = new ItemStack(breakdown.item(), current.getCount() * breakdown.count());
		}
		
		return result;
	}
	
	@Nullable
	private Recycler.ItemWithCount findBreakdown(ItemStack stack) {
		for (Map.Entry<Item, ItemWithCount> entry : ingredientBreakdown.entrySet()) {
			if (stack.is(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
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
		initializeRecipeMapAsync(lvl);
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
