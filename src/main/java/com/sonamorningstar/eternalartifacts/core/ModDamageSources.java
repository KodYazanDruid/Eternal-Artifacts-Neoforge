package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ModDamageSources {
	public static Map<LevelAccessor, ModDamageSources> INSTANCES = new HashMap<>();
	
	public final Registry<DamageType> damageTypes;
	private final DamageSource execute;
	
	public ModDamageSources(RegistryAccess reg) {
		this.damageTypes = reg.registryOrThrow(Registries.DAMAGE_TYPE);
		this.execute = this.source(ModDamageTypes.EXECUTE.get());
	}
	
	public DamageSource source(ResourceKey<DamageType> pDamageTypeKey) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey));
	}
	
	public DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pEntity) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey), pEntity);
	}
	
	public DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pCausingEntity, @Nullable Entity pDirectEntity) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey), pCausingEntity, pDirectEntity);
	}
	
	public DamageSource execute() {return this.execute;}
	
	public DamageSource execute(LivingEntity entity) {
		return this.source(ModDamageTypes.EXECUTE.get(), entity);
	}
	
	public DamageSource magicBypassIFrame(LivingEntity entity) {
		return this.source(ModDamageTypes.MAGIC_BYPASS_IFRAME.get(), entity);
	}
}
