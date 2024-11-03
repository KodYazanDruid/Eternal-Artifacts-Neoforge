package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModStructures {

    public static final ResourceKey<Structure> SURVIVALISTS_IGLOO = registerKey("survivalists_igloo");

    public static ResourceKey<Structure> registerKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(MODID, name));
    }

    public static void bootstrap(BootstapContext<Structure> context) {
        /*context.register(SURVIVALISTS_IGLOO, new SurvivalistsIglooStructure(new Structure.StructureSettings(
                context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_TAIGA),
                Arrays.stream(MobCategory.values()).collect(Collectors.toMap(category -> category, category -> new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create()))),
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                TerrainAdjustment.BEARD_THIN)));*/
    }
}
