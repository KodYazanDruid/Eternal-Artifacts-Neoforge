package com.sonamorningstar.eternalartifacts.world;

import com.sonamorningstar.eternalartifacts.world.placement.ModPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_GRAVEL_COAL_ORE = registerKey("add_gravel_coal_ore");
    public static final ResourceKey<BiomeModifier> ADD_GRAVEL_COPPER_ORE = registerKey("add_gravel_copper_ore");
    public static final ResourceKey<BiomeModifier> ADD_GRAVEL_IRON_ORE = registerKey("add_gravel_iron_ore");
    public static final ResourceKey<BiomeModifier> ADD_GRAVEL_GOLD_ORE = registerKey("add_gravel_gold_ore");

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(MODID, "feature/"+name));
    }

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
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
    }
}
