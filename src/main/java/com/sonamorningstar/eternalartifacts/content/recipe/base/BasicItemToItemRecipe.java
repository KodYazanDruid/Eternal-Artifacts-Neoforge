package com.sonamorningstar.eternalartifacts.content.recipe.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

@Getter
@RequiredArgsConstructor
public abstract class BasicItemToItemRecipe implements Recipe<SimpleContainer> {
    protected final Ingredient input;
    protected final ItemStack output;
    @Override
    public boolean matches(SimpleContainer con, Level lvl) {
        NonNullList<ItemStack> stacks = con.getItems();
        for(ItemStack stack : stacks) { return input.test(stack); }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer con, RegistryAccess reg) {return getResultItem(reg);}

    @Override
    public boolean canCraftInDimensions(int width, int height) {return false;}

    @Override
    public ItemStack getResultItem(RegistryAccess reg) {return output;}
}
