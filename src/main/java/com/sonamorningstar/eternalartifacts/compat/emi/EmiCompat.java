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
    public static final ResourceLocation SPRITE = new ResourceLocation(MODID, "textures/gui/meat_packer.png");
    public static final EmiStack MEAT_PACKER = EmiStack.of(ModBlocks.MEAT_PACKER);
    public static final EmiRecipeCategory MEAT_PACKER_CATEGORY = new EmiRecipeCategory(SPRITE, new EmiTexture(MEAT_PACKER.getId(), 0, 0, 16, 16));
    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MEAT_PACKER_CATEGORY);
        registry.addWorkstation(MEAT_PACKER_CATEGORY, MEAT_PACKER);

        registry.addRecipe(new MeatPackerCategory(MEAT_PACKER_CATEGORY));
    }
}
