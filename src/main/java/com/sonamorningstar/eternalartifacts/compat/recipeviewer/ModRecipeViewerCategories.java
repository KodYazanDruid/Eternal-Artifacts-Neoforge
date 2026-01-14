package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

/**
 * Defines common recipe viewer categories for the mod.
 * These are registered once and used by both EMI and JEI.
 */
public final class ModRecipeViewerCategories {
    
    private ModRecipeViewerCategories() {}
    
    public static final ResourceLocation IN_WORLD_ID = new ResourceLocation(MODID, "in_world");
    public static final ResourceLocation CAULDRON_ID = new ResourceLocation(MODID, "cauldron");
    
    public static final RecipeViewerCategory IN_WORLD = new RecipeViewerCategory(
        IN_WORLD_ID,
        Component.translatable("gui." + MODID + ".category.in_world"),
        new ItemStack(Items.GRASS_BLOCK),
        120, 60
    );
    
    public static final RecipeViewerCategory CAULDRON = new RecipeViewerCategory(
        CAULDRON_ID,
        Component.translatable("gui." + MODID + ".category.cauldron"),
        new ItemStack(Items.CAULDRON),
        120, 60
    );
    
    public static void registerAll() {
        RecipeViewerRegistry.registerCategory(IN_WORLD);
        RecipeViewerRegistry.registerCategory(CAULDRON);
    }
}

