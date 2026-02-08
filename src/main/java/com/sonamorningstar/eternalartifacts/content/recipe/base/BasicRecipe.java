package com.sonamorningstar.eternalartifacts.content.recipe.base;

import com.sonamorningstar.eternalartifacts.registrar.RecipeDeferredHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class BasicRecipe implements Recipe<SimpleContainer> {
    private final RecipeDeferredHolder<? extends Container, ? extends Recipe<?>> holder;

    protected BasicRecipe(RecipeDeferredHolder<? extends Container, ? extends Recipe<?>> holder) {
        this.holder = holder;
    }

    @Override
    public ItemStack assemble(SimpleContainer con, RegistryAccess reg) {return getResultItem(reg).copy();}
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}
    @Override
    public ItemStack getResultItem(RegistryAccess reg) {return ItemStack.EMPTY;}
    @Override
    public RecipeType<?> getType() {return holder.getType();}
    @Override
    public RecipeSerializer<?> getSerializer() {return holder.getSerializer();}
}
