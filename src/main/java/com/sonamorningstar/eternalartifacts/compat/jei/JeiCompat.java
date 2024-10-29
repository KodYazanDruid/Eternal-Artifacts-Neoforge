package com.sonamorningstar.eternalartifacts.compat.jei;

import com.sonamorningstar.eternalartifacts.compat.jei.categories.*;
import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@JeiPlugin
public class JeiCompat implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MODID, "jei_compat");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        IIngredientSubtypeInterpreter<ItemStack> retextured = getSubtypes(RetexturedBlockItem::getTextureName);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.GARDENING_POT.asItem(), retextured);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.FANCY_CHEST.asItem(), retextured);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModMachines.INDUCTION_FURNACE.getItem().getDefaultInstance(), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(ModMachines.INDUCTION_FURNACE.getItem().getDefaultInstance(), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(ModMachines.INDUCTION_FURNACE.getItem().getDefaultInstance(), RecipeTypes.SMOKING);

        registration.addRecipeCatalyst(ModMachines.MEAT_SHREDDER.getItem().getDefaultInstance(), MeatShredderCategory.recipeType);
        registration.addRecipeCatalyst(ModMachines.INDUSTRIAL_MACERATOR.getItem().getDefaultInstance(), IndustrialMaceratorCategory.recipeType);
        registration.addRecipeCatalyst(ModMachines.FLUID_INFUSER.getItem().getDefaultInstance(), FluidInfuserCategory.recipeType);
        registration.addRecipeCatalyst(ModMachines.MELTING_CRUCIBLE.getItem().getDefaultInstance(), MelterCategory.recipeType);
        registration.addRecipeCatalyst(ModMachines.SOLIDIFIER.getItem().getDefaultInstance(), SolidifierCategory.recipeType);
        registration.addRecipeCatalyst(ModMachines.MATERIAL_SQUEEZER.getItem().getDefaultInstance(), MaterialSqueezerCategory.recipeType);
        registration.addRecipeCatalyst(ModMachines.ALLOY_SMELTER.getItem().getDefaultInstance(), AlloySmelterCategory.recipeType);
        registration.addRecipeCatalyst(ModMachines.MEAT_PACKER.getItem().getDefaultInstance(), MeatPackerCategory.recipeType);
        registration.addRecipeCatalyst(ModMachines.MOB_LIQUIFIER.getItem().getDefaultInstance(), MobLiquifierCategory.recipeType);

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new MeatShredderCategory(guiHelper));
        registration.addRecipeCategories(new IndustrialMaceratorCategory(guiHelper));
        registration.addRecipeCategories(new FluidInfuserCategory(guiHelper));
        registration.addRecipeCategories(new MelterCategory(guiHelper));
        registration.addRecipeCategories(new SolidifierCategory(guiHelper));
        registration.addRecipeCategories(new MaterialSqueezerCategory(guiHelper));
        registration.addRecipeCategories(new AlloySmelterCategory(guiHelper));
        registration.addRecipeCategories(new MeatPackerCategory(guiHelper));
        registration.addRecipeCategories(new MobLiquifierCategory(guiHelper));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(MeatShredderCategory.recipeType, getAllRecipes(ModRecipes.MEAT_SHREDDING.getType()));
        registration.addRecipes(IndustrialMaceratorCategory.recipeType, getAllRecipes(ModRecipes.MACERATING.getType()));
        registration.addRecipes(FluidInfuserCategory.recipeType, getAllRecipes(ModRecipes.FLUID_INFUSING.getType()));
        registration.addRecipes(MelterCategory.recipeType, getAllRecipes(ModRecipes.MELTING.getType()));
        registration.addRecipes(SolidifierCategory.recipeType, getAllRecipes(ModRecipes.SOLIDIFYING.getType()));
        registration.addRecipes(MaterialSqueezerCategory.recipeType, getAllRecipes(ModRecipes.SQUEEZING.getType()));
        registration.addRecipes(AlloySmelterCategory.recipeType, getAllRecipes(ModRecipes.ALLOYING.getType()));
        registration.addRecipes(MeatPackerCategory.recipeType, List.of(new IJeiMeatPackerRecipe() {}));
        registration.addRecipes(MobLiquifierCategory.recipeType, getAllRecipes(ModRecipes.MOB_LIQUIFYING.getType()));

    }

    private IIngredientSubtypeInterpreter<ItemStack> getSubtypes(Function<ItemStack, String> getter) {
        return (stack, ctx) -> {
            if(ctx == UidContext.Ingredient) return getter.apply(stack);
            return IIngredientSubtypeInterpreter.NONE;
        };
    }
    private <C extends Container, R extends Recipe<C>> List<R> getAllRecipes(RecipeType<R> recipeType) {
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager rm = level.getRecipeManager();
        return rm.getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
    }
}
