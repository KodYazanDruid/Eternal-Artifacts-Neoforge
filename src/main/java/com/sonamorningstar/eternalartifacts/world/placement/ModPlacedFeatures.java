package com.sonamorningstar.eternalartifacts.world.placement;

import com.sonamorningstar.eternalartifacts.world.feature.ModConfiguredFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_COAL_ORE = registerKey("gravel_coal_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_COPPER_ORE = registerKey("gravel_copper_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_IRON_ORE = registerKey("gravel_iron_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GRAVEL_GOLD_ORE = registerKey("gravel_gold_ore");
    public static final ResourceKey<PlacedFeature> PLACED_MANGANESE_ORE_MIDDLE = registerKey("placed_manganese_ore_middle");
    public static final ResourceKey<PlacedFeature> PLACED_MANGANESE_ORE_SMALL = registerKey("placed_manganese_ore_small");

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MODID, name));
    }
    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> holderGetter = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(PLACED_GRAVEL_COAL_ORE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.GRAVEL_COAL_ORE), createListWithRarity(250)));
        context.register(PLACED_GRAVEL_COPPER_ORE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.GRAVEL_COPPER_ORE), createListWithRarity(300)));
        context.register(PLACED_GRAVEL_IRON_ORE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.GRAVEL_IRON_ORE), createListWithRarity(350)));
        context.register(PLACED_GRAVEL_GOLD_ORE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.GRAVEL_GOLD_ORE), createListWithRarity(500)));
        context.register(PLACED_MANGANESE_ORE_MIDDLE, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.MANGANESE_ORE),
                commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56)))));
        context.register(PLACED_MANGANESE_ORE_SMALL, new PlacedFeature(holderGetter.getOrThrow(ModConfiguredFeatures.MANGANESE_ORE),
                commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72)))));
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
}

