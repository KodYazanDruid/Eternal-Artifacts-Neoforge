package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.world.feature.GravelOrePileConfiguration;
import com.sonamorningstar.eternalartifacts.world.feature.GravelOrePileFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);
    
    public static final DeferredHolder<Feature<?>, GravelOrePileFeature> GRAVEL_ORE_PILE = FEATURES.register("gravel_ore_pile",
        () -> new GravelOrePileFeature(GravelOrePileConfiguration.CODEC));
}
