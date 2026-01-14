package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

import com.sonamorningstar.eternalartifacts.compat.recipeviewer.recipes.CauldronRecipe;
import com.sonamorningstar.eternalartifacts.content.block.BluePlasticCauldronBlock;
import com.sonamorningstar.eternalartifacts.content.block.NaphthaCauldronBlock;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

/**
 * Provides recipes for recipe viewers.
 * Add your in-world and cauldron recipes here.
 */
public final class ModRecipeViewerRecipes {
    
    private ModRecipeViewerRecipes() {}
    
    /**
     * Register all recipes to the registry.
     * Call this after categories are registered.
     */
    public static void registerAll() {
        registerInWorldRecipes();
        registerCauldronRecipes();
    }
    
    private static void registerInWorldRecipes() {
        // Example: Add your in-world recipes here
        // InWorldRecipe.throwIntoFluid(...)
        // InWorldRecipe.useOnBlock(...)
    }
    
    private static void registerCauldronRecipes() {
        RecipeViewerRegistry.registerRecipe(CauldronRecipe.transformCauldron(
            new ResourceLocation(MODID, "cauldron/naphtha_to_plastic"),
            ModRecipeViewerCategories.CAULDRON_ID,
            SizedIngredient.of(Tags.Items.SAND, 16).toIngredient(),
            ModBlocks.NAPHTHA_CAULDRON.get().defaultBlockState().setValue(NaphthaCauldronBlock.LEVEL, NaphthaCauldronBlock.MAX_LEVEL),
            ModBlocks.PLASTIC_CAULDRON.get().defaultBlockState()
        ));
        RecipeViewerRegistry.registerRecipe(CauldronRecipe.transformCauldron(
            new ResourceLocation(MODID, "cauldron/dye_blue"),
            ModRecipeViewerCategories.CAULDRON_ID,
            Ingredient.of(Tags.Items.DYES_BLUE),
            ModBlocks.PLASTIC_CAULDRON.get().defaultBlockState(),
            ModBlocks.BLUE_PLASTIC_CAULDRON.get().defaultBlockState().setValue(BluePlasticCauldronBlock.LEVEL, BluePlasticCauldronBlock.MAX_LEVEL)
        ));
    }
}
