package com.sonamorningstar.eternalartifacts.world;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_GRAVEL_COAL_ORE = registerKey("feature", "add_gravel_coal_ore");
    public static final ResourceKey<BiomeModifier> ADD_GRAVEL_COPPER_ORE = registerKey("feature", "add_gravel_copper_ore");
    public static final ResourceKey<BiomeModifier> ADD_GRAVEL_IRON_ORE = registerKey("feature", "add_gravel_iron_ore");
    public static final ResourceKey<BiomeModifier> ADD_GRAVEL_GOLD_ORE = registerKey("feature", "add_gravel_gold_ore");
    public static final ResourceKey<BiomeModifier> ADD_MANGANESE_ORE_MEDIUM = registerKey("feature", "add_manganese_ore_medium");
    public static final ResourceKey<BiomeModifier> ADD_MANGANESE_ORE_SMALL = registerKey("feature", "add_manganese_ore_small");
    public static final ResourceKey<BiomeModifier> ADD_TIGRIS_FLOWERS = registerKey("feature", "add_tigris_flowers");
    
    public static final ResourceKey<BiomeModifier> SPAWN_DUCK = registerKey("spawn", "spawn_duck");
    public static final ResourceKey<BiomeModifier> SPAWN_DEMON_EYE = registerKey("spawn", "spawn_demon_eye");

    private static ResourceKey<BiomeModifier> registerKey(String type, String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(MODID, type+"/"+name));
    }

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> configuredCarver = context.lookup(Registries.CONFIGURED_CARVER);
        HolderGetter<Biome> biome = context.lookup(Registries.BIOME);

        context.register(ADD_GRAVEL_COAL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biome.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PLACED_GRAVEL_COAL_ORE)),
                GenerationStep.Decoration.SURFACE_STRUCTURES));
        context.register(ADD_GRAVEL_COPPER_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biome.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PLACED_GRAVEL_COPPER_ORE)),
                GenerationStep.Decoration.SURFACE_STRUCTURES));
        context.register(ADD_GRAVEL_IRON_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biome.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PLACED_GRAVEL_IRON_ORE)),
                GenerationStep.Decoration.SURFACE_STRUCTURES));
        context.register(ADD_GRAVEL_GOLD_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biome.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PLACED_GRAVEL_GOLD_ORE)),
                GenerationStep.Decoration.SURFACE_STRUCTURES));
        context.register(ADD_MANGANESE_ORE_MEDIUM, new BiomeModifiers.AddFeaturesBiomeModifier(
                biome.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PLACED_MANGANESE_ORE_MIDDLE)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_MANGANESE_ORE_SMALL, new BiomeModifiers.AddFeaturesBiomeModifier(
                biome.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PLACED_MANGANESE_ORE_SMALL)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        
        context.register(ADD_TIGRIS_FLOWERS, new BiomeModifiers.AddFeaturesBiomeModifier(
                biome.getOrThrow(Tags.Biomes.IS_SWAMP),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PLACED_TIGRIS_FLOWER)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(SPAWN_DUCK, new BiomeModifiers.AddSpawnsBiomeModifier(
                biome.getOrThrow(BiomeTags.IS_FOREST),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.DUCK.get(), 10, 4, 4))
        ));
        context.register(SPAWN_DEMON_EYE, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biome.getOrThrow(Biomes.CRIMSON_FOREST)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.DEMON_EYE.get(), 10, 2, 6))
        ));
    }
}
