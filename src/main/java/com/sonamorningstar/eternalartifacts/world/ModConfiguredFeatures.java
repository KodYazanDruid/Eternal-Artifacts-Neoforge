package com.sonamorningstar.eternalartifacts.world;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_MANGANESE_ORE = registerKey("gravel_manganese_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_COAL_ORE = registerKey("gravel_coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_COPPER_ORE = registerKey("gravel_copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_IRON_ORE = registerKey("gravel_iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_GOLD_ORE = registerKey("gravel_gold_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_DIAMOND_ORE = registerKey("gravel_diamond_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_EMERALD_ORE = registerKey("gravel_emerald_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_REDSTONE_ORE = registerKey("gravel_redstone_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_LAPIS_ORE = registerKey("gravel_lapis_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MANGANESE_ORE = registerKey("manganese_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MANGANESE_ORE_SMALL = registerKey("manganese_ore_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TIGRIS_FLOWER = registerKey("tigris_flower");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRUDE_OIL_LAKE_DEEPSLATE = registerKey("crude_oil_lake_deepslate");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_MANGANESE_ORE = registerKey("moss_manganese_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_COAL_ORE = registerKey("moss_coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_COPPER_ORE = registerKey("moss_copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_IRON_ORE = registerKey("moss_iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_GOLD_ORE = registerKey("moss_gold_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_DIAMOND_ORE = registerKey("moss_diamond_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_EMERALD_ORE = registerKey("moss_emerald_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_REDSTONE_ORE = registerKey("moss_redstone_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_LAPIS_ORE = registerKey("moss_lapis_ore");
    
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MODID, name));
    }
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest ruleTestStone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest ruleTestDeepslate = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> manganeseOres = List.of(
            OreConfiguration.target(ruleTestStone, ModBlocks.MANGANESE_ORE.get().defaultBlockState()),
            OreConfiguration.target(ruleTestDeepslate, ModBlocks.DEEPSLATE_MANGANESE_ORE.get().defaultBlockState())
        );
        registerGravelOre(context, GRAVEL_MANGANESE_ORE, ModBlocks.GRAVEL_MANGANESE_ORE);
        registerGravelOre(context, GRAVEL_COAL_ORE, ModBlocks.GRAVEL_COAL_ORE);
        registerGravelOre(context, GRAVEL_COPPER_ORE, ModBlocks.GRAVEL_COPPER_ORE);
        registerGravelOre(context, GRAVEL_IRON_ORE, ModBlocks.GRAVEL_IRON_ORE);
        registerGravelOre(context, GRAVEL_GOLD_ORE, ModBlocks.GRAVEL_GOLD_ORE);
        registerGravelOre(context, GRAVEL_DIAMOND_ORE, ModBlocks.GRAVEL_DIAMOND_ORE);
        registerGravelOre(context, GRAVEL_EMERALD_ORE, ModBlocks.GRAVEL_EMERALD_ORE);
        registerGravelOre(context, GRAVEL_REDSTONE_ORE, ModBlocks.GRAVEL_REDSTONE_ORE);
        registerGravelOre(context, GRAVEL_LAPIS_ORE, ModBlocks.GRAVEL_LAPIS_ORE);
        registerMossOre(context, MOSS_MANGANESE_ORE, ModBlocks.MOSS_MANGANESE_ORE, 9);
        registerMossOre(context, MOSS_COAL_ORE, ModBlocks.MOSS_COAL_ORE, 9);
        registerMossOre(context, MOSS_COPPER_ORE, ModBlocks.MOSS_COPPER_ORE, 9);
        registerMossOre(context, MOSS_IRON_ORE, ModBlocks.MOSS_IRON_ORE, 9);
        registerMossOre(context, MOSS_GOLD_ORE, ModBlocks.MOSS_GOLD_ORE, 9);
        registerMossOre(context, MOSS_DIAMOND_ORE, ModBlocks.MOSS_DIAMOND_ORE, 9);
        registerMossOre(context, MOSS_EMERALD_ORE, ModBlocks.MOSS_EMERALD_ORE, 9);
        registerMossOre(context, MOSS_REDSTONE_ORE, ModBlocks.MOSS_REDSTONE_ORE, 9);
        registerMossOre(context, MOSS_LAPIS_ORE, ModBlocks.MOSS_LAPIS_ORE, 9);
        context.register(MANGANESE_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(manganeseOres, 9)));
        context.register(MANGANESE_ORE_SMALL, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(manganeseOres, 4)));
        context.register(TIGRIS_FLOWER, new ConfiguredFeature<>(Feature.NO_BONEMEAL_FLOWER, new RandomPatchConfiguration(64, 6, 2,
            PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.TIGRIS_FLOWER.get())
            ))
        )));
        context.register(CRUDE_OIL_LAKE_DEEPSLATE, new ConfiguredFeature<>(Feature.LAKE, new LakeFeature.Configuration(
            BlockStateProvider.simple(ModFluids.CRUDE_OIL.getFluidBlock()), BlockStateProvider.simple(Blocks.DEEPSLATE)
        )));
    }
    
    private static void registerGravelOre(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, DeferredBlock<?> block) {
        context.register(key, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(block.get()))));
    }
    
    private static void registerMossOre(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key,
                                        DeferredBlock<?> block, int size) {
        context.register(key, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
            OreConfiguration.target(new BlockStateMatchTest(Blocks.MOSS_BLOCK.defaultBlockState()), block.get().defaultBlockState())
        ), size)));
    }
}
