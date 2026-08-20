package com.sonamorningstar.eternalartifacts.compat.emi.recipes;

import com.google.common.collect.Lists;
import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class BlueprintRecipeHandler implements StandardRecipeHandler<BlueprintMenu> {

    @Override
    public List<Slot> getInputSources(BlueprintMenu blueprintMenu) {
        List<Slot> list = Lists.newArrayList();

        for(int i = 1; i < 5; ++i) {
            list.add(blueprintMenu.getSlot(i));
        }

        int invStart = 9;

        for(int i = invStart; i < invStart + 36; ++i) {
            list.add(blueprintMenu.getSlot(i));
        }

        return list;
    }

    @Override
    public List<Slot> getCraftingSlots(BlueprintMenu blueprintMenu) {
        return blueprintMenu.slots.subList(36, 46);
    }

    public boolean supportsRecipe(EmiRecipe emiRecipe) {
        return emiRecipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && emiRecipe.supportsRecipeTree();
    }
}
