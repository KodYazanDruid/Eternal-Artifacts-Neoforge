package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.content.recipe.MaceratingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MaceratingCategory extends BasicEmiRecipe {
    public static final EmiRecipeCategory MACERATING_CATEGORY = new EmiRecipeCategory(new ResourceLocation(MODID, "macerating"), EmiStack.of(ModMachines.INDUSTRIAL_MACERATOR.getItem()));
    public MaceratingCategory(MaceratingRecipe recipe, ResourceLocation id) {
        super(MACERATING_CATEGORY, id, 72, 18);
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addFillingArrow((width/2)-12, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), width-18, 0).recipeContext(this);
    }
}
