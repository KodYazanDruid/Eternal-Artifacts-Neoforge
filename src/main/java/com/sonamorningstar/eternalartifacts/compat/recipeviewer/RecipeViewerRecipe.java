package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Common data holder for recipe viewer recipes.
 * This class provides a flexible way to define recipes with various input/output types.
 */
public class RecipeViewerRecipe {
    private final ResourceLocation id;
    private final ResourceLocation categoryId;
    private final List<RecipeSlot> inputs = new ArrayList<>();
    private final List<RecipeSlot> outputs = new ArrayList<>();
    private final List<RecipeSlot> catalysts = new ArrayList<>();
    @Nullable
    private Integer processingTime;
    @Nullable
    private Integer energyCost;
    
    public RecipeViewerRecipe(ResourceLocation id, ResourceLocation categoryId) {
        this.id = id;
        this.categoryId = categoryId;
    }
    
    public ResourceLocation getId() { return id; }
    public ResourceLocation getCategoryId() { return categoryId; }
    public List<RecipeSlot> getInputs() { return inputs; }
    public List<RecipeSlot> getOutputs() { return outputs; }
    public List<RecipeSlot> getCatalysts() { return catalysts; }
    @Nullable public Integer getProcessingTime() { return processingTime; }
    @Nullable public Integer getEnergyCost() { return energyCost; }
    
    // Builder-style methods
    public RecipeViewerRecipe addItemInput(Ingredient ingredient) {
        inputs.add(new RecipeSlot(SlotType.ITEM_INGREDIENT, ingredient, null, null, null, 1, 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe addItemInput(ItemStack stack) {
        inputs.add(new RecipeSlot(SlotType.ITEM_STACK, null, stack, null, null, stack.getCount(), 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe addFluidInput(FluidStack fluid) {
        inputs.add(new RecipeSlot(SlotType.FLUID, null, null, fluid, null, fluid.getAmount(), 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe addBlockInput(BlockState block) {
        inputs.add(new RecipeSlot(SlotType.BLOCK, null, null, null, block, 1, 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe addItemOutput(ItemStack stack) {
        outputs.add(new RecipeSlot(SlotType.ITEM_STACK, null, stack, null, null, stack.getCount(), 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe addItemOutput(ItemStack stack, float chance) {
        outputs.add(new RecipeSlot(SlotType.ITEM_STACK, null, stack, null, null, stack.getCount(), chance));
        return this;
    }
    
    public RecipeViewerRecipe addFluidOutput(FluidStack fluid) {
        outputs.add(new RecipeSlot(SlotType.FLUID, null, null, fluid, null, fluid.getAmount(), 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe addBlockOutput(BlockState block) {
        outputs.add(new RecipeSlot(SlotType.BLOCK, null, null, null, block, 1, 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe addCatalyst(Ingredient ingredient) {
        catalysts.add(new RecipeSlot(SlotType.ITEM_INGREDIENT, ingredient, null, null, null, 1, 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe addCatalyst(ItemStack stack) {
        catalysts.add(new RecipeSlot(SlotType.ITEM_STACK, null, stack, null, null, stack.getCount(), 1.0f));
        return this;
    }
    
    public RecipeViewerRecipe processingTime(int ticks) {
        this.processingTime = ticks;
        return this;
    }
    
    public RecipeViewerRecipe energyCost(int energy) {
        this.energyCost = energy;
        return this;
    }
    
    /**
     * Represents a single slot in a recipe (input, output, or catalyst)
     */
    public record RecipeSlot(
        SlotType type,
        @Nullable Ingredient ingredient,
        @Nullable ItemStack itemStack,
        @Nullable FluidStack fluidStack,
        @Nullable BlockState blockState,
        int amount,
        float chance
    ) {
        public boolean isItem() {
            return type == SlotType.ITEM_INGREDIENT || type == SlotType.ITEM_STACK;
        }
        
        public boolean isFluid() {
            return type == SlotType.FLUID;
        }
        
        public boolean isBlock() {
            return type == SlotType.BLOCK;
        }
        
        public boolean hasChance() {
            return chance < 1.0f && chance > 0.0f;
        }
    }
    
    public enum SlotType {
        ITEM_INGREDIENT,
        ITEM_STACK,
        FLUID,
        BLOCK
    }
}

