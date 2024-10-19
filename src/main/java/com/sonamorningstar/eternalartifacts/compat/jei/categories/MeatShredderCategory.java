package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.sonamorningstar.eternalartifacts.compat.jei.SimpleBackgroundDrawable;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Getter
public class MeatShredderCategory implements IRecipeCategory<MeatShredderRecipe> {
    public static final RecipeType<MeatShredderRecipe> recipeType = RecipeType.create(MODID, "meat_shredding", MeatShredderRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public MeatShredderCategory(IGuiHelper helper) {
        this.background = new SimpleBackgroundDrawable(116, 54);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModMachines.MEAT_SHREDDER.getItem()));
        this.title = Component.translatable(ModConstants.GUI.withSuffix("meat_shredder"));
    }

    @Override
    public RecipeType<MeatShredderRecipe> getRecipeType() {return recipeType;}

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, MeatShredderRecipe recipe, IFocusGroup focus) {
        layout.addSlot(RecipeIngredientRole.INPUT, 18, 18)
                .addIngredients(recipe.getInput());

        layout.addSlot(RecipeIngredientRole.OUTPUT, 80, 18)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutput())
                .addTooltipCallback((slotView, components) -> components.add(Component.literal(recipe.getOutput().getAmount() + " MB")));
    }
}
