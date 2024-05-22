package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import java.util.List;

public class MeatPackerCategory extends BasicEmiRecipe {
    public MeatPackerCategory(EmiRecipeCategory category) {
        super(category, category.id, 112, 18);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(EmiIngredient.of(ModTags.Fluids.MEAT, 250), 0, 0);
        widgets.addFillingArrow(49, 0, 10000);
        widgets.addSlot(EmiStack.of(ModItems.RAW_MEAT_INGOT), 94, 0).recipeContext(this);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(ModTags.Fluids.MEAT, 250));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(ModItems.RAW_MEAT_INGOT));
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}
