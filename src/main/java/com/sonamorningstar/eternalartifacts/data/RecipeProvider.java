package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.content.recipe.*;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.EntityIngredient;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
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
        createFluidCombustionRecipe(recipeOutput, ModTags.Fluids.CRUDE_OIL, 50, 80);
        createFluidCombustionRecipe(recipeOutput, ModTags.Fluids.GASOLINE, 270, 300);
        createFluidCombustionRecipe(recipeOutput, ModTags.Fluids.DIESEL, 250, 360);

        createFluidInfusingRecipe(recipeOutput, ModTags.Fluids.EXPERIENCE, 250, Ingredient.of(Items.GLASS_BOTTLE), Items.EXPERIENCE_BOTTLE.getDefaultInstance());
        createFluidInfusingRecipe(recipeOutput, ModTags.Fluids.PINK_SLIME, 2000, Ingredient.of(Tags.Items.INGOTS_IRON), ModItems.PINK_SLIME_INGOT.toStack());

        createMeltingRecipe(recipeOutput, Items.NETHERRACK.getDefaultInstance(), new FluidStack(Fluids.LAVA, 1000));
        createMeltingRecipe(recipeOutput, Items.ICE.getDefaultInstance(), new FluidStack(Fluids.WATER, 1000));
        createMeltingRecipe(recipeOutput, ModItems.PINK_SLIME.toStack(), new FluidStack(ModFluids.PINK_SLIME.getFluid(), 250));

        createMaceratingRecipe(recipeOutput, Items.CLAY.getDefaultInstance(), new ItemStack(ModItems.CLAY_DUST.get(), 4));
        createMaceratingRecipe(recipeOutput, Items.CLAY_BALL.getDefaultInstance(), ModItems.CLAY_DUST.toStack());
        createMaceratingRecipe(recipeOutput, Items.COAL.getDefaultInstance(), ModItems.COAL_DUST.toStack());
        createMaceratingRecipe(recipeOutput, Items.CHARCOAL.getDefaultInstance(), ModItems.CHARCOAL_DUST.toStack());
        createMaceratingRecipe(recipeOutput, Items.GLOWSTONE.getDefaultInstance(), new ItemStack(Items.GLOWSTONE_DUST, 4));
        createMaceratingRecipe(recipeOutput, ModItems.SUGAR_CHARCOAL.get().getDefaultInstance(), ModItems.SUGAR_CHARCOAL_DUST.toStack());
        createMaceratingRecipe(recipeOutput, Items.COBBLESTONE.getDefaultInstance(), Items.GRAVEL.getDefaultInstance());
        createMaceratingRecipe(recipeOutput, Items.GRAVEL.getDefaultInstance(), Items.SAND.getDefaultInstance());
        createMaceratingRecipe(recipeOutput, Items.SANDSTONE.getDefaultInstance(), new ItemStack(Items.SAND, 4));
        createMaceratingRecipe(recipeOutput, Items.CUT_SANDSTONE.getDefaultInstance(), new ItemStack(Items.SAND, 4));
        createMaceratingRecipe(recipeOutput, Items.CHISELED_SANDSTONE.getDefaultInstance(), new ItemStack(Items.SAND, 4));
        createMaceratingRecipe(recipeOutput, Items.SMOOTH_SANDSTONE.getDefaultInstance(), new ItemStack(Items.SAND, 4));
        createMaceratingRecipe(recipeOutput, Items.RED_SANDSTONE.getDefaultInstance(), new ItemStack(Items.RED_SAND, 4));
        createMaceratingRecipe(recipeOutput, Items.CUT_RED_SANDSTONE.getDefaultInstance(), new ItemStack(Items.RED_SAND, 4));
        createMaceratingRecipe(recipeOutput, Items.CHISELED_RED_SANDSTONE.getDefaultInstance(), new ItemStack(Items.RED_SAND, 4));
        createMaceratingRecipe(recipeOutput, Items.SMOOTH_RED_SANDSTONE.getDefaultInstance(), new ItemStack(Items.RED_SAND, 4));
        createMaceratingRecipe(recipeOutput, Items.NETHER_BRICKS.getDefaultInstance(), new ItemStack(Items.NETHER_BRICK, 4));
        createMaceratingRecipe(recipeOutput, Items.BONE.getDefaultInstance(), new ItemStack(Items.BONE_MEAL, 5));
        createMaceratingRecipe(recipeOutput, Items.BLAZE_ROD.getDefaultInstance(), new ItemStack(Items.BLAZE_POWDER, 4));
        createMaceratingRecipe(recipeOutput, Items.QUARTZ_BLOCK.getDefaultInstance(), new ItemStack(Items.QUARTZ, 4));
        createMaceratingRecipe(recipeOutput, Items.QUARTZ_BRICKS.getDefaultInstance(), new ItemStack(Items.QUARTZ, 4));
        createMaceratingRecipe(recipeOutput, Items.QUARTZ_PILLAR.getDefaultInstance(), new ItemStack(Items.QUARTZ, 4));
        createMaceratingRecipe(recipeOutput, Items.SMOOTH_QUARTZ.getDefaultInstance(), new ItemStack(Items.QUARTZ, 4));
        createMaceratingRecipe(recipeOutput, Items.SUGAR_CANE.getDefaultInstance(), new ItemStack(Items.SUGAR, 3));
        createMaceratingRecipe(recipeOutput, Items.BEETROOT.getDefaultInstance(), new ItemStack(Items.SUGAR, 2));

        createSqueezingRecipe(recipeOutput, Items.WET_SPONGE.getDefaultInstance(), Items.SPONGE.getDefaultInstance(), new FluidStack(Fluids.WATER, 1000));
        createSqueezingRecipe(recipeOutput, Items.CACTUS.getDefaultInstance(), Items.GREEN_DYE.getDefaultInstance(), new FluidStack(Fluids.WATER, 125));

        createAlloyingRecipe(recipeOutput, List.of(
                Ingredient.of(Tags.Items.INGOTS_GOLD),
                Ingredient.of(Items.REDSTONE)),
                ModItems.DEMON_INGOT.toStack());

        createAlloyingRecipe(recipeOutput, List.of(
                Ingredient.of(ModItems.DEMON_INGOT),
                Ingredient.of(Items.GLOWSTONE_DUST),
                Ingredient.of(ModItems.PLANT_MATTER)),
                ModItems.CHLOROPHYTE_INGOT.toStack());

        createMobLiquifyingRecipe(recipeOutput, EntityType.COW, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.getFluid(), 40),
                new FluidStack(ModFluids.LIQUID_MEAT.getFluid(), 75),
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 25),
                new FluidStack(ModFluids.NOUS.getFluid(), 20)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.SHEEP, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.getFluid(), 35),
                new FluidStack(ModFluids.LIQUID_MEAT.getFluid(), 50),
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 20),
                new FluidStack(ModFluids.NOUS.getFluid(), 20)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.PIG, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.getFluid(), 30),
                new FluidStack(ModFluids.LIQUID_MEAT.getFluid(), 75),
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 25),
                new FluidStack(ModFluids.NOUS.getFluid(), 20)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.CHICKEN, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.getFluid(), 10),
                new FluidStack(ModFluids.LIQUID_MEAT.getFluid(), 25),
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 10),
                new FluidStack(ModFluids.NOUS.getFluid(), 15)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityTypeTags.ZOMBIES, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.getFluid(), 10),
                new FluidStack(ModFluids.LIQUID_MEAT.getFluid(), 20),
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 10),
                new FluidStack(ModFluids.NOUS.getFluid(), 25)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.SKELETON, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(NeoForgeMod.MILK, 75),
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 5),
                new FluidStack(ModFluids.NOUS.getFluid(), 25)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.BLAZE, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(Fluids.LAVA.getSource(), 75),
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 35),
                new FluidStack(ModFluids.NOUS.getFluid(), 25)
        ));
        createMobLiquifyingRecipe(recipeOutput, EntityType.ENDERMAN, NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 50),
                new FluidStack(ModFluids.NOUS.getFluid(), 35)
        ));
        createMobLiquifyingRecipe(recipeOutput, ModEntities.DUCK.get(), NonNullList.of(
                FluidStack.EMPTY,
                new FluidStack(ModFluids.BLOOD.getFluid(), 10),
                new FluidStack(ModFluids.LIQUID_MEAT.getFluid(), 25),
                new FluidStack(ModFluids.PINK_SLIME.getFluid(), 10),
                new FluidStack(ModFluids.NOUS.getFluid(), 15)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ICE_BRICKS)
                .pattern("II").pattern("II")
                .define('I', Blocks.ICE)
                .unlockedBy("has_item", has(Blocks.ICE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SNOW_BRICKS)
                .pattern("SS").pattern("SS")
                .define('S', Blocks.SNOW_BLOCK)
                .unlockedBy("has_item", has(Blocks.SNOW_BLOCK)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DEMONIC_TABLET)
                .pattern(" D ")
                .pattern("DSD")
                .pattern(" D ")
                .define('S', ModItems.STONE_TABLET).define('D', ModItems.DEMON_INGOT)
                .unlockedBy("has_item", has(ModItems.DEMON_INGOT)).save(recipeOutput);

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
        SpecialRecipeBuilder.special(category -> new MeatShredderRecipe(Ingredient.of(input), new FluidStack(ModFluids.LIQUID_MEAT.getStillFluidHolder(), output)))
                .save(recipeOutput, new ResourceLocation(MODID, "meat_shredding/"+path));
    }
    private void createMeatShredderRecipe(RecipeOutput recipeOutput, TagKey<Item> input, int output) {
        SpecialRecipeBuilder.special(category -> new MeatShredderRecipe(Ingredient.of(input), new FluidStack(ModFluids.LIQUID_MEAT.getStillFluidHolder(), output)))
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
        SpecialRecipeBuilder.special(category -> new FluidCombustionRecipe(FluidIngredient.of(fluid, 1000), generation, duration))
                .save(output, new ResourceLocation(MODID, "fluid_combusting/"+fluid.location().getPath()));
    }

    private void createFluidInfusingRecipe(RecipeOutput output, Fluid fluid, int fluidAmount, Ingredient ingredient, ItemStack result) {
        String path = BuiltInRegistries.ITEM.getKey(result.getItem()).getPath();
        SpecialRecipeBuilder.special(category -> new FluidInfuserRecipe(FluidIngredient.of(new FluidStack(fluid, fluidAmount)), ingredient, result))
                .save(output, new ResourceLocation(MODID, "fluid_infusing/"+path));
    }
    private void createFluidInfusingRecipe(RecipeOutput output, TagKey<Fluid> fluid, int fluidAmount, Ingredient ingredient, ItemStack result) {
        //String path = BuiltInRegistries.ITEM.getKey(result.getItem()).getPath();
        SpecialRecipeBuilder.special(category -> new FluidInfuserRecipe(FluidIngredient.of(fluid, fluidAmount), ingredient, result))
                .save(output, new ResourceLocation(MODID, "fluid_infusing/"+fluid.location().getPath()));
    }

    private void createMeltingRecipe(RecipeOutput output, ItemStack input, FluidStack result){
        String path = BuiltInRegistries.ITEM.getKey(input.getItem()).getPath();
        SpecialRecipeBuilder.special(category -> new MeltingRecipe(Ingredient.of(input), result))
                .save(output, new ResourceLocation(MODID, "melting/"+path));
    }
    private void createMeltingRecipe(RecipeOutput output, TagKey<Item> input, FluidStack result){
        SpecialRecipeBuilder.special(category -> new MeltingRecipe(Ingredient.of(input), result))
                .save(output, new ResourceLocation(MODID, "melting/"+input.location().getPath()));
    }

    private void createMaceratingRecipe(RecipeOutput output, ItemStack input, ItemStack result){
        String path = BuiltInRegistries.ITEM.getKey(input.getItem()).getPath();
        SpecialRecipeBuilder.special(category -> new MaceratingRecipe(Ingredient.of(input), result))
                .save(output, new ResourceLocation(MODID, "macerating/"+path));
    }
    private void createMaceratingRecipe(RecipeOutput output, TagKey<Item> input, ItemStack result){
        SpecialRecipeBuilder.special(category -> new MaceratingRecipe(Ingredient.of(input), result))
                .save(output, new ResourceLocation(MODID, "macerating/"+input.location().getPath()));
    }

    private void createSqueezingRecipe(RecipeOutput output, ItemStack input, ItemStack result, FluidStack resultFluid){
        String path = BuiltInRegistries.ITEM.getKey(input.getItem()).getPath();
        SpecialRecipeBuilder.special(category -> new SqueezingRecipe(Ingredient.of(input), result, resultFluid))
                .save(output, new ResourceLocation(MODID, "squeezing/"+path));
    }
    private void createSqueezingRecipe(RecipeOutput output, TagKey<Item> input, ItemStack result, FluidStack resultFluid){
        SpecialRecipeBuilder.special(category -> new SqueezingRecipe(Ingredient.of(input), result, resultFluid))
                .save(output, new ResourceLocation(MODID, "squeezing/"+input.location().getPath()));
    }

    private void createAlloyingRecipe(RecipeOutput output, List<Ingredient> inputs, ItemStack result){
        String path = BuiltInRegistries.ITEM.getKey(result.getItem()).getPath();
        SpecialRecipeBuilder.special(category -> new AlloyingRecipe(inputs, result))
                .save(output, new ResourceLocation(MODID, "alloying/"+path));
    }


}
