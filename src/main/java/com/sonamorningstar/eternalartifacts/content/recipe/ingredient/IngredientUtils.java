package com.sonamorningstar.eternalartifacts.content.recipe.ingredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public final class IngredientUtils {
	public static boolean canIngredientSustain(ItemStack stack, Ingredient ingredient) {
		if (stack.isEmpty()) {
			return false;
		}
		for (ItemStack item : ingredient.getItems()) {
			if (item.is(stack.getItem()) && stack.getCount() >= item.getCount()) {
				return true;
			}
		}
		return false;
	}
}
