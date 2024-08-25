package com.sonamorningstar.eternalartifacts.content.recipe.base;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public abstract class NoResultItemRecipe<C extends Container> implements Recipe<C> {
    @Override
    public ItemStack assemble(C pContainer, RegistryAccess pRegistryAccess) {return ItemStack.EMPTY;}

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {return ItemStack.EMPTY;}
}
