package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.compat.jei.SimpleBackgroundDrawable;
import com.sonamorningstar.eternalartifacts.content.recipe.AlloyingRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
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
public class AlloySmelterCategory implements IRecipeCategory<AlloyingRecipe> {
    public static final RecipeType<AlloyingRecipe> recipeType = new RecipeType<>(ModRecipes.ALLOYING.getKey(), AlloyingRecipe.class);
    private final IDrawable icon;
    private final Component title;

    public AlloySmelterCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModMachines.ALLOY_SMELTER.getItem()));
        this.title = Component.translatable(ModMachines.ALLOY_SMELTER.getBlockTranslationKey());
    }

    @Override
    public RecipeType<AlloyingRecipe> getRecipeType() {return recipeType;}

    @Override
    public IDrawable getBackground() {
        SimpleBackgroundDrawable background = new SimpleBackgroundDrawable(116, 54);
        background.addItemSlot(6, 17);
        background.addItemSlot(24, 17);
        background.addItemSlot(42, 17);
        background.addItemSlot(92, 17);
        background.setArrow(Pair.of(65, 19));
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, AlloyingRecipe recipe, IFocusGroup focus) {
        for (int i = 0; i < recipe.getInputs().size(); i++) {
            SizedIngredient input = recipe.getInputs().get(i);
            layout.addSlot(RecipeIngredientRole.INPUT, 7 + 18 * i, 18)
                    .addIngredients(input.toIngredient());
        }

        layout.addSlot(RecipeIngredientRole.OUTPUT, 93, 18)
                .addItemStack(recipe.getOutput());
    }
}
