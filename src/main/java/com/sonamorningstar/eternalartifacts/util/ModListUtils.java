package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModListUtils {
	private static final Map<String, String> modIdCache = new HashMap<>();
	
	public static Optional<String> getBlockCreatorModId(Block block) {
		return getCreatorModId(BuiltInRegistries.BLOCK, block);
	}
	
	public static Optional<String> getFluidCreatorModId(FluidStack fluid) {
		return getCreatorModId(BuiltInRegistries.FLUID, fluid.getFluid());
	}
	
	public static Optional<String> getEntityCreatorModId(EntityType<?> entityType) {
		return getCreatorModId(BuiltInRegistries.ENTITY_TYPE, entityType);
	}
	
	public static <T> Optional<String> getCreatorModId(Registry<T> registry, T holder) {
		ResourceLocation loc = registry.getKey(holder);
		if (loc == null) return Optional.empty();
		return Optional.of(getModNameForModId(loc.getNamespace()));
	}
	
	public static String getModNameForModId(String modid) {
		return modIdCache.computeIfAbsent(modid, id -> ModList.get().getModContainerById(modid).map(ModContainer::getModInfo)
			.map(IModInfo::getDisplayName).orElseGet(() -> StringUtils.capitalize(modid)));
	}
}
