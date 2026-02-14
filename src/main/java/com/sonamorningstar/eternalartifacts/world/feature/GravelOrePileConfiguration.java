package com.sonamorningstar.eternalartifacts.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record GravelOrePileConfiguration(BlockStateProvider stateProvider, IntProvider radius) implements FeatureConfiguration {
    public static final Codec<GravelOrePileConfiguration> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(GravelOrePileConfiguration::stateProvider),
            IntProvider.CODEC.fieldOf("radius").forGetter(GravelOrePileConfiguration::radius)
        ).apply(instance, GravelOrePileConfiguration::new)
    );
}
