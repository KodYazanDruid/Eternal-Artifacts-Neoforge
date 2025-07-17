package com.sonamorningstar.eternalartifacts.core;

import com.google.common.base.Suppliers;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModDamageTypes {
	
	public static Supplier<ResourceKey<DamageType>> EXECUTE = Suppliers.memoize(() -> ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "execute")));
	public static Supplier<ResourceKey<DamageType>> MAGIC_BYPASS_IFRAME = Suppliers.memoize(() -> ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "magic_bypass_iframe")));
	
	public static void bootstrap(BootstapContext<DamageType> ctx) {
		ctx.register(EXECUTE.get(), new DamageType("execute", DamageScaling.NEVER, 0.0F));
		ctx.register(MAGIC_BYPASS_IFRAME.get(), new DamageType("magic_bypass_iframe", 0.0F));
	
	}
}
