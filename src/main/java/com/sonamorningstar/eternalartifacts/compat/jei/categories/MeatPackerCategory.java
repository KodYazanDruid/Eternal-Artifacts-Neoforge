package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.compat.jei.IJeiMeatPackerRecipe;
import com.sonamorningstar.eternalartifacts.compat.jei.SimpleBackgroundDrawable;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Getter
public class MeatPackerCategory implements IRecipeCategory<IJeiMeatPackerRecipe> {
    public static final RecipeType<IJeiMeatPackerRecipe> recipeType = new RecipeType<>(new ResourceLocation(MODID, "meat_packing"), IJeiMeatPackerRecipe.class);
    private final IDrawable icon;
    private final Component title;

    public MeatPackerCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModMachines.MEAT_PACKER.getItem()));
        this.title = Component.translatable(ModMachines.MEAT_PACKER.getBlockTranslationKey());
    }

    @Override
    public RecipeType<IJeiMeatPackerRecipe> getRecipeType() {return recipeType;}

    @Override
    public IDrawable getBackground() {
        SimpleBackgroundDrawable background = new SimpleBackgroundDrawable(116, 54);
        background.addSmallFluidSlot(17, 17);
        background.addItemSlot(79, 17);
        background.setArrow(Pair.of(47, 19));
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, IJeiMeatPackerRecipe recipe, IFocusGroup focus) {
        IRecipeSlotBuilder slotBuilder = layout.addSlot(RecipeIngredientRole.INPUT, 18, 18);

        FluidIngredient ingredient = recipe.getInput();
        slotBuilder.addTooltipCallback((slotView, components) -> components.add(Component.literal(ingredient.getFluidStacks()[0].getAmount() + " MB")));

        for (FluidStack fluidStack : ingredient.getFluidStacks()) {
            slotBuilder.addFluidStack(fluidStack.getFluid(), fluidStack.getAmount());
        }

        layout.addSlot(RecipeIngredientRole.OUTPUT, 80, 18)
                .addItemStack(recipe.getOutput());
    }
}
