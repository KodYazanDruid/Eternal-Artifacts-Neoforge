package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import com.sonamorningstar.eternalartifacts.util.WorldUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;

public class VoidlockSpellProj extends AbstractHurtingProjectile implements ItemSupplier {
	public float damage;
	public double damageRadius = 3D;
	private LivingEntity target;
	private final int MAX_AGE = 200;
	private final double HOMING_RANGE = 10.0D;
	
	public VoidlockSpellProj(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = true;
	}
	
	public VoidlockSpellProj(Level level, LivingEntity caster, double x, double y, double z, float amplifiedDamage) {
		super(ModEntities.SPELL_VOIDLOCK.get(), caster, x, y, z, level);
		this.damage = amplifiedDamage;
		this.noPhysics = true;
	}
	
	@Override
	public void tick() {
		super.tick();
		if (this.tickCount > MAX_AGE) {
			this.discard();
		}
		
		findNearestTarget();
		
		if (!this.level().isClientSide() && target != null) {
			Vec3 targetPos = this.target.position().add(0, this.target.getBbHeight() / 2.0, 0);
			Vec3 directionToTarget = targetPos.subtract(this.position()).normalize();
			this.setDeltaMovement(directionToTarget);
		}
	}
	
	private void findNearestTarget() {
		Entity owner = this.getOwner();
		
		WorldUtils.getEntitiesInCone(level(), position(), getDeltaMovement(), HOMING_RANGE, 90, LivingEntity.class,
			entity -> {
				boolean eligible = entity != owner && entity.isAlive() && !entity.isSpectator();
				if (entity instanceof OwnableEntity ownable) {
					LivingEntity petOwner = ownable.getOwner();
					eligible = petOwner == null || getOwner() == null ||
						!Objects.equals(petOwner.getUUID(), getOwner().getUUID());
				}
				return eligible;
			}
		).stream().min(Comparator.comparingDouble(this::distanceToSqr)).ifPresent(e -> this.target = e);
		
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!level().isClientSide()) {
			Entity mainTarget = result.getEntity();
			SpellDamageHelper.hurtWithSpellDamage(this, mainTarget, damage);
			
			if (level() instanceof ServerLevel serverLevel) {
				for (int i = 0; i < 50; i++) {
					double dx = random.nextGaussian();
					double dy = random.nextGaussian();
					double dz = random.nextGaussian();
					double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
					if (length != 0) {
						dx /= length;
						dy /= length;
						dz /= length;
					}
					double r = (damageRadius * 0.5) * Math.cbrt(random.nextDouble());
					serverLevel.sendParticles(ParticleTypes.PORTAL,
						mainTarget.getX() + dx * r, mainTarget.getY(0.5) + dy * r, mainTarget.getZ() + dz * r,
						1, 0, 0, 0, 0.1);
				}
				serverLevel.sendParticles(ParticleTypes.EXPLOSION,
					mainTarget.getX(), mainTarget.getY(0.5), mainTarget.getZ(),
					1, 0, 0, 0, 0);
			}
			
			WorldUtils.getEntitiesInRange(level(), position(), damageRadius, LivingEntity.class,
				e -> e != mainTarget && e.isAlive() && !e.isSpectator())
				.forEach(e -> SpellDamageHelper.hurtWithSpellDamage(this, e, damage / 2));
			this.discard();
		}
	}
	
	@Override
	public ItemStack getItem() {
		return Items.ENDER_EYE.getDefaultInstance();
	}
	
	@Nullable
	@Override
	protected ParticleOptions getTrailParticle() {
		return ParticleTypes.PORTAL;
	}
	
	@Override
	public boolean isPickable() {
		return false;
	}
	
	@Override
	protected boolean shouldBurn() {
		return false;
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putFloat("SpellDamage", damage);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		damage = tag.getFloat("SpellDamage");
	}
}
