package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
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

	public static Optional<String> getFluidCreatorModId(FluidStack fluid) {
		if (fluid.isEmpty()) return Optional.empty();
		return Optional.of(getModNameForModId(BuiltInRegistries.FLUID.getKey(fluid.getFluid()).getNamespace()));
	}
	
	public static String getModNameForModId(String modid) {
		return modIdCache.computeIfAbsent(modid, id -> ModList.get().getModContainerById(modid).map(ModContainer::getModInfo)
			.map(IModInfo::getDisplayName).orElseGet(() -> StringUtils.capitalize(modid)));
	}
}
