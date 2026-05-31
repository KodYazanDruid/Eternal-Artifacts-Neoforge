package com.sonamorningstar.eternalartifacts.content.recipe.blueprint;

import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleContainerCrafterWrapped;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class BlueprintPattern {
    private final SimpleContainerCrafterWrapped fakeItems;
    @Nullable
    private CraftingRecipe recipe;
    @Nullable
    private RecipeHolder<CraftingRecipe> recipeHolder;

    public BlueprintPattern(SimpleContainerCrafterWrapped fakeItems) {
        this.fakeItems = fakeItems;
    }

    //Mainly used for right-click crafting.
    public void findRecipe(ServerPlayer player) {
        Level level = player.level();
        Optional<RecipeHolder<CraftingRecipe>> optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, fakeItems, level);
        if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipeholder = optional.get();
            CraftingRecipe craftingrecipe = recipeholder.value();
            if (recipeChecks(player, level, recipeholder)) {
                recipe = craftingrecipe;
                recipeHolder = recipeholder;
            }
        }
    }
    
    public void findRecipe(Level level) {
        Optional<RecipeHolder<CraftingRecipe>> optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, fakeItems, level);
        if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipeholder = optional.get();
            CraftingRecipe craftingrecipe = recipeholder.value();
            if (recipeholder.value().isSpecial() || !level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING)) {
                recipe = craftingrecipe;
                recipeHolder = recipeholder;
            }
        }
    }

    public boolean recipeChecks(ServerPlayer player, Level level, RecipeHolder<?> recipe) {
		return recipe.value().isSpecial()
			|| !level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING)
			|| player.getRecipeBook().contains(recipe);
    }
    
    public NonNullList<Ingredient> getIngredients() {
        return recipe == null ? NonNullList.withSize(9, Ingredient.EMPTY) : recipe.getIngredients();
    }
    
    /**
     * Tests if the given stack matches the pattern at the given index.
     * Uses exact item matching.
     */
    public boolean testForPattern(ItemStack stack, int index) {
        if (index >= 0 && index < 9) {
            return ItemStack.isSameItemSameTags(fakeItems.getItem(index), stack);
        }
        return false;
    }
    
    /**
     * Blueprint pattern'deki sıvı kovası slotlarını ve gereken sıvıları döndürür.
     * @return Map&lt;slotIndex, FluidStack needed&gt;
     */
    public Map<Integer, FluidStack> getBucketFluidSlots() {
        Map<Integer, FluidStack> bucketSlots = new HashMap<>();
        
        for (int i = 0; i < 9; i++) {
            ItemStack fakeItem = fakeItems.getItem(i);
            if (fakeItem.isEmpty()) continue;
            
            FluidStack fluidFromBucket = FluidUtil.getFluidContained(fakeItem).orElse(FluidStack.EMPTY);
            if (!fluidFromBucket.isEmpty()) {
                bucketSlots.put(i, fluidFromBucket);
            }
        }
        return bucketSlots;
    }
    
    /*public Map<Ingredient, FluidIngredient> getFluidIngredients() {
        Map<Ingredient, FluidIngredient> fluidIngredients = new HashMap<>();
        if (recipe == null) return fluidIngredients;
        
        *//*for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Ingredient ingredient = recipe.getIngredients().get(i);
            ItemStack fakeItem = fakeItems.getItem(i);
            if (ingredient instanceof FluidIngredient && !fakeItem.isEmpty()) {
                FluidStack fluidFromBucket = FluidUtil.getFluidContained(fakeItem).orElse(FluidStack.EMPTY);
                if (!fluidFromBucket.isEmpty()) {
                    fluidIngredients.put(ingredient, new FluidIngredient(fluidFromBucket));
                }
            }
        }*//*
        return fluidIngredients;
    }*/
    
    public Map<Integer, FluidIngredient> getFluidIngredients() {
        Map<Integer, FluidIngredient> map = new HashMap<>();
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = fakeItems.getItem(i);
            
            if (stack.isEmpty()) {
                continue;
            }
            
            FluidStack fluid =
                FluidUtil.getFluidContained(stack)
                    .orElse(FluidStack.EMPTY);
            
            if (fluid.isEmpty()) {
                continue;
            }
            
            map.put(i, FluidIngredient.of(fluid.copy()));
        }
        
        return map;
    }
}
