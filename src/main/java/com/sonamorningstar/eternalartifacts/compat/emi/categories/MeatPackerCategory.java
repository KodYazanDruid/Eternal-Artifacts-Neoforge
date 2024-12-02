package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MeatPackerCategory extends BasicEmiRecipe {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "fake_recipe/meat_packer");
    public static final EmiRecipeCategory MEAT_PACKER_CATEGORY = new EmiRecipeCategory(ID, EmiStack.of(ModMachines.MEAT_PACKER.getItem()));
    public MeatPackerCategory() {
        super(MEAT_PACKER_CATEGORY, MEAT_PACKER_CATEGORY.id, 72, 18);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(EmiIngredient.of(ModTags.Fluids.MEAT).setAmount(250), 0, 0);
        widgets.addFillingArrow(24, 0, 10000);
        widgets.addSlot(EmiStack.of(ModItems.RAW_MEAT_INGOT), 54, 0).recipeContext(this);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(ModTags.Fluids.MEAT).setAmount(250));
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
