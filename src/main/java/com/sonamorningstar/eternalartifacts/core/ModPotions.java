package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModPotions {
	public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, MODID);
	
	public static final DeferredHolder<Potion, Potion> ANGLERS_LUCK = POTIONS.register("anglers_luck",
		() -> new Potion(new MobEffectInstance(ModEffects.ANGLERS_LUCK.get(), 3600)));
	public static final DeferredHolder<Potion, Potion> LONG_ANGLERS_LUCK = POTIONS.register("long_anglers_luck",
		() -> new Potion("anglers_luck", new MobEffectInstance(ModEffects.ANGLERS_LUCK.get(), 9600)));
	public static final DeferredHolder<Potion, Potion> STRONG_ANGLERS_LUCK = POTIONS.register("strong_anglers_luck",
		() -> new Potion("anglers_luck", new MobEffectInstance(ModEffects.ANGLERS_LUCK.get(), 1800, 1)));
	
	public static final DeferredHolder<Potion, Potion> LURING = POTIONS.register("luring",
		() -> new Potion(new MobEffectInstance(ModEffects.LURING.get(), 3600)));
	public static final DeferredHolder<Potion, Potion> LONG_LURING = POTIONS.register("long_luring",
		() -> new Potion("luring", new MobEffectInstance(ModEffects.LURING.get(), 9600)));
	public static final DeferredHolder<Potion, Potion> STRONG_LURING = POTIONS.register("strong_luring",
		() -> new Potion("luring", new MobEffectInstance(ModEffects.LURING.get(), 1800, 1)));
}
