package com.sonamorningstar.eternalartifacts.data.recipe;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder {

    public RecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        SpecialRecipeBuilder.special(category -> new ShapedRetexturedRecipe(category, ModItems.GARDENING_POT.get()))
                .save(pRecipeOutput, new ResourceLocation(MODID, "gardening_pot_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.GOLDEN_ANCIENT_FRUIT)
                .pattern("NNN").pattern("NFN").pattern("NNN")
                .define('N', Items.GOLD_INGOT).define('F', ModItems.ANCIENT_FRUIT)
                .unlockedBy("has_item", has(ModItems.ANCIENT_FRUIT))
                .save(pRecipeOutput);

    }

}
