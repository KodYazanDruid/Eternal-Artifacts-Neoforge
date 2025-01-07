package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.compat.jei.SimpleBackgroundDrawable;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidInfuserRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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
public class FluidInfuserCategory implements IRecipeCategory<FluidInfuserRecipe> {
    public static final RecipeType<FluidInfuserRecipe> recipeType = new RecipeType<>(ModRecipes.FLUID_INFUSING.getKey(), FluidInfuserRecipe.class);
    private final IDrawable icon;
    private final Component title;

    public FluidInfuserCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModMachines.FLUID_INFUSER.getItem()));
        this.title = Component.translatable(ModMachines.FLUID_INFUSER.getBlockTranslationKey());
    }

    @Override
    public RecipeType<FluidInfuserRecipe> getRecipeType() {return recipeType;}

    @Override
    public IDrawable getBackground() {
        SimpleBackgroundDrawable background = new SimpleBackgroundDrawable(116, 54);
        background.addSmallFluidSlot(6, 17);
        background.addItemSlot(28, 17);
        background.addItemSlot(90, 17);
        background.setArrow(Pair.of(58, 19));
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, FluidInfuserRecipe recipe, IFocusGroup focus) {
        IRecipeSlotBuilder slotBuilder = layout.addSlot(RecipeIngredientRole.INPUT, 7, 18);
        FluidIngredient ingredient = recipe.getInputFluid();
        slotBuilder.addTooltipCallback((slotView, components) -> components.add(Component.literal(ingredient.getFluidStacks()[0].getAmount() + " MB")));

        for (FluidStack fluidStack : ingredient.getFluidStacks()) {
            slotBuilder.addFluidStack(fluidStack.getFluid(), fluidStack.getAmount());
        }

        layout.addSlot(RecipeIngredientRole.INPUT, 29, 18)
                .addIngredients(recipe.getInput());

        layout.addSlot(RecipeIngredientRole.OUTPUT, 91, 18)
                .addItemStack(recipe.getOutput());
    }
}