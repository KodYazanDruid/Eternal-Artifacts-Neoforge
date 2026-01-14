package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

/**
 * Common data holder for recipe viewer categories.
 * This record holds all the information needed to display a recipe category in any recipe viewer.
 */
public record RecipeViewerCategory(
    ResourceLocation id,
    Component title,
    ItemStack icon,
    @Nullable Ingredient iconIngredient,
    int width,
    int height
) {
    public RecipeViewerCategory(ResourceLocation id, Component title, ItemStack icon, int width, int height) {
        this(id, title, icon, null, width, height);
    }
    
    public RecipeViewerCategory(ResourceLocation id, Component title, Ingredient iconIngredient, int width, int height) {
        this(id, title, ItemStack.EMPTY, iconIngredient, width, height);
    }
    
    /**
     * Checks if this category uses an ingredient as icon (for tags, etc.)
     */
    public boolean usesIngredientIcon() {
        return iconIngredient != null && !iconIngredient.isEmpty();
    }
}

