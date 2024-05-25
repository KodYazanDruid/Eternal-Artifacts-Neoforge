package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.MeatPackerCategory;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MeatPackerCategory.MEAT_PACKER_CATEGORY);

        registry.addWorkstation(MeatPackerCategory.MEAT_PACKER_CATEGORY, EmiStack.of(ModBlocks.MEAT_PACKER));

        registry.addRecipe(new MeatPackerCategory());
    }
}
