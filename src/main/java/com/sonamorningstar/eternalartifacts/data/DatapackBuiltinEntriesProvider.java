package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.ModStructures;
import com.sonamorningstar.eternalartifacts.world.ModBiomeModifiers;
import com.sonamorningstar.eternalartifacts.world.feature.ModConfiguredFeatures;
import com.sonamorningstar.eternalartifacts.world.placement.ModPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class DatapackBuiltinEntriesProvider extends net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.STRUCTURE, ModStructures::bootstrap);

    public DatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of("minecraft", MODID));
    }
}
