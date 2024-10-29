package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.compat.jei.SimpleBackgroundDrawable;
import com.sonamorningstar.eternalartifacts.content.recipe.SqueezingRecipe;
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
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
public class MaterialSqueezerCategory implements IRecipeCategory<SqueezingRecipe> {
    public static final RecipeType<SqueezingRecipe> recipeType = new RecipeType<>(ModRecipes.SQUEEZING.getKey(), SqueezingRecipe.class);
    private final IDrawable icon;
    private final Component title;

    public MaterialSqueezerCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModMachines.MATERIAL_SQUEEZER.getItem()));
        this.title = Component.translatable(ModMachines.MATERIAL_SQUEEZER.getBlockTranslationKey());
    }

    @Override
    public RecipeType<SqueezingRecipe> getRecipeType() {return recipeType;}

    @Override
    public IDrawable getBackground() {
        SimpleBackgroundDrawable background = new SimpleBackgroundDrawable(116, 54);
        background.addItemSlot(6, 17);
        background.addItemSlot(68, 17);
        background.addSmallFluidSlot(90, 17);
        background.setArrow(Pair.of(36, 19));
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, SqueezingRecipe recipe, IFocusGroup focus) {
        layout.addSlot(RecipeIngredientRole.INPUT, 7, 18)
                .addIngredients(recipe.getInput());

        layout.addSlot(RecipeIngredientRole.OUTPUT, 69, 18)
                .addItemStack(recipe.getOutput());

        FluidStack output = recipe.getOutputFluid();
        layout.addSlot(RecipeIngredientRole.OUTPUT, 91, 18)
                .addFluidStack(output.getFluid(), output.getAmount())
                .addTooltipCallback((slotView, components) -> components.add(Component.literal(output.getAmount() + " MB")));;
    }
}
