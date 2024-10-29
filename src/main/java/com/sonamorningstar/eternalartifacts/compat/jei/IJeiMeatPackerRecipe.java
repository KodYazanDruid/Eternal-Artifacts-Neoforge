package com.sonamorningstar.eternalartifacts.compat.jei;

import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.world.item.ItemStack;

public interface IJeiMeatPackerRecipe {
    default FluidIngredient getInput() {
        return FluidIngredient.of(ModTags.Fluids.MEAT, 250);
    }

    default ItemStack getOutput()  {
        return ModItems.RAW_MEAT_INGOT.toStack();
    }
}
