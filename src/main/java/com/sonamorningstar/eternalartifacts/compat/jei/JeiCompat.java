package com.sonamorningstar.eternalartifacts.compat.jei;

import com.sonamorningstar.eternalartifacts.compat.jei.categories.GardeningPotCategory;
import com.sonamorningstar.eternalartifacts.compat.jei.categories.MeatPackerCategory;
import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.data.recipe.MeatPackerRecipe;
import com.sonamorningstar.eternalartifacts.data.recipe.ShapedRetexturedRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@JeiPlugin
public class JeiCompat implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MODID, "jei_compat");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        IIngredientSubtypeInterpreter<ItemStack> pots = (stack, ctx) -> {
          if(ctx == UidContext.Ingredient) return RetexturedBlockItem.getTextureName(stack);
          return IIngredientSubtypeInterpreter.NONE;
        };
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.GARDENING_POT.asItem(), pots);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        //registration.addRecipeCatalyst(new ItemStack(ModItems.GARDENING_POT.get()), GardeningPotCategory.RETEXTURING);
        registration.addRecipeCatalyst(ModBlocks.MEAT_PACKER.toStack(), MeatPackerCategory.TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        //registration.addRecipeCategories(new GardeningPotCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new MeatPackerCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager rm = level.getRecipeManager();
        //registration.addRecipes(GardeningPotCategory.RETEXTURING, List.of(new ShapedRetexturedRecipe(CraftingBookCategory.MISC, ModItems.GARDENING_POT.get(), ModTags.Items.GARDENING_POT_SUITABLE)));
        registration.addRecipes(MeatPackerCategory.TYPE, rm.getAllRecipesFor(ModRecipes.MEAT_PACKER_TYPE.get()).stream().map(RecipeHolder::value).toList());
    }
}
