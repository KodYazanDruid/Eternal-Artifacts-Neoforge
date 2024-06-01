package com.sonamorningstar.eternalartifacts.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.List;

public class EmiShapedRetexturedRecipe extends EmiPatternCraftingRecipe {
    Item texture;
    ItemStack pot;
    public EmiShapedRetexturedRecipe(ResourceLocation id, Item texture, ItemStack pot) {
        super(List.of(
                EmiStack.EMPTY,
                EmiStack.of(Items.BONE_MEAL),
                EmiStack.EMPTY,
                EmiStack.of(texture),
                EmiStack.of(Items.DIRT),
                EmiStack.of(texture),
                EmiStack.EMPTY,
                EmiStack.of(texture),
                EmiStack.EMPTY
        ), EmiStack.of(pot), id, false);
        this.texture = texture;
        this.pot = pot;
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        return switch (slot) {
            case 1 -> new SlotWidget(EmiStack.of(Items.BONE_MEAL), x, y);
            case 4 -> new SlotWidget(EmiStack.of(Items.DIRT), x, y);
            case 3, 5, 7 -> new SlotWidget(EmiStack.of(texture), x, y);
            default -> new SlotWidget(EmiStack.EMPTY, x, y);
        };
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new SlotWidget(EmiStack.of(pot), x, y);
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}
