package com.sonamorningstar.eternalartifacts.compat.recipeviewer.recipes;

import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Recipe for cauldron-based interactions.
 * Supports various cauldron interaction patterns.
 */
public class CauldronRecipe extends RecipeViewerRecipe {
    private final CauldronType cauldronType;
    @Nullable
    private final BlockState cauldronState;
    private final int inputLevel;
    private final int outputLevel;
    @Nullable
    private final Component description;
    
    public CauldronRecipe(ResourceLocation id, ResourceLocation categoryId, CauldronType type) {
        this(id, categoryId, type, null, 0, 0, null);
    }
    
    public CauldronRecipe(ResourceLocation id, ResourceLocation categoryId, CauldronType type,
                          @Nullable BlockState cauldronState, int inputLevel, int outputLevel,
                          @Nullable Component description) {
        super(id, categoryId);
        this.cauldronType = type;
        this.cauldronState = cauldronState;
        this.inputLevel = inputLevel;
        this.outputLevel = outputLevel;
        this.description = description;
    }
    
    public CauldronType getCauldronType() { return cauldronType; }
    @Nullable public BlockState getCauldronState() { return cauldronState; }
    public int getInputLevel() { return inputLevel; }
    public int getOutputLevel() { return outputLevel; }
    @Nullable public Component getDescription() { return description; }
    
    // Builder pattern for common cauldron interactions
    
    /**
     * Fill cauldron with fluid from bucket
     */
    public static CauldronRecipe fillWithBucket(ResourceLocation id, ResourceLocation categoryId,
            ItemStack bucket, BlockState resultCauldron, ItemStack emptyBucket) {
        CauldronRecipe recipe = new CauldronRecipe(id, categoryId, CauldronType.FILL,
            resultCauldron, 0, 3, null);
        recipe.addItemInput(Ingredient.of(bucket));
        recipe.addItemOutput(emptyBucket);
        return recipe;
    }
    
    /**
     * Drain cauldron into bucket
     */
    public static CauldronRecipe drainWithBucket(ResourceLocation id, ResourceLocation categoryId,
                                                 BlockState cauldron, ItemStack emptyBucket, ItemStack filledBucket) {
        CauldronRecipe recipe = new CauldronRecipe(id, categoryId, CauldronType.DRAIN,
            cauldron, 3, 0, null);
        recipe.addItemInput(Ingredient.of(emptyBucket));
        recipe.addItemOutput(filledBucket);
        return recipe;
    }
    
    /**
     * Use item on cauldron to create result
     */
    public static CauldronRecipe useItem(ResourceLocation id, ResourceLocation categoryId,
            Ingredient usedItem, BlockState cauldron, int inputLvl, int outputLvl, ItemStack result) {
        CauldronRecipe recipe = new CauldronRecipe(id, categoryId, CauldronType.USE_ITEM,
            cauldron, inputLvl, outputLvl, null);
        recipe.addItemInput(usedItem);
        recipe.addItemOutput(result);
        return recipe;
    }
    
    /**
     * Use item to transform cauldron contents
     */
    public static CauldronRecipe transformCauldron(ResourceLocation id, ResourceLocation categoryId,
                                                   Ingredient usedItem, BlockState inputCauldron, BlockState outputCauldron) {
        CauldronRecipe recipe = new CauldronRecipe(id, categoryId, CauldronType.TRANSFORM,
            inputCauldron, 3, 3, null);
        recipe.addItemInput(usedItem);
        recipe.addBlockInput(inputCauldron);
        recipe.addBlockOutput(outputCauldron);
        return recipe;
    }
    
    /**
     * Dye item in cauldron
     */
    public static CauldronRecipe dyeItem(ResourceLocation id, ResourceLocation categoryId,
            Ingredient inputItem, BlockState cauldron, ItemStack resultItem) {
        CauldronRecipe recipe = new CauldronRecipe(id, categoryId, CauldronType.DYE,
            cauldron, 1, 0, null);
        recipe.addItemInput(inputItem);
        recipe.addItemOutput(resultItem);
        return recipe;
    }
    
    /**
     * Clean item in cauldron (remove dye, etc.)
     */
    public static CauldronRecipe cleanItem(ResourceLocation id, ResourceLocation categoryId,
            Ingredient inputItem, BlockState cauldron, ItemStack resultItem) {
        CauldronRecipe recipe = new CauldronRecipe(id, categoryId, CauldronType.CLEAN,
            cauldron, 1, 0, null);
        recipe.addItemInput(inputItem);
        recipe.addItemOutput(resultItem);
        return recipe;
    }
    
    public enum CauldronType {
        /** Fill cauldron with fluid */
        FILL,
        /** Drain fluid from cauldron */
        DRAIN,
        /** Use item on cauldron */
        USE_ITEM,
        /** Transform cauldron contents */
        TRANSFORM,
        /** Dye an item */
        DYE,
        /** Clean/wash an item */
        CLEAN,
        /** Generic cauldron interaction */
        GENERIC
    }
}

