package com.sonamorningstar.eternalartifacts.data.recipe;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
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
        SpecialRecipeBuilder.special(category -> new MeatPackerRecipe(ModItems.RAW_MEAT_INGOT.toStack(), ModTags.Fluids.MEAT, 250))
                    .save(recipeOutput, new ResourceLocation(MODID, "meat_from_meat_packer"));
        SpecialRecipeBuilder.special(category -> new MeatPackerRecipe(ModItems.BANANA_CREAM_PIE.toStack(), Tags.Fluids.MILK, 400))
                .save(recipeOutput, new ResourceLocation(MODID, "test_for_jei"));

        craftingRecipes(recipeOutput);
        smeltingRecipe(recipeOutput, Items.SUGAR_CANE, ModItems.SUGAR_CHARCOAL, 1.0f);
        createFoodCookingRecipe(recipeOutput, ModItems.RAW_MEAT_INGOT, ModItems.MEAT_INGOT, 0.35f);
        createOreSmeltingRecipe(recipeOutput, ModBlocks.GRAVEL_COAL_ORE, Items.COAL, 0.1f);
        createOreSmeltingRecipe(recipeOutput, ModBlocks.GRAVEL_COPPER_ORE, Items.COPPER_INGOT, 0.7f);
        createOreSmeltingRecipe(recipeOutput, ModBlocks.GRAVEL_IRON_ORE, Items.IRON_INGOT, 0.7f);
        createOreSmeltingRecipe(recipeOutput, ModBlocks.GRAVEL_GOLD_ORE, Items.GOLD_INGOT, 1.0f);

        copySmithingTemplate(recipeOutput, ModItems.CHLOROPHYTE_UPGRADE_SMITHING_TEMPLATE, ModItems.CHLOROPHYTE_TABLET);
        chlorophyteSmithing(recipeOutput, ModItems.COPPER_AXE.get(), RecipeCategory.TOOLS, ModItems.AXE_OF_REGROWTH.get());
        chlorophyteSmithing(recipeOutput, ModItems.COPPER_PICKAXE.get(), RecipeCategory.TOOLS, ModItems.CHLOROVEIN_PICKAXE.get());
    }

    private void craftingRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.GOLDEN_ANCIENT_FRUIT)
                .pattern("NNN").pattern("NFN").pattern("NNN")
                .define('N', Items.GOLD_INGOT).define('F', ModItems.ANCIENT_FRUIT)
                .unlockedBy("has_item", has(ModItems.ANCIENT_FRUIT))
                .save(recipeOutput);
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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PINK_SLIME_BLOCK)
                .pattern("SSS").pattern("SSS").pattern("SSS")
                .define('S', ModItems.PINK_SLIME)
                .unlockedBy("has_item", has(ModItems.PINK_SLIME))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STONE_TABLET)
                .pattern("SS").pattern("SS")
                .define('S', Blocks.SMOOTH_STONE)
                .unlockedBy("has_item", has(Blocks.SMOOTH_STONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENDER_TABLET)
                .pattern("ESE")
                .define('S', ModItems.STONE_TABLET)
                .define('E', Items.ENDER_PEARL)
                .unlockedBy("has_item", has(ModItems.STONE_TABLET))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_SWORD)
                .pattern("I")
                .pattern("I")
                .pattern("S")
                .define('I', Tags.Items.INGOTS_COPPER)
                .define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_PICKAXE)
                .pattern("III")
                .pattern(" S ")
                .pattern(" S ")
                .define('I', Tags.Items.INGOTS_COPPER)
                .define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_AXE)
                .pattern("II")
                .pattern("IS")
                .pattern(" S")
                .define('I', Tags.Items.INGOTS_COPPER)
                .define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_SHOVEL)
                .pattern("I")
                .pattern("S")
                .pattern("S")
                .define('I', Tags.Items.INGOTS_COPPER)
                .define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_HOE)
                .pattern("II")
                .pattern(" S")
                .pattern(" S")
                .define('I', Tags.Items.INGOTS_COPPER)
                .define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHLOROPHYTE_TABLET)
                .pattern("ESE")
                .define('S', ModItems.STONE_TABLET)
                .define('E', ModItems.CHLOROPHYTE_INGOT)
                .unlockedBy("has_item", has(ModItems.STONE_TABLET))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SUGAR_CHARCOAL, 9)
                .requires(ModBlocks.SUGAR_CHARCOAL_BLOCK)
                .unlockedBy("has_item", has(ModBlocks.SUGAR_CHARCOAL_BLOCK))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ENDER_NOTEBOOK)
                .requires(Items.FEATHER).requires(ModItems.ENDER_TABLET).requires(Items.DIAMOND)
                .unlockedBy("has_item", has(ModItems.ENDER_TABLET))
                .save(recipeOutput);
    }

    private void smeltingRecipe(RecipeOutput output, ItemLike input, ItemLike result, float xp) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.asItem());
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, result, xp, 200)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smelting/"+id.getPath()));
    }

    private void createFoodCookingRecipe(RecipeOutput output, ItemLike input, ItemLike result, float xp) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.asItem());
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, result, xp, 200)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smelting/"+id.getPath()));
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, result, xp, 100)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smoking/"+id.getPath()));
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, result, xp, 600)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "campfire_cooking/"+id.getPath()));
    }

    private void createOreSmeltingRecipe(RecipeOutput output, ItemLike input, ItemLike result, float xp) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.asItem());
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, result, xp, 200)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smelting/"+id.getPath()));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, result, xp, 100)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "blasting/"+id.getPath()));
    }

    private void chlorophyteSmithing(RecipeOutput recipeOutput, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.CHLOROPHYTE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(ModItems.CHLOROPHYTE_INGOT), category, resultItem
                )
                .unlocks("has_item", has(ModItems.CHLOROPHYTE_INGOT))
                .save(recipeOutput, new ResourceLocation(MODID, "smithing/"+getItemName(resultItem)+"_smithing"));
    }

}
