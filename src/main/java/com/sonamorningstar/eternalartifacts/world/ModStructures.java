package com.sonamorningstar.eternalartifacts.world;

import com.sonamorningstar.eternalartifacts.world.structure.SurvivalistsIglooStructure;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static net.minecraft.data.worldgen.Structures.structure;

public class ModStructures {

    public static final ResourceKey<Structure> SURVIVALISTS_IGLOO = registerKey("survivalists_igloo");

    public static ResourceKey<Structure> registerKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(MODID, name));
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> biome, TerrainAdjustment terrainAdjustment) {
        return new Structure.StructureSettings(biome, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, terrainAdjustment);
    }

    public static void bootstrap(BootstapContext<Structure> context) {
        HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templateGetter = context.lookup(Registries.TEMPLATE_POOL);

        context.register(
                SURVIVALISTS_IGLOO,
                new SurvivalistsIglooStructure(structure(biomeGetter.getOrThrow(BiomeTags.HAS_IGLOO), TerrainAdjustment.NONE))
        );
    }
}
