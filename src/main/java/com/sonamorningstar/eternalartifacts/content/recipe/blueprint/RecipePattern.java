package com.sonamorningstar.eternalartifacts.content.recipe.blueprint;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleContainerCrafterWrapped;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.Optional;

@Getter
public class RecipePattern {
    private final SimpleContainerCrafterWrapped fakeItems;
    private CraftingRecipe recipe;
    private RecipeHolder<CraftingRecipe> recipeHolder;

    public RecipePattern(SimpleContainerCrafterWrapped fakeItems) {
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
            if (recipeChecks(level, recipeholder)) {
                recipe = craftingrecipe;
                recipeHolder = recipeholder;
            }
        }
    }

    public boolean recipeChecks(Level level, RecipeHolder<?> recipe) {
        return !(!recipe.value().isSpecial() && level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING));
    }

    public void updateFakeItems(NonNullList<ItemStack> itemStacks) {
        for (int i = 0; i < itemStacks.size(); i++) {
            fakeItems.setItem(i, itemStacks.get(i));
        }
    }
}
