package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MeatShredderCategory extends BasicEmiRecipe {
    public static final EmiRecipeCategory MEAT_SHREDDER_CATEGORY = new EmiRecipeCategory(new ResourceLocation(MODID, "meat_shredding"), EmiStack.of(ModMachines.MEAT_SHREDDER.getItem()));
    public MeatShredderCategory(MeatShredderRecipe recipe, ResourceLocation id) {
        super(MEAT_SHREDDER_CATEGORY, id, 72, 18);
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getOutput().getFluid(), recipe.getOutput().getAmount()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addFillingArrow(24, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), 54, 0).recipeContext(this);
    }
}
