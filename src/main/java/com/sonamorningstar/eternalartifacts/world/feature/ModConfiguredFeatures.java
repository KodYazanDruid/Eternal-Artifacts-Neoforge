package com.sonamorningstar.eternalartifacts.world.feature;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_COAL_ORE = registerKey("gravel_coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_COPPER_ORE = registerKey("gravel_copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_IRON_ORE = registerKey("gravel_iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAVEL_GOLD_ORE = registerKey("gravel_gold_ore");

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MODID, name));
    }
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(GRAVEL_COAL_ORE, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(ModBlocks.GRAVEL_COAL_ORE.get()))));
        context.register(GRAVEL_COPPER_ORE, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(ModBlocks.GRAVEL_COPPER_ORE.get()))));
        context.register(GRAVEL_IRON_ORE, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(ModBlocks.GRAVEL_IRON_ORE.get()))));
        context.register(GRAVEL_GOLD_ORE, new ConfiguredFeature<>(Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(ModBlocks.GRAVEL_GOLD_ORE.get()))));
    }
}
