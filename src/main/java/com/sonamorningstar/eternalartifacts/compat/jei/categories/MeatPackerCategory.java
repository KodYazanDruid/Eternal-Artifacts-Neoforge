package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluidTypes;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.data.recipe.MeatPackerRecipe;
import com.sonamorningstar.eternalartifacts.data.recipe.ShapedRetexturedRecipe;
import com.sonamorningstar.eternalartifacts.util.FluidTagHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MeatPackerCategory implements IRecipeCategory<MeatPackerRecipe> {
    public static final RecipeType<MeatPackerRecipe> TYPE = RecipeType.create(MODID, MeatPackerRecipe.Type.ID, MeatPackerRecipe.class);
    @Getter
    private final IDrawable background;
    @Getter
    private final IDrawable icon;
    @Getter
    private final Component title;

    public MeatPackerCategory(IGuiHelper helper) {
        ResourceLocation location = new ResourceLocation(ModIds.JEI_ID, "textures/jei/gui/gui_vanilla.png");
        this.background = helper.createDrawable(location, 0, 60, 116, 54);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModBlocks.MEAT_PACKER.toStack());
        this.title = Component.translatable(ModConstants.GUI.withSuffix("meat_packer_jei_title"));
    }

    @Override
    public RecipeType<MeatPackerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MeatPackerRecipe recipe, IFocusGroup focuses) {
        List<FluidStack> fluids = FluidTagHelper.getMatchingFluidStacks(recipe.getFluidIn(), recipe.getFluidAmount());
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(NeoForgeTypes.FLUID_STACK, fluids);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 16).addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }
}
