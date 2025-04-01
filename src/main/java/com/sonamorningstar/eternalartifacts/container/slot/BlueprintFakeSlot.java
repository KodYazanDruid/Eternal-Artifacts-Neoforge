package com.sonamorningstar.eternalartifacts.container.slot;

import com.sonamorningstar.eternalartifacts.content.recipe.blueprint.BlueprintPattern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BlueprintFakeSlot extends FakeSlot {
	public BlueprintPattern pattern;
	public Ingredient ingredient = Ingredient.EMPTY;
	public BlueprintFakeSlot(BlueprintPattern pattern, int index, int x, int y, boolean displayOnly) {
		super(pattern.getFakeItems(), index, x, y, displayOnly);
		this.pattern = pattern;
	}
	
	public Ingredient getRecipeIngredient() {
		if (ingredient == Ingredient.EMPTY) {
			ingredient = pattern == null ? Ingredient.EMPTY : pattern.getIngredients().get(getSlotIndex());
		}
		return ingredient;
	}
}
