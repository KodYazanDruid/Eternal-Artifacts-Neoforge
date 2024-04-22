package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.GardeningPotBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

public class ShapedRetexturedRecipe extends CustomRecipe {
    public static SimpleCraftingRecipeSerializer<ShapedRetexturedRecipe> SERIALIZER = null;

    private final RetexturedBlockItem retexturedBlockItem;

    public ShapedRetexturedRecipe(CraftingBookCategory pCategory, RetexturedBlockItem retexturedBlockItem) {
        super(pCategory);
        this.retexturedBlockItem = retexturedBlockItem;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level pLevel) {
        return !getOutput(inv).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
        return getOutput(inv);
    }

    private ItemStack getOutput(CraftingContainer inv) {
        if(inv.getItem(0).isEmpty() && inv.getItem(2).isEmpty() &&
                inv.getItem(6).isEmpty() && inv.getItem(8).isEmpty()) {
            if(inv.getItem(3).is(ModTags.Items.GARDENING_POT_SUITABLE) &&
                inv.getItem(5).is(ModTags.Items.GARDENING_POT_SUITABLE) &&
                inv.getItem(7).is(ModTags.Items.GARDENING_POT_SUITABLE) &&
                inv.getItem(3).is(inv.getItem(5).getItem()) && inv.getItem(5).is(inv.getItem(7).getItem())
            ) {
                ItemStack result = new ItemStack(retexturedBlockItem);
                if(inv.getItem(3).getItem() instanceof BlockItem bi) result = RetexturedBlockItem.setTexture(result, bi.getBlock());
                return result;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 3 && pHeight >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return getSerializer(retexturedBlockItem);
    }

    public static RecipeSerializer<ShapedRetexturedRecipe> getSerializer(RetexturedBlockItem retexturedBlockItem) {
        if(SERIALIZER == null) {
            SERIALIZER = new SimpleCraftingRecipeSerializer<>(category -> new ShapedRetexturedRecipe(category, retexturedBlockItem));
        }
        return SERIALIZER;
    }
}
