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

import javax.annotation.Nullable;
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
    
    public boolean testForPattern(ItemStack stack, int index) {
        if (index >= 0 && index < 9) {
            return ItemStack.isSameItemSameTags(fakeItems.getItem(index), stack);
        }
        return false;
    }
}
