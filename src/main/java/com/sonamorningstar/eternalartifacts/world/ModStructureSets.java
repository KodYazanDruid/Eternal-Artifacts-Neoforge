package com.sonamorningstar.eternalartifacts.world;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModStructureSets {

    public static final ResourceKey<StructureSet> SURVIVALISTS_IGLOO = register("survivalists_igloo");
    public static final ResourceKey<StructureSet> PLAINS_HOUSE = register("plains_house");

    private static ResourceKey<StructureSet> register(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, new ResourceLocation(MODID, name));
    }

    public static void bootstrap(BootstapContext<StructureSet> ctx) {
        HolderGetter<Structure> structureGetter = ctx.lookup(Registries.STRUCTURE);
        HolderGetter<Biome> biomeGetter = ctx.lookup(Registries.BIOME);

        ctx.register(SURVIVALISTS_IGLOO,
            new StructureSet(
                structureGetter.getOrThrow(ModStructures.SURVIVALISTS_IGLOO),
                new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357615)
            )
        );
        ctx.register(PLAINS_HOUSE,
            new StructureSet(
                structureGetter.getOrThrow(ModStructures.PLAINS_HOUSE),
                new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357616)
            )
        );
    }
}
