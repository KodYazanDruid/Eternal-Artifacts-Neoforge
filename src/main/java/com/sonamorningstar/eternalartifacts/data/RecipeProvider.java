package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.content.recipe.FluidCombustionRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ShapedRetexturedRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.EntityIngredient;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder {

    private final List<ItemLike> MANGANESE_SMELTABLES = ImmutableList.of(ModBlocks.MANGANESE_ORE, ModBlocks.DEEPSLATE_MANGANESE_ORE, ModItems.RAW_MANGANESE);
    private final List<ItemLike> ARDITE_SMELTABLES = ImmutableList.of(ModBlocks.ARDITE_ORE, ModItems.RAW_ARDITE);

    public RecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        SpecialRecipeBuilder.special(category -> new ShapedRetexturedRecipe(category, ModItems.GARDENING_POT.get(), ModTags.Items.GARDENING_POT_SUITABLE))
                .save(recipeOutput, new ResourceLocation(MODID, "gardening_pot_recipe"));

        craftingRecipes(recipeOutput);
        smeltingRecipe(recipeOutput, Items.SUGAR_CANE, ModItems.SUGAR_CHARCOAL, 1.0f);
        createFoodCookingRecipe(recipeOutput, ModItems.RAW_MEAT_INGOT, ModItems.MEAT_INGOT, 0.35f);
        createFoodCookingRecipe(recipeOutput, ModItems.DUCK_MEAT, ModItems.COOKED_DUCK_MEAT, 0.35f);
        createOreSmeltingRecipe(recipeOutput, ModBlocks.GRAVEL_COAL_ORE, Items.COAL, 0.1f);
        createOreSmeltingRecipe(recipeOutput, ModBlocks.GRAVEL_COPPER_ORE, Items.COPPER_INGOT, 0.7f);
        createOreSmeltingRecipe(recipeOutput, ModBlocks.GRAVEL_IRON_ORE, Items.IRON_INGOT, 0.7f);
        createOreSmeltingRecipe(recipeOutput, ModBlocks.GRAVEL_GOLD_ORE, Items.GOLD_INGOT, 1.0f);
        createOreSmeltingRecipe(recipeOutput, MANGANESE_SMELTABLES, ModItems.MANGANESE_INGOT, 0.7f);
        createOreSmeltingRecipe(recipeOutput, ARDITE_SMELTABLES, ModItems.ARDITE_INGOT, 1.0f);

        copySmithingTemplate(recipeOutput, ModItems.CHLOROPHYTE_UPGRADE_SMITHING_TEMPLATE, ModItems.CHLOROPHYTE_TABLET);
        chlorophyteSmithing(recipeOutput, ModItems.COPPER_SWORD.get(), RecipeCategory.TOOLS, ModItems.SWORD_OF_THE_GREEN_EARTH.get());
        chlorophyteSmithing(recipeOutput, ModItems.COPPER_PICKAXE.get(), RecipeCategory.TOOLS, ModItems.CHLOROVEIN_PICKAXE.get());
        chlorophyteSmithing(recipeOutput, ModItems.COPPER_AXE.get(), RecipeCategory.TOOLS, ModItems.AXE_OF_REGROWTH.get());
        chlorophyteSmithing(recipeOutput, ModItems.COPPER_SHOVEL.get(), RecipeCategory.TOOLS, ModItems.NATURAL_SPADE.get());
        chlorophyteSmithing(recipeOutput, ModItems.COPPER_HOE.get(), RecipeCategory.TOOLS, ModItems.LUSH_GRUBBER.get());
        chlorophyteSmithing(recipeOutput, ModItems.COPPER_HAMMER.get(), RecipeCategory.TOOLS, ModItems.HAMMAXE.get());
        netheriteSmithing(recipeOutput, ModItems.DIAMOND_HAMMER.get(), RecipeCategory.TOOLS, ModItems.NETHERITE_HAMMER.get());

        createMeatShredderRecipe(recipeOutput, ModTags.Items.INGOTS_RAW_MEAT, 250);
        createMeatShredderRecipe(recipeOutput, Items.BEEF.getDefaultInstance(), 250);
        createMeatShredderRecipe(recipeOutput, Items.PORKCHOP.getDefaultInstance(), 250);
        createMeatShredderRecipe(recipeOutput, Items.CHICKEN.getDefaultInstance(), 200);
        createMeatShredderRecipe(recipeOutput, ModItems.DUCK_MEAT.toStack(), 200);
        createMeatShredderRecipe(recipeOutput, Items.MUTTON.getDefaultInstance(), 200);
        createMeatShredderRecipe(recipeOutput, Items.RABBIT.getDefaultInstance(), 200);
        createMeatShredderRecipe(recipeOutput, Items.COD.getDefaultInstance(), 125);
        createMeatShredderRecipe(recipeOutput, Items.SALMON.getDefaultInstance(), 125);
        createMeatShredderRecipe(recipeOutput, Items.TROPICAL_FISH.getDefaultInstance(), 100);
        createMeatShredderRecipe(recipeOutput, Items.ROTTEN_FLESH.getDefaultInstance(),20);

        createFluidCombustionRecipe(recipeOutput, Fluids.LAVA, 40, 100);
        createFluidCombustionRecipe(recipeOutput, ModFluids.PINK_SLIME.get(), 80, 60);
        //createFluidCombustionRecipe(recipeOutput, ModTags.Fluids.EXPERIENCE, 40, 2500);

        createMobLiquifyingRecipe(recipeOutput, EntityType.COW, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.get().getSource(), 40),
                new FluidStack(ModFluids.LIQUID_MEAT.get().getSource(), 75),
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 25),
                new FluidStack(ModFluids.NOUS.get().getSource(), 20)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.SHEEP, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.get().getSource(), 35),
                new FluidStack(ModFluids.LIQUID_MEAT.get().getSource(), 50),
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 20),
                new FluidStack(ModFluids.NOUS.get().getSource(), 20)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.PIG, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.get().getSource(), 30),
                new FluidStack(ModFluids.LIQUID_MEAT.get().getSource(), 75),
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 25),
                new FluidStack(ModFluids.NOUS.get().getSource(), 20)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.CHICKEN, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.get().getSource(), 10),
                new FluidStack(ModFluids.LIQUID_MEAT.get().getSource(), 25),
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 10),
                new FluidStack(ModFluids.NOUS.get().getSource(), 15)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.ZOMBIE, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.get().getSource(), 10),
                new FluidStack(ModFluids.LIQUID_MEAT.get().getSource(), 20),
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 10),
                new FluidStack(ModFluids.NOUS.get().getSource(), 25)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.SKELETON, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(NeoForgeMod.MILK, 75),
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 5),
                new FluidStack(ModFluids.NOUS.get().getSource(), 25)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.BLAZE, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(Fluids.LAVA.getSource(), 75),
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 35),
                new FluidStack(ModFluids.NOUS.get().getSource(), 25)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.ENDERMAN, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 50),
                new FluidStack(ModFluids.NOUS.get().getSource(), 35)
        ));
        createMobLiquifyingRecipe(recipeOutput, ModEntities.DUCK.get(), NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.get().getSource(), 10),
                new FluidStack(ModFluids.LIQUID_MEAT.get().getSource(), 25),
                new FluidStack(ModFluids.PINK_SLIME.get().getSource(), 10),
                new FluidStack(ModFluids.NOUS.get().getSource(), 15)
        ));


    }

    private void craftingRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.GOLDEN_ANCIENT_FRUIT)
                .pattern("NNN").pattern("NFN").pattern("NNN")
                .define('N', Items.GOLD_INGOT).define('F', ModItems.ANCIENT_FRUIT)
                .unlockedBy("has_item", has(ModItems.ANCIENT_FRUIT)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SUGAR_CHARCOAL_BLOCK)
                .pattern("SSS").pattern("SSS").pattern("SSS")
                .define('S', ModItems.SUGAR_CHARCOAL)
                .unlockedBy("has_item", has(ModItems.SUGAR_CHARCOAL)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PINK_SLIME_BLOCK)
                .pattern("SSS").pattern("SSS").pattern("SSS")
                .define('S', ModItems.PINK_SLIME)
                .unlockedBy("has_item", has(ModItems.PINK_SLIME)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STONE_TABLET)
                .pattern("SS").pattern("SS")
                .define('S', Blocks.SMOOTH_STONE)
                .unlockedBy("has_item", has(Blocks.SMOOTH_STONE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENDER_TABLET)
                .pattern("ESE")
                .define('S', ModItems.STONE_TABLET).define('E', Items.ENDER_PEARL)
                .unlockedBy("has_item", has(ModItems.STONE_TABLET)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_SWORD)
                .pattern("I").pattern("I").pattern("S")
                .define('I', Tags.Items.INGOTS_COPPER).define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_PICKAXE)
                .pattern("III").pattern(" S ").pattern(" S ")
                .define('I', Tags.Items.INGOTS_COPPER).define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_AXE)
                .pattern("II").pattern("IS").pattern(" S")
                .define('I', Tags.Items.INGOTS_COPPER).define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_SHOVEL)
                .pattern("I").pattern("S").pattern("S")
                .define('I', Tags.Items.INGOTS_COPPER).define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COPPER_HOE)
                .pattern("II").pattern(" S").pattern(" S")
                .define('I', Tags.Items.INGOTS_COPPER)
                .define('S', Items.STICK)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHLOROPHYTE_TABLET)
                .pattern("ESE")
                .define('S', ModItems.STONE_TABLET).define('E', ModItems.CHLOROPHYTE_INGOT)
                .unlockedBy("has_item", has(ModItems.STONE_TABLET)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_TABLET)
                .pattern("CBC")
                .define('B', Items.COPPER_BLOCK).define('C', Items.COPPER_INGOT)
                .unlockedBy("has_item", has(Items.COPPER_INGOT)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.COPPER_INGOT)
                .pattern("TTT").pattern("TNT").pattern("TTT")
                .define('T', ModTags.Items.NUGGETS_COPPER)
                .define('N', ModItems.COPPER_NUGGET)
                .unlockedBy("has_item", has(ModTags.Items.NUGGETS_COPPER)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STEEL_INGOT)
                .pattern("NNN").pattern("NMN").pattern("NNN")
                .define('N', ModTags.Items.NUGGETS_STEEL).define('M', ModItems.STEEL_NUGGET)
                .unlockedBy("has_item", has(ModTags.Items.NUGGETS_STEEL)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MANGANESE_INGOT)
                .pattern("NNN").pattern("NMN").pattern("NNN")
                .define('N', ModTags.Items.NUGGETS_MANGANESE).define('M', ModItems.MANGANESE_NUGGET)
                .unlockedBy("has_item", has(ModTags.Items.NUGGETS_MANGANESE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BATTERY)
                .pattern("CMC").pattern("PRP").pattern("PCP")
                .define('C', Tags.Items.INGOTS_COPPER).define('M', ModTags.Items.INGOTS_MANGANESE)
                .define('P', ModTags.Items.PLASTIC).define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_item", has(ModTags.Items.PLASTIC)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BATTERY_BOX)
                .pattern("RCR").pattern("CMC").pattern("PPP")
                .define('C', ModItems.COPPER_TABLET).define('M', ModBlocks.MACHINE_BLOCK)
                .define('P', ModTags.Items.PLASTIC).define('R', Tags.Items.DUSTS_REDSTONE).
                unlockedBy("has_item", has(ModBlocks.MACHINE_BLOCK)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.JAR)
                .pattern(" W ").pattern("P P").pattern(" P ")
                .define('W', ItemTags.LOGS).define('P', Tags.Items.GLASS_PANES)
                .unlockedBy("has_item", has(Tags.Items.GLASS_PANES)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RAW_MANGANESE_BLOCK)
                .pattern("NNN").pattern("NNN").pattern("NNN")
                .define('N', ModItems.RAW_MANGANESE)
                .unlockedBy("has_item", has(ModItems.RAW_MANGANESE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CHARCOAL_BLOCK)
                .pattern("TTT").pattern("TNT").pattern("TTT")
                .define('T', ModTags.Items.CHARCOAL)
                .define('N', Items.CHARCOAL)
                .unlockedBy("has_item", has(Items.CHARCOAL)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RAW_ARDITE_BLOCK)
                .pattern("NNN").pattern("NNN").pattern("NNN")
                .define('N', ModItems.RAW_ARDITE)
                .unlockedBy("has_item", has(ModItems.RAW_ARDITE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ARDITE_BLOCK)
                .pattern("NNN").pattern("NMN").pattern("NNN")
                .define('N', ModTags.Items.INGOTS_ARDITE)
                .define('M', ModItems.ARDITE_INGOT)
                .unlockedBy("has_item", has(ModTags.Items.INGOTS_ARDITE)).save(recipeOutput);
        createHammerRecipe(recipeOutput, ModItems.WOODEN_HAMMER, ItemTags.PLANKS, ItemTags.LOGS);
        createHammerRecipe(recipeOutput, ModItems.STONE_HAMMER, Items.COBBLESTONE, Items.SMOOTH_STONE);
        createHammerRecipe(recipeOutput, ModItems.COPPER_HAMMER, Tags.Items.INGOTS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER);
        createHammerRecipe(recipeOutput, ModItems.IRON_HAMMER, Tags.Items.INGOTS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
        createHammerRecipe(recipeOutput, ModItems.GOLDEN_HAMMER, Tags.Items.INGOTS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
        createHammerRecipe(recipeOutput, ModItems.DIAMOND_HAMMER, Tags.Items.GEMS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SUGAR_CHARCOAL, 9)
                .requires(ModBlocks.SUGAR_CHARCOAL_BLOCK)
                .unlockedBy("has_item", has(ModBlocks.SUGAR_CHARCOAL_BLOCK)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ENDER_NOTEBOOK)
                .requires(Items.FEATHER).requires(ModItems.ENDER_TABLET).requires(Items.DIAMOND)
                .unlockedBy("has_item", has(ModItems.ENDER_TABLET)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.COPPER_NUGGET, 9)
                .requires(Items.COPPER_INGOT)
                .unlockedBy("has_item", has(ModTags.Items.NUGGETS_COPPER)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.STEEL_NUGGET, 9)
                .requires(ModTags.Items.INGOTS_STEEL)
                .unlockedBy("has_item", has(ModTags.Items.NUGGETS_STEEL)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MANGANESE_NUGGET, 9)
                .requires(ModTags.Items.INGOTS_MANGANESE)
                .unlockedBy("has_item", has(ModTags.Items.NUGGETS_MANGANESE)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RAW_MANGANESE, 9)
                .requires(ModBlocks.RAW_MANGANESE_BLOCK)
                .unlockedBy("has_item", has(ModBlocks.RAW_MANGANESE_BLOCK)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CHARCOAL, 9)
                .requires(ModBlocks.CHARCOAL_BLOCK)
                .unlockedBy("has_item", has(ModBlocks.CHARCOAL_BLOCK)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RAW_ARDITE, 9)
                .requires(ModBlocks.RAW_ARDITE_BLOCK)
                .unlockedBy("has_item", has(ModBlocks.RAW_ARDITE_BLOCK)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ARDITE_INGOT, 9)
                .requires(ModBlocks.ARDITE_BLOCK)
                .unlockedBy("has_item", has(ModBlocks.ARDITE_BLOCK)).save(recipeOutput);

    }

    private void smeltingRecipe(RecipeOutput output, ItemLike input, ItemLike result, float xp) {
        ResourceLocation resultId = BuiltInRegistries.ITEM.getKey(result.asItem());
        ResourceLocation inputId = BuiltInRegistries.ITEM.getKey(input.asItem());
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, result, xp, 200)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smelting/"+resultId.getPath()+"_from_"+inputId.getPath()));
    }

    private void createFoodCookingRecipe(RecipeOutput output, ItemLike input, ItemLike result, float xp) {
        ResourceLocation resultId = BuiltInRegistries.ITEM.getKey(result.asItem());
        ResourceLocation inputId = BuiltInRegistries.ITEM.getKey(input.asItem());
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, result, xp, 200)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smelting/"+resultId.getPath()+"_from_"+inputId.getPath()));
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, result, xp, 100)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smoking/"+resultId.getPath()+"_from_"+inputId.getPath()));
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, result, xp, 600)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "campfire_cooking/"+resultId.getPath()+"_from_"+inputId.getPath()));
    }

    private void createOreSmeltingRecipe(RecipeOutput output, List<ItemLike> ingredients, ItemLike result, float xp) {
        for(ItemLike item : ingredients) createOreSmeltingRecipe(output, item, result, xp);
    }

    private void createOreSmeltingRecipe(RecipeOutput output, ItemLike input, ItemLike result, float xp) {
        ResourceLocation resultId = BuiltInRegistries.ITEM.getKey(result.asItem());
        ResourceLocation inputId = BuiltInRegistries.ITEM.getKey(input.asItem());
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, result, xp, 200)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "smelting/"+resultId.getPath()+"_from_"+inputId.getPath()));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, result, xp, 100)
                .unlockedBy("has_item", has(input))
                .save(output, new ResourceLocation(MODID, "blasting/"+resultId.getPath()+"_from_"+inputId.getPath()));
    }

    private void chlorophyteSmithing(RecipeOutput recipeOutput, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.CHLOROPHYTE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(ModItems.CHLOROPHYTE_INGOT), category, resultItem
                )
                .unlocks("has_item", has(ModItems.CHLOROPHYTE_INGOT))
                .save(recipeOutput, new ResourceLocation(MODID, "smithing/"+getItemName(resultItem)+"_smithing"));
    }

    private void createHammerRecipe(RecipeOutput output, ItemLike result, ItemLike firstIng, ItemLike secondIng) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result)
                .pattern("FFF").pattern("FSF").pattern(" R ")
                .define('F', firstIng)
                .define('S', secondIng)
                .define('R', Items.STICK)
                .unlockedBy("has_item", has(firstIng)).save(output);
    }
    private void createHammerRecipe(RecipeOutput output, ItemLike result, TagKey<Item> firstIng, TagKey<Item> secondIng) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result)
                .pattern("FFF").pattern("FSF").pattern(" R ")
                .define('F', firstIng)
                .define('S', secondIng)
                .define('R', Items.STICK)
                .unlockedBy("has_item", has(firstIng)).save(output);
    }

    private void createMeatShredderRecipe(RecipeOutput recipeOutput, ItemStack input, int output) {
        String path = BuiltInRegistries.ITEM.getKey(input.getItem()).getPath();
        SpecialRecipeBuilder.special(category -> new MeatShredderRecipe(Ingredient.of(input), new FluidStack(ModFluids.LIQUID_MEAT, output)))
                .save(recipeOutput, new ResourceLocation(MODID, "meat_shredding/"+path));
    }
    private void createMeatShredderRecipe(RecipeOutput recipeOutput, TagKey<Item> input, int output) {
        SpecialRecipeBuilder.special(category -> new MeatShredderRecipe(Ingredient.of(input), new FluidStack(ModFluids.LIQUID_MEAT, output)))
                .save(recipeOutput, new ResourceLocation(MODID, "meat_shredding/"+input.location().getPath()));
    }

    private void createMobLiquifyingRecipe(RecipeOutput recipeOutput, EntityType<?> entity, NonNullList<FluidStack> outputs) {
        String path = BuiltInRegistries.ENTITY_TYPE.getKey(entity).getPath();
        SpecialRecipeBuilder.special(category -> new MobLiquifierRecipe(EntityIngredient.of(entity), outputs))
                .save(recipeOutput, new ResourceLocation(MODID, "mob_liquifying/"+path));
    }
    private void createMobLiquifyingRecipe(RecipeOutput recipeOutput, TagKey<EntityType<?>> entity, NonNullList<FluidStack> outputs) {
        SpecialRecipeBuilder.special(category -> new MobLiquifierRecipe(EntityIngredient.of(entity), outputs))
                .save(recipeOutput, new ResourceLocation(MODID, "mob_liquifying/"+entity.location().getPath()));
    }

    private void createFluidCombustionRecipe(RecipeOutput output, Fluid fluid, int generation, int duration) {
        String path = BuiltInRegistries.FLUID.getKey(fluid).getPath();
        SpecialRecipeBuilder.special(category -> new FluidCombustionRecipe(FluidIngredient.of(new FluidStack(fluid, 1000)), generation, duration))
                .save(output, new ResourceLocation(MODID, "fluid_combusting/"+path));
    }
    private void createFluidCombustionRecipe(RecipeOutput output, TagKey<Fluid> fluid, int generation, int duration) {
        //String path = BuiltInRegistries.FLUID.getKey(fluid).getPath();
        SpecialRecipeBuilder.special(category -> new FluidCombustionRecipe(FluidIngredient.of(fluid), generation, duration))
                .save(output, new ResourceLocation(MODID, "fluid_combusting/"+fluid.location().getPath()));
    }

}
