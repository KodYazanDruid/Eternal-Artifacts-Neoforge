package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.MeatPackerCategory;
import com.sonamorningstar.eternalartifacts.compat.emi.categories.MeatShredderCategory;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MeatPackerCategory.MEAT_PACKER_CATEGORY);
        registry.addCategory(MeatShredderCategory.MEAT_SHREDDER_CATEGORY);

        registry.addWorkstation(MeatPackerCategory.MEAT_PACKER_CATEGORY, EmiStack.of(ModBlocks.MEAT_PACKER));
        registry.addWorkstation(MeatShredderCategory.MEAT_SHREDDER_CATEGORY, EmiStack.of(ModBlocks.MEAT_SHREDDER));

        registry.addRecipe(new MeatPackerCategory());

        RecipeManager manager = registry.getRecipeManager();
        for(MeatShredderRecipe recipe : manager.getAllRecipesFor(ModRecipes.MEAT_SHREDDING_TYPE.get()).stream().map(RecipeHolder::value).toList()) {
            registry.addRecipe(new MeatShredderCategory(recipe));
        }
    }
}
