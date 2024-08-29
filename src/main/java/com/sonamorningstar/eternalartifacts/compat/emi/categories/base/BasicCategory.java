package com.sonamorningstar.eternalartifacts.compat.emi.categories.base;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

public class BasicCategory extends BasicEmiRecipe {
    public BasicCategory(EmiRecipeCategory category, EmiIngredient input, EmiStack output, ResourceLocation id, int width, int height) {
        super(category, id, width, height);
        inputs.add(input);
        outputs.add(output);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addFillingArrow((width/2)-12, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), width-18, 0).recipeContext(this);
    }
}
