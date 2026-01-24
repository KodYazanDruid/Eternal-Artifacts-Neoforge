package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.api.ItemWithCount;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UnpackerRecipeCache {
	
	private static final Map<Item, ItemWithCount> UNPACKING_MAP = new HashMap<>();
	
	private UnpackerRecipeCache() {}
	
	public static void rebuild(RecipeManager recipeManager, Level level) {
		UNPACKING_MAP.clear();
		
		for (var holder : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
			CraftingRecipe recipe = holder.value();
			
			ItemStack result = recipe.getResultItem(level.registryAccess());
			if (result == null || result.isEmpty() || result.getCount() != 1) continue;
			
			if (result.hasCraftingRemainingItem()) continue;
			
			ItemWithCount breakdown = extractBreakdown(recipe);
			if (breakdown == null) continue;
			
			UNPACKING_MAP.putIfAbsent(
				result.getItem(),
				breakdown
			);
		}
	}
	
	public static boolean hasRecipe(Item item) {
		return UNPACKING_MAP.containsKey(item);
	}
	
	public static ItemWithCount getBreakdown(Item item) {
		return UNPACKING_MAP.get(item);
	}
	
	private static ItemWithCount extractBreakdown(Recipe<?> recipe) {
		List<Ingredient> ings;
		
		if (recipe instanceof ShapedRecipe shaped) {
			ings = shaped.getIngredients().stream().filter(i -> !i.isEmpty()).toList();
		}
		else if (recipe instanceof ShapelessRecipe shapeless) {
			ings = shapeless.getIngredients().stream().filter(i -> !i.isEmpty()).toList();
		}
		else return null;
		
		if (ings.isEmpty()) return null;
		
		for (ItemStack candidate : ings.get(0).getItems()) {
			boolean ok = true;
			for (Ingredient ing : ings) {
				if (!ing.test(candidate) || candidate.hasCraftingRemainingItem()) {
					ok = false;
					break;
				}
			}
			if (ok) {
				return new ItemWithCount(candidate.getItem(), ings.size());
			}
		}
		return null;
	}
}