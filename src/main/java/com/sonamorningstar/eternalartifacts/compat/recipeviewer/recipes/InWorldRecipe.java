package com.sonamorningstar.eternalartifacts.compat.recipeviewer.recipes;

import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe for in-world crafting (throwing items, using items on blocks, etc.)
 */
public class InWorldRecipe extends RecipeViewerRecipe {
    private final InWorldType inWorldType;
    @Nullable
    private final Component description;
    @Nullable
    private BlockState containerBlock;
    private final List<EnvironmentCondition> environmentConditions = new ArrayList<>();
    
    public InWorldRecipe(ResourceLocation id, ResourceLocation categoryId, InWorldType type) {
        this(id, categoryId, type, null);
    }
    
    public InWorldRecipe(ResourceLocation id, ResourceLocation categoryId, InWorldType type, @Nullable Component description) {
        super(id, categoryId);
        this.inWorldType = type;
        this.description = description;
    }
    
    public InWorldType getInWorldType() {
        return inWorldType;
    }
    
    @Nullable
    public Component getDescription() {
        return description;
    }
    
    @Nullable
    public BlockState getContainerBlock() {
        return containerBlock;
    }
    
    public List<EnvironmentCondition> getEnvironmentConditions() {
        return environmentConditions;
    }
    
    /**
     * Sets the container block where items should be thrown into
     */
    public InWorldRecipe setContainerBlock(BlockState block) {
        this.containerBlock = block;
        return this;
    }
    
    /**
     * Adds an environment condition (e.g., heat source below)
     */
    public InWorldRecipe addEnvironmentCondition(EnvironmentCondition condition) {
        this.environmentConditions.add(condition);
        return this;
    }
    
    /**
     * Adds a block requirement at a specific relative position
     */
    public InWorldRecipe requireBlockAt(ConditionType position, Component description, BlockState... blocks) {
        this.environmentConditions.add(new EnvironmentCondition(
            position,
            List.of(blocks),
            description
        ));
        return this;
    }
    
    // Convenience builder methods for in-world specific patterns
    
    /**
     * Creates a "throw item into fluid" recipe
     */
    public static InWorldRecipe throwIntoFluid(ResourceLocation id, ResourceLocation categoryId,
            Ingredient thrownItem, BlockState fluidBlock, ItemStack result) {
        InWorldRecipe recipe = new InWorldRecipe(id, categoryId, InWorldType.THROW_INTO_FLUID);
        recipe.addItemInput(thrownItem);
        recipe.addBlockInput(fluidBlock);
        recipe.addItemOutput(result);
        return recipe;
    }
    
    /**
     * Creates a "use item on block" recipe
     */
    public static InWorldRecipe useOnBlock(ResourceLocation id, ResourceLocation categoryId,
            Ingredient usedItem, BlockState targetBlock, ItemStack result) {
        InWorldRecipe recipe = new InWorldRecipe(id, categoryId, InWorldType.USE_ON_BLOCK);
        recipe.addItemInput(usedItem);
        recipe.addBlockInput(targetBlock);
        recipe.addItemOutput(result);
        return recipe;
    }
    
    /**
     * Creates a "use item on block to transform it" recipe
     */
    public static InWorldRecipe useOnBlockTransform(ResourceLocation id, ResourceLocation categoryId,
            Ingredient usedItem, BlockState targetBlock, BlockState resultBlock) {
        InWorldRecipe recipe = new InWorldRecipe(id, categoryId, InWorldType.USE_ON_BLOCK);
        recipe.addItemInput(usedItem);
        recipe.addBlockInput(targetBlock);
        recipe.addBlockOutput(resultBlock);
        return recipe;
    }
    
    /**
     * Creates a "throw items to combine inside a container" recipe with environment conditions
     */
    public static InWorldRecipe throwCombineInContainer(ResourceLocation id, ResourceLocation categoryId,
            BlockState container, ItemStack result, Ingredient... items) {
        InWorldRecipe recipe = new InWorldRecipe(id, categoryId, InWorldType.THROW_COMBINE);
        recipe.setContainerBlock(container);
        for (Ingredient item : items) {
            recipe.addItemInput(item);
        }
        recipe.addItemOutput(result);
        return recipe;
    }
    
    /**
     * Creates a "throw item to combine with another" recipe
     */
    public static InWorldRecipe throwCombine(ResourceLocation id, ResourceLocation categoryId,
            Ingredient item1, Ingredient item2, ItemStack result) {
        InWorldRecipe recipe = new InWorldRecipe(id, categoryId, InWorldType.THROW_COMBINE);
        recipe.addItemInput(item1);
        recipe.addItemInput(item2);
        recipe.addItemOutput(result);
        return recipe;
    }
    
    /**
     * Creates a "lightning strike" recipe
     */
    public static InWorldRecipe lightningStrike(ResourceLocation id, ResourceLocation categoryId,
            BlockState targetBlock, BlockState resultBlock) {
        InWorldRecipe recipe = new InWorldRecipe(id, categoryId, InWorldType.LIGHTNING);
        recipe.addBlockInput(targetBlock);
        recipe.addBlockOutput(resultBlock);
        return recipe;
    }
    
    /**
     * Creates an "explosion" recipe
     */
    public static InWorldRecipe explosion(ResourceLocation id, ResourceLocation categoryId,
            Ingredient input, ItemStack result) {
        InWorldRecipe recipe = new InWorldRecipe(id, categoryId, InWorldType.EXPLOSION);
        recipe.addItemInput(input);
        recipe.addItemOutput(result);
        return recipe;
    }
    
    public enum InWorldType {
        /** Throwing an item into a fluid (e.g., throwing iron into lava) */
        THROW_INTO_FLUID,
        /** Using an item on a block (right-click) */
        USE_ON_BLOCK,
        /** Throwing items that combine (e.g., throwing items together) */
        THROW_COMBINE,
        /** Lightning strike transformation */
        LIGHTNING,
        /** Explosion-based crafting */
        EXPLOSION,
        /** Fire-based transformation */
        FIRE,
        /** Generic in-world */
        GENERIC
    }
    
    /**
     * Represents an environment condition for the recipe
     */
    public record EnvironmentCondition(
        ConditionType type,
        List<BlockState> requiredBlocks,
        Component description
    ) {}
    
    /**
     * Types of environment conditions
     */
    public enum ConditionType {
        /** Block directly below */
        BLOCK_BELOW,
        /** Block directly above */
        BLOCK_ABOVE,
        /** Adjacent blocks (any horizontal direction) */
        BLOCK_ADJACENT,
        /** Surrounding blocks (all horizontal directions) */
        BLOCK_SURROUNDING,
        /** Custom positioned block */
        CUSTOM
    }
}
