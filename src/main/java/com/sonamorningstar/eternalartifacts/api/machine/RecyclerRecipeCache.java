package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.api.ItemWithCount;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecyclerRecipeCache {
	
	private static final Map<Item, CraftingRecipe> RECIPE_MAP = new HashMap<>();
	private static final Map<Item, ItemWithCount> BREAKDOWN_MAP = new HashMap<>();
	
	private RecyclerRecipeCache() {}
	
	
	public static void rebuild(RecipeManager manager, RegistryAccess access) {
		RECIPE_MAP.clear();
		BREAKDOWN_MAP.clear();
		
		var recipes = manager.getAllRecipesFor(RecipeType.CRAFTING);
		
		for (var holder : recipes) {
			var recipe = holder.value();
			ItemStack result = recipe.getResultItem(access);
			if (result == null || result.isEmpty()) continue;
			
			if (result.is(ModTags.Items.RECYCLABLE)) {
				RECIPE_MAP.put(result.getItem(), recipe);
			}
			
			if (result.hasCraftingRemainingItem()) continue;
			
			List<Ingredient> ingredients = recipe.getIngredients();
			if (ingredients.size() != 1) continue;
			
			Ingredient ing = ingredients.get(0);
			if (ing.isEmpty()) continue;
			
			if (result.getCount() > 1) {
				for (ItemStack stack : ing.getItems()) {
					BREAKDOWN_MAP.put(
						stack.getItem(),
						new ItemWithCount(result.getItem(), result.getCount())
					);
				}
			}
		}
	}
	
	public static CraftingRecipe getRecipe(Item item) {
		return RECIPE_MAP.get(item);
	}
	
	public static ItemWithCount getBreakdown(Item item) {
		return BREAKDOWN_MAP.get(item);
	}
	
	public static boolean hasRecipe(Item item) {
		return RECIPE_MAP.containsKey(item);
	}
}