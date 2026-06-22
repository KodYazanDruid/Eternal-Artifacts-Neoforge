package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.content.datamaps.Coolant;
import com.sonamorningstar.eternalartifacts.content.datamaps.HeatSource;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModDataMaps;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
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
            .add(ModItems.BITUMEN.getId(), new FurnaceFuel(1600), false)
            .add(ModItems.SOUL_BLAZE_ROD.getId(), new FurnaceFuel(3200), false);
        
        builder(NeoForgeDataMaps.COMPOSTABLES)
            .add(ModItems.GREEN_APPLE.getId(), new Compostable(0.65F), false)
            .add(ModItems.YELLOW_APPLE.getId(), new Compostable(0.65F), false);
        
        builder(ModDataMaps.COOLANTS)
            .add(FluidTags.WATER, new Coolant(1000, 2), false)
            .add(ModFluids.COOLANT_GEL.getRegistryName(), new Coolant(2000, 1), false);
        
        builder(ModDataMaps.HEAT_BLOCKS)
            .add(Blocks.LAVA.builtInRegistryHolder(), new HeatSource(1000), false)
            .add(Blocks.MAGMA_BLOCK.builtInRegistryHolder(), new HeatSource(850), false)
            .add(ModBlocks.SOUL_MAGMA_BLOCK.getId(), new HeatSource(1250), false);
    }
}
