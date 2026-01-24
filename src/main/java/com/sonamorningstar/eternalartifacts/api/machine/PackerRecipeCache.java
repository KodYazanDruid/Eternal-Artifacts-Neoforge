package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.api.ItemWithCount;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PackerRecipeCache {
	
	private static final Map<Item, ItemWithCount> PACKING_MAP = new HashMap<>();
	
	private PackerRecipeCache() {}
	
	public static void rebuild(RecipeManager recipeManager, Level level) {
		PACKING_MAP.clear();
		for (var holder : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
			Recipe<?> recipe = holder.value();
			
			ItemStack result = recipe.getResultItem(level.registryAccess());
			if (result == null || result.isEmpty() || result.getCount() != 1) continue;
			
			if (recipe instanceof ShapedRecipe shaped) {
				handleShaped(shaped, result);
			}
			else if (recipe instanceof ShapelessRecipe shapeless) {
				handleShapeless(shapeless, result);
			}
		}
	}
	
	public static ItemWithCount get(Item item) {
		return PACKING_MAP.get(item);
	}
	
	public static boolean contains(Item item) {
		return PACKING_MAP.containsKey(item);
	}
	
	private static void handleShaped(ShapedRecipe shaped, ItemStack result) {
		var ings = shaped.getIngredients().stream().filter(i -> !i.isEmpty()).toList();
		if (ings.isEmpty() || !allSame(ings)) return;
		
		int w = shaped.getWidth();
		int h = shaped.getHeight();
		int size = ings.size();
		
		boolean isMiddleEmpty =
			w == 3 && h == 3 &&
				shaped.getIngredients().get(4).isEmpty();
		
		if ((w == 3 && h == 3 && size == 9) ||
			(w == 2 && h == 2 && size == 4) ||
			(w == 3 && h == 3 && size == 8 && isMiddleEmpty)) {
			fill(ings, result);
		}
	}
	
	private static void handleShapeless(ShapelessRecipe shapeless, ItemStack result) {
		var ings = shapeless.getIngredients().stream().filter(i -> !i.isEmpty()).toList();
		if (ings.isEmpty() || !allSame(ings)) return;
		
		int size = ings.size();
		if (size == 9 || size == 4 || size == 8) {
			fill(ings, result);
		}
	}
	
	private static boolean allSame(List<Ingredient> ings) {
		for (ItemStack candidate : ings.get(0).getItems()) {
			boolean ok = true;
			for (Ingredient ing : ings) {
				if (!ing.test(candidate)) {
					ok = false;
					break;
				}
			}
			if (ok) return true;
		}
		return false;
	}
	
	private static void fill(List<Ingredient> ingredients, ItemStack result) {
		Ingredient ing = ingredients.get(0);
		for (ItemStack stack : ing.getItems()) {
			if (stack.isEmpty() || stack.hasCraftingRemainingItem()) continue;
			
			PACKING_MAP.putIfAbsent(
				stack.getItem(),
				new ItemWithCount(result.getItem(), ingredients.size())
			);
		}
	}
}