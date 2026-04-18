package com.sonamorningstar.eternalartifacts.world;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.common.world.StructureModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModStructureModifiers {
	public static final ResourceKey<StructureModifier> SPAWN_SOUL_BLAZE_ON_FORTRESS = registerKey("spawn", "spawn_soul_blaze_on_fortress");
	
	private static ResourceKey<StructureModifier> registerKey(String type, String name) {
		return ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, new ResourceLocation(MODID, type+"/"+name));
	}
	
	public static void bootstrap(BootstapContext<StructureModifier> context) {
		HolderGetter<Structure> structure = context.lookup(Registries.STRUCTURE);
		
		context.register(SPAWN_SOUL_BLAZE_ON_FORTRESS, new StructureModifiers.AddSpawnsStructureModifier(
			HolderSet.direct(structure.getOrThrow(BuiltinStructures.FORTRESS)),
			List.of(new MobSpawnSettings.SpawnerData(ModEntities.SOUL_BLAZE.get(), 5, 2, 4))
		));
	}
}
