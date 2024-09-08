package com.sonamorningstar.eternalartifacts.content.recipe.base;

import com.sonamorningstar.eternalartifacts.registrar.RecipeDeferredHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

@Getter
public abstract class BasicItemToItemRecipe extends BasicRecipe {
    protected final Ingredient input;
    protected final ItemStack output;

    protected BasicItemToItemRecipe(RecipeDeferredHolder<? extends Container, ? extends Recipe<?>>holder, Ingredient input, ItemStack output) {
        super(holder);
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(SimpleContainer con, Level lvl) {
        NonNullList<ItemStack> stacks = con.getItems();
        for(ItemStack stack : stacks) { return input.test(stack); }
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess reg) {return output;}
}
