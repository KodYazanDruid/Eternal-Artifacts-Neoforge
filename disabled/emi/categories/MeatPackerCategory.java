package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MeatPackerCategory extends EAEmiRecipe {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "fake_recipe/meat_packer");
    public static final EmiRecipeCategory MEAT_PACKER_CATEGORY = new EmiRecipeCategory(ID, EmiStack.of(ModMachines.MEAT_PACKER.getItem()));
    public MeatPackerCategory() {
        super(MEAT_PACKER_CATEGORY, new ResourceLocation(MODID, "/fake_recipe/meat_packer"), 76, 56);
        inputs.add(EmiIngredient.of(ModTags.Fluids.MEAT).setAmount(250));
        outputs.add(EmiStack.of(ModItems.RAW_MEAT_INGOT));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        addLargeTank(widgets, inputs.get(0), 0, 0, 500);
        widgets.addFillingArrow(24, 19, 10000);
        widgets.addSlot(outputs.get(0), 54, 19).recipeContext(this);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}
