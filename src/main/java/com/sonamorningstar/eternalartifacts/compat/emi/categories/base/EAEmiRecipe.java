package com.sonamorningstar.eternalartifacts.compat.emi.categories.base;

import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.RecipeDeferredHolder;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public abstract class EAEmiRecipe extends BasicEmiRecipe {
    public EAEmiRecipe(EmiRecipeCategory category, ResourceLocation id, int width, int height) {
        super(category, id, width, height);
    }

    protected static EmiRecipeCategory createCategory(RecipeDeferredHolder<?, ?> recipeHolder, MachineDeferredHolder<?, ?, ?, ?> machineHolder) {
        return new EmiRecipeCategory(recipeHolder.getKey(), EmiStack.of(machineHolder));
    }
}
