package com.sonamorningstar.eternalartifacts.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_MANGANESE_ORE = registerKey("gravel_manganese_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_COAL_ORE = registerKey("gravel_coal_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_COPPER_ORE = registerKey("gravel_copper_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_IRON_ORE = registerKey("gravel_iron_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_GOLD_ORE = registerKey("gravel_gold_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_DIAMOND_ORE = registerKey("gravel_diamond_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_EMERALD_ORE = registerKey("gravel_emerald_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_REDSTONE_ORE = registerKey("gravel_redstone_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_LAPIS_ORE = registerKey("gravel_lapis_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_TIN_ORE = registerKey("gravel_tin_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_ALUMINUM_ORE = registerKey("gravel_aluminum_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MANGANESE_ORE_MIDDLE = registerKey("placed_manganese_ore_middle");
    public static final ResourceKey<PlacedFeature> PLACED_MANGANESE_ORE_SMALL = registerKey("placed_manganese_ore_small");
    public static final ResourceKey<PlacedFeature> PLACED_TIN_ORE_MIDDLE = registerKey("placed_tin_ore_middle");
    public static final ResourceKey<PlacedFeature> PLACED_TIN_ORE_SMALL = registerKey("placed_tin_ore_small");
    public static final ResourceKey<PlacedFeature> PLACED_ALUMINUM_ORE_MIDDLE = registerKey("placed_aluminum_ore_middle");
    public static final ResourceKey<PlacedFeature> PLACED_ALUMINUM_ORE_SMALL = registerKey("placed_aluminum_ore_small");
    public static final ResourceKey<PlacedFeature> PLACED_TIGRIS_FLOWER = registerKey("placed_tigris_flower");
    public static final ResourceKey<PlacedFeature> CRUDE_OIL_LAKE_DEEPSLATE = registerKey("crude_oil_deposit");
    public static final ResourceKey<PlacedFeature> CRUDE_OIL_SURFACE = registerKey("crude_oil_surface_deposit");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_COAL_ORE = registerKey("moss_coal_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_COPPER_ORE = registerKey("moss_copper_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_IRON_ORE = registerKey("moss_iron_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_GOLD_ORE = registerKey("moss_gold_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_DIAMOND_ORE = registerKey("moss_diamond_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_EMERALD_ORE = registerKey("moss_emerald_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_REDSTONE_ORE = registerKey("moss_redstone_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_LAPIS_ORE = registerKey("moss_lapis_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_TIN_ORE = registerKey("moss_tin_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MOSS_ALUMINUM_ORE = registerKey("moss_aluminum_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MARIN_ORE = registerKey("marin_ore");
    
    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MODID, name));
    }
    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> holderGetter = context.lookup(Registries.CONFIGURED_FEATURE);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_MANGANESE_ORE, ModConfiguredFeatures.GRAVEL_MANGANESE_ORE, 250);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_COAL_ORE, ModConfiguredFeatures.GRAVEL_COAL_ORE, 100);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_COPPER_ORE, ModConfiguredFeatures.GRAVEL_COPPER_ORE, 200);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_IRON_ORE, ModConfiguredFeatures.GRAVEL_IRON_ORE, 250);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_GOLD_ORE, ModConfiguredFeatures.GRAVEL_GOLD_ORE, 350);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_DIAMOND_ORE, ModConfiguredFeatures.GRAVEL_DIAMOND_ORE, 500);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_EMERALD_ORE, ModConfiguredFeatures.GRAVEL_EMERALD_ORE, 500);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_LAPIS_ORE, ModConfiguredFeatures.GRAVEL_LAPIS_ORE, 300);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_REDSTONE_ORE, ModConfiguredFeatures.GRAVEL_REDSTONE_ORE, 300);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_TIN_ORE, ModConfiguredFeatures.GRAVEL_TIN_ORE, 250);
        registerGravelOre(context, holderGetter, PLACED_GRAVEL_ALUMINUM_ORE, ModConfiguredFeatures.GRAVEL_ALUMINUM_ORE, 250);
        registerMossOre(context, holderGetter, PLACED_MOSS_COAL_ORE, ModConfiguredFeatures.MOSS_COAL_ORE, 10);
        registerMossOre(context, holderGetter, PLACED_MOSS_COPPER_ORE, ModConfiguredFeatures.MOSS_COPPER_ORE, 10);
        registerMossOre(context, holderGetter, PLACED_MOSS_IRON_ORE, ModConfiguredFeatures.MOSS_IRON_ORE, 8);
        registerMossOre(context, holderGetter, PLACED_MOSS_GOLD_ORE, ModConfiguredFeatures.MOSS_GOLD_ORE, 8);
        registerMossOre(context, holderGetter, PLACED_MOSS_DIAMOND_ORE, ModConfiguredFeatures.MOSS_DIAMOND_ORE, 4);
        registerMossOre(context, holderGetter, PLACED_MOSS_EMERALD_ORE, ModConfiguredFeatures.MOSS_EMERALD_ORE, 4);
        registerMossOre(context, holderGetter, PLACED_MOSS_REDSTONE_ORE, ModConfiguredFeatures.MOSS_REDSTONE_ORE, 12);
        registerMossOre(context, holderGetter, PLACED_MOSS_LAPIS_ORE, ModConfiguredFeatures.MOSS_LAPIS_ORE, 8);
        registerMossOre(context, holderGetter, PLACED_MOSS_TIN_ORE, ModConfiguredFeatures.MOSS_TIN_ORE, 10);
        registerMossOre(context, holderGetter, PLACED_MOSS_ALUMINUM_ORE, ModConfiguredFeatures.MOSS_ALUMINUM_ORE, 10);
        context.register(PLACED_MANGANESE_ORE_MIDDLE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.MANGANESE_ORE),
                commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(36)))));
        context.register(PLACED_MANGANESE_ORE_SMALL, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.MANGANESE_ORE_SMALL),
                commonOrePlacement(15, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72)))));
        context.register(PLACED_TIN_ORE_MIDDLE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.TIN_ORE),
                commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(36)))));
        context.register(PLACED_TIN_ORE_SMALL, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.TIN_ORE_SMALL),
                commonOrePlacement(15, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72)))));
        context.register(PLACED_ALUMINUM_ORE_MIDDLE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.ALUMINUM_ORE),
                commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(36)))));
        context.register(PLACED_ALUMINUM_ORE_SMALL, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.ALUMINUM_ORE_SMALL),
                commonOrePlacement(15, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72)))));
        context.register(PLACED_MARIN_ORE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.MARIN_ORE),
                List.of(
                    CountPlacement.of(8),
                    InSquarePlacement.spread(),
                    PlacementUtils.RANGE_8_8,
                    EnvironmentScanPlacement.scanningFor(
                        Direction.UP,
                        BlockPredicate.alwaysTrue(),
                        BlockPredicate.matchesBlocks(Blocks.NETHERRACK),
                        48
                    ),
                    EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.alwaysTrue(),
                        BlockPredicate.matchesBlocks(Blocks.NETHERRACK),
                        48
                    ),
                    BiomeFilter.biome()
                )
        ));
        
        context.register(PLACED_TIGRIS_FLOWER, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.TIGRIS_FLOWER),
                List.of(RarityFilter.onAverageOnceEvery(12), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        context.register(CRUDE_OIL_LAKE_DEEPSLATE,
            new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.CRUDE_OIL_LAKE_DEEPSLATE),
                List.of(RarityFilter.onAverageOnceEvery(24),
                    InSquarePlacement.spread(),
                    HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(0))),
                    EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))),
                        32
                    ),
                    SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5),
                    BiomeFilter.biome()
                )
            ));
        context.register(CRUDE_OIL_SURFACE,
            new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.CRUDE_OIL_LAKE_SURFACE),
                List.of(RarityFilter.onAverageOnceEvery(48),
                    InSquarePlacement.spread(),
                    HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(1), VerticalAnchor.absolute(64))),
                    PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                    BiomeFilter.biome()
                )
            ));
    }
    private static List<PlacementModifier> createListWithRarity(int rarity) {
        return List.of(RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier countPlacement, PlacementModifier heightRange) {
        return List.of(countPlacement, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    private static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier heightRange) {
        return orePlacement(RarityFilter.onAverageOnceEvery(chance), heightRange);
    }
    
    private static void registerGravelOre(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> holderGetter, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, int rarity) {
        context.register(key, new PlacedFeature(holderGetter.getOrThrow(configuredFeature), createListWithRarity(rarity)));
    }
    private static void registerMossOre(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> holderGetter,
                                        ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, int size) {
        context.register(key, new PlacedFeature(holderGetter.getOrThrow(configuredFeature),
            commonOrePlacement(size, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()))));
    }
}

