package com.sonamorningstar.eternalartifacts.data.recipe;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.checkerframework.checker.units.qual.C;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder {

    public RecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        /*SpecialRecipeBuilder.special(category -> new ShapedRetexturedRecipe(category, ModItems.GARDENING_POT.get(), ModTags.Items.GARDENING_POT_SUITABLE))
                .save(recipeOutput, new ResourceLocation(MODID, "gardening_pot_recipe"));*/

        craftingRecipes(recipeOutput);
        smeltingRecipe(recipeOutput, Items.SUGAR_CANE, ModItems.SUGAR_CHARCOAL);
        createFoodCookingRecipe(recipeOutput, ModItems.RAW_MEAT_INGOT, ModItems.MEAT_INGOT);

    }

    private void craftingRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.GOLDEN_ANCIENT_FRUIT)
                .pattern("NNN").pattern("NFN").pattern("NNN")
                .define('N', Items.GOLD_INGOT).define('F', ModItems.ANCIENT_FRUIT)
                .unlockedBy("has_item", has(ModItems.ANCIENT_FRUIT))
                .save(recipeOutput);

        //CompoundTag terracottaTag = new CompoundTag();
        //terracottaTag.putString(RetexturedHelper.TEXTURE_TAG_KEY, RetexturedHelper.getTextureName(Blocks.TERRACOTTA));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.GARDENING_POT)
                .pattern(" B ").pattern("TDT").pattern(" T ")
                .define('B', Items.BONE_MEAL).define('T', Items.TERRACOTTA).define('D', Items.DIRT)
                .unlockedBy("has_item", has(Items.TERRACOTTA))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SUGAR_CHARCOAL_BLOCK)
                .pattern("SSS").pattern("SSS").pattern("SSS")
                .define('S', ModItems.SUGAR_CHARCOAL)
                .unlockedBy("has_item", has(ModItems.SUGAR_CHARCOAL))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SUGAR_CHARCOAL, 9)
                .requires(ModBlocks.SUGAR_CHARCOAL_BLOCK)
                .unlockedBy("has_item", has(ModBlocks.SUGAR_CHARCOAL_BLOCK))
                .save(recipeOutput);
    }

    private void smeltingRecipe(RecipeOutput output, ItemLike input, DeferredItem<Item> result) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, result, 0.35f, 200)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smelting/"+result.getId().getPath()));
    }

    private void createFoodCookingRecipe(RecipeOutput output, ItemLike input, DeferredItem<Item> result) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, result, 0.35f, 200)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smelting/"+result.getId().getPath()));
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, result, 0.35f, 100)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smoking/"+result.getId().getPath()));
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, result, 0.35f, 600)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "campfire_cooking/"+result.getId().getPath()));
    }

}
