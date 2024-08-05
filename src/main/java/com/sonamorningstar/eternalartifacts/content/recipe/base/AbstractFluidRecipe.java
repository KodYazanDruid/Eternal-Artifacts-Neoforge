package com.sonamorningstar.eternalartifacts.content.recipe.base;

import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractFluidRecipe implements Recipe<SimpleFluidContainer> {

    @Override
    public ItemStack assemble(SimpleFluidContainer pContainer, RegistryAccess pRegistryAccess) {return ItemStack.EMPTY;}
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {return ItemStack.EMPTY;}

}
