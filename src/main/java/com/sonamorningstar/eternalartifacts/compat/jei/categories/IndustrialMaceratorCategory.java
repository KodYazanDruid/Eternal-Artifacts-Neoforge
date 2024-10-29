package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.compat.jei.SimpleBackgroundDrawable;
import com.sonamorningstar.eternalartifacts.content.recipe.MaceratingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@Getter
public class IndustrialMaceratorCategory implements IRecipeCategory<MaceratingRecipe> {
    public static final RecipeType<MaceratingRecipe> recipeType = new RecipeType<>(ModRecipes.MACERATING.getKey(), MaceratingRecipe.class);
    private final IDrawable icon;
    private final Component title;

    public IndustrialMaceratorCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModMachines.INDUSTRIAL_MACERATOR.getItem()));
        this.title = Component.translatable(ModMachines.INDUSTRIAL_MACERATOR.getBlockTranslationKey());
    }

    @Override
    public RecipeType<MaceratingRecipe> getRecipeType() {return recipeType;}

    @Override
    public IDrawable getBackground() {
        SimpleBackgroundDrawable background = new SimpleBackgroundDrawable(116, 54);
        background.addItemSlot(17, 17);
        background.addItemSlot(79, 17);
        background.setArrow(Pair.of(47, 19));
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, MaceratingRecipe recipe, IFocusGroup focus) {
        layout.addSlot(RecipeIngredientRole.INPUT, 18, 18)
                .addIngredients(recipe.getInput());

        layout.addSlot(RecipeIngredientRole.OUTPUT, 80, 18)
                .addItemStack(recipe.getOutput());
    }
}
