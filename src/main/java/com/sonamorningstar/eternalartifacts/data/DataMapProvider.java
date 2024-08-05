package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class DataMapProvider extends net.neoforged.neoforge.common.data.DataMapProvider {
    protected DataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        builder(NeoForgeDataMaps.FURNACE_FUELS)
            .add(ModItems.SUGAR_CHARCOAL.getId(), new FurnaceFuel(400), false)
            .add(ModBlocks.SUGAR_CHARCOAL_BLOCK.getId(), new FurnaceFuel(4000), false)
            .add(ModItems.TAR_BALL.getId(), new FurnaceFuel(1600), false)
            .add(ModItems.BITUMEN.getId(), new FurnaceFuel(1600), false);
    }
}
