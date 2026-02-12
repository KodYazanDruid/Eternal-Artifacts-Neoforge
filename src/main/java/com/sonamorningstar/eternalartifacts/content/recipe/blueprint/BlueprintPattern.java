package com.sonamorningstar.eternalartifacts.content.recipe.blueprint;

import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleContainerCrafterWrapped;
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

    public void findRecipe(ServerPlayer player) {
        Level level = player.level();
        MinecraftServer server = level.getServer();
        if (server == null) return;
        Optional<RecipeHolder<CraftingRecipe>> optional = server.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, fakeItems, level);
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
        MinecraftServer server = level.getServer();
        if (server == null) return;
        Optional<RecipeHolder<CraftingRecipe>> optional = server.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, fakeItems, level);
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
     * Tests if the given stack matches the ingredient at the given index.
     * Uses ingredient matching (tag-based).
     */
    public boolean testForIngredient(ItemStack stack, int index) {
        if (index < 0 || index >= 9) return false;
        
        // Get the ingredient for this slot position
        ItemStack fakeItem = fakeItems.getItem(index);
        if (fakeItem.isEmpty()) {
            // Slot should be empty
            return stack.isEmpty();
        }
        
        // If recipe is null, fall back to pattern matching
        if (recipe == null) {
            return testForPattern(stack, index);
        }
        
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        
        // Find matching ingredient for this slot
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(fakeItem)) {
                return ingredient.test(stack);
            }
        }
        
        // No matching ingredient found, fall back to pattern matching
        return testForPattern(stack, index);
    }
    
    /**
     * Gets the ingredient for a specific slot index based on the fake items pattern.
     */
    @Nullable
    public Ingredient getIngredientForSlot(int index) {
        if (recipe == null || index < 0 || index >= 9) return null;
        ItemStack fakeItem = fakeItems.getItem(index);
        if (fakeItem.isEmpty()) return Ingredient.EMPTY;
        
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(fakeItem)) {
                return ingredient;
            }
        }
        return null;
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
            
            FluidStack fluidFromBucket = getFluidFromItem(fakeItem);
            if (!fluidFromBucket.isEmpty()) {
                bucketSlots.put(i, fluidFromBucket);
            }
        }
        return bucketSlots;
    }
    
    /**
     * Belirli bir slot'un sıvı kovası slotu olup olmadığını kontrol eder.
     */
    public boolean isBucketSlot(int index) {
        if (index < 0 || index >= 9) return false;
        ItemStack fakeItem = fakeItems.getItem(index);
        if (fakeItem.isEmpty()) return false;
        return !getFluidFromItem(fakeItem).isEmpty();
    }
    
    /**
     * Belirli bir slot için gereken sıvıyı döndürür.
     */
    public FluidStack getRequiredFluidForSlot(int index) {
        if (index < 0 || index >= 9) return FluidStack.EMPTY;
        ItemStack fakeItem = fakeItems.getItem(index);
        if (fakeItem.isEmpty()) return FluidStack.EMPTY;
        return getFluidFromItem(fakeItem);
    }
    
    /**
     * Bir item'dan (kova vb.) sıvı çıkarır.
     */
    private FluidStack getFluidFromItem(ItemStack stack) {
        if (stack.isEmpty()) return FluidStack.EMPTY;
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack.copy()).orElse(null);
        if (fluidHandler != null) {
            FluidStack contained = fluidHandler.getFluidInTank(0);
            if (!contained.isEmpty() && contained.getAmount() >= FluidType.BUCKET_VOLUME) {
                return new FluidStack(contained.getFluid(), FluidType.BUCKET_VOLUME);
            }
        }
        return FluidStack.EMPTY;
    }
    
    /**
     * Pattern'deki kova item'ını döndürür (remainder için kullanılır).
     */
    public ItemStack getBucketItemForSlot(int index) {
        if (index < 0 || index >= 9) return ItemStack.EMPTY;
        ItemStack fakeItem = fakeItems.getItem(index);
        if (fakeItem.isEmpty()) return ItemStack.EMPTY;
        if (!getFluidFromItem(fakeItem).isEmpty()) {
            return fakeItem.copy();
        }
        return ItemStack.EMPTY;
    }
}
