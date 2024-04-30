package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.data.recipe.ShapedRetexturedRecipe;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class GardeningPotCategory implements IRecipeCategory<ShapedRetexturedRecipe> {
    public static final RecipeType<ShapedRetexturedRecipe> RETEXTURING = RecipeType.create(MODID, "pot_retexturing", ShapedRetexturedRecipe.class);
    @Getter
    private final IDrawable background;
    @Getter
    private final IDrawable icon;
    @Getter
    private final Component title;

    public GardeningPotCategory(IGuiHelper helper) {
        ResourceLocation location = new ResourceLocation(ModIds.JEI_ID, "textures/jei/gui/gui_vanilla.png");
        this.background = helper.createDrawable(location, 0, 60, 116, 54);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.GARDENING_POT.get()));
        this.title = Component.translatable(ModConstants.GUI.withSuffix("pot_jei"));
    }

    @Override
    public RecipeType<ShapedRetexturedRecipe> getRecipeType() {
        return RETEXTURING;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShapedRetexturedRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(Items.TERRACOTTA.getDefaultInstance());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 16).addItemStack(ModItems.GARDENING_POT.toStack());
    }
}
