package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.datamaps.Coolant;
import com.sonamorningstar.eternalartifacts.content.datamaps.HeatSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModDataMaps {
	public static final DataMapType<Fluid, Coolant> COOLANTS = DataMapType.builder(
		new ResourceLocation(MODID, "coolants"), Registries.FLUID, Coolant.CODEC).build();
	public static final DataMapType<Block, HeatSource> HEAT_BLOCKS = DataMapType.builder(
		new ResourceLocation(MODID, "heat_blocks"), Registries.BLOCK, HeatSource.CODEC).synced(HeatSource.HEAT_VALUE_CODEC, false).build();
}
