package com.sonamorningstar.eternalartifacts.world;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_COAL_ORE = registerKey("gravel_coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_COPPER_ORE = registerKey("gravel_copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_IRON_ORE = registerKey("gravel_iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_GOLD_ORE = registerKey("gravel_gold_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MANGANESE_ORE = registerKey("manganese_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MANGANESE_ORE_SMALL = registerKey("manganese_ore_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TIGRIS_FLOWER = registerKey("tigris_flower");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRUDE_OIL_LAKE_DEEPSLATE = registerKey("crude_oil_lake_deepslate");

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
        context.register(GRAVEL_COAL_ORE, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(ModBlocks.GRAVEL_COAL_ORE.get()))));
        context.register(GRAVEL_COPPER_ORE, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(ModBlocks.GRAVEL_COPPER_ORE.get()))));
        context.register(GRAVEL_IRON_ORE, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(ModBlocks.GRAVEL_IRON_ORE.get()))));
        context.register(GRAVEL_GOLD_ORE, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(ModBlocks.GRAVEL_GOLD_ORE.get()))));
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
}
