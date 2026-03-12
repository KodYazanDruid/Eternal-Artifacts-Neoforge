package com.sonamorningstar.eternalartifacts.core;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModVillagers {
	public static DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, MODID);
	public static DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, MODID);

	public static DeferredHolder<PoiType, ?> MECHANIC_POI = POI_TYPES.register("mechanic_poi",
		() -> new PoiType(ImmutableSet.copyOf(ModBlocks.MACHINE_WORKBENCH.get().getStateDefinition().getPossibleStates()), 1, 1));

	public static DeferredHolder<VillagerProfession, ?> MECHANIC = PROFESSIONS.register("mechanic",
		() -> new VillagerProfession("mechanic", x -> x.is(MECHANIC_POI.getKey()), x -> x.is(MECHANIC_POI.getKey()),
			ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_LIBRARIAN));
}
