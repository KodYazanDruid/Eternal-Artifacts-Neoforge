package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Comparator;
import java.util.List;

public class LightningStrikeProjectile extends AbstractHurtingProjectile {
    private float damage;
    private static final int MAX_CHAIN_TARGETS = 4;
    private static final double CHAIN_RADIUS = 6.0;
    private static final float CHAIN_DAMAGE_FALLOFF = 0.7F;
    private static final int STUN_DURATION = 40;
    private static final int ENERGY_AMOUNT = 256;

    public LightningStrikeProjectile(EntityType<? extends LightningStrikeProjectile> type, Level level) {
        super(type, level);
    }

    public LightningStrikeProjectile(Level level, LivingEntity shooter, double dx, double dy, double dz, float damage) {
        super(ModEntities.LIGHTNING_STRIKE_PROJECTILE.get(), shooter, dx, dy, dz, level);
        this.damage = damage;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide()) {
            Entity hit = result.getEntity();
            LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;

            SpellDamageHelper.hurtWithSpellDamage(this, hit, damage);
            applyStun(hit);

            chainLightning(hit, owner);

            level().playSound(null, hit.getX(), hit.getY(), hit.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.8F, 1.2F + level().random.nextFloat() * 0.3F);

            if (level() instanceof ServerLevel serverLevel) {
                spawnImpactParticles(serverLevel, hit.position());
            }

            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide()) {
            BlockPos pos = result.getBlockPos();

            IEnergyStorage energy = level().getCapability(Capabilities.EnergyStorage.BLOCK, pos, result.getDirection());
            if (energy != null) {
                energy.receiveEnergy(ENERGY_AMOUNT, false);
            }

            level().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.6F, 1.4F + level().random.nextFloat() * 0.3F);

            if (level() instanceof ServerLevel serverLevel) {
                Vec3 impactPos = result.getLocation();
                spawnImpactParticles(serverLevel, impactPos);
            }

            this.discard();
        }
    }

    private void chainLightning(Entity primaryTarget, LivingEntity owner) {
        Vec3 center = primaryTarget.position();
        AABB searchArea = new AABB(center, center).inflate(CHAIN_RADIUS);

        List<LivingEntity> nearbyEntities = level().getEntitiesOfClass(LivingEntity.class, searchArea, entity ->
            entity.isAlive() &&
            !entity.isInvulnerable() &&
            entity != primaryTarget &&
            entity != owner &&
            entity.distanceTo(primaryTarget) <= CHAIN_RADIUS
        );

        nearbyEntities.sort(Comparator.comparingDouble(e -> e.distanceTo(primaryTarget)));

        int chainCount = Math.min(MAX_CHAIN_TARGETS, nearbyEntities.size());
        float chainDamage = damage;
        Entity previousTarget = primaryTarget;

        for (int i = 0; i < chainCount; i++) {
            LivingEntity chainTarget = nearbyEntities.get(i);
            chainDamage *= CHAIN_DAMAGE_FALLOFF;

            SpellDamageHelper.hurtWithSpellDamage(this, chainTarget, chainDamage);
            applyStun(chainTarget);

            if (level() instanceof ServerLevel serverLevel) {
                spawnChainParticles(serverLevel, previousTarget.position().add(0, previousTarget.getBbHeight() / 2.0, 0),
                    chainTarget.position().add(0, chainTarget.getBbHeight() / 2.0, 0));
            }

            previousTarget = chainTarget;
        }
    }

    private void applyStun(Entity target) {
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, STUN_DURATION, 5, false, true));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, STUN_DURATION, 2, false, true));
        }
    }

    private void spawnImpactParticles(ServerLevel serverLevel, Vec3 pos) {
        for (int i = 0; i < 20; i++) {
            double ox = (serverLevel.random.nextDouble() - 0.5) * 1.5;
            double oy = (serverLevel.random.nextDouble() - 0.5) * 1.5;
            double oz = (serverLevel.random.nextDouble() - 0.5) * 1.5;
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.x + ox, pos.y + oy, pos.z + oz,
                1, 0, 0, 0, 0.1);
        }
    }

    private void spawnChainParticles(ServerLevel serverLevel, Vec3 from, Vec3 to) {
        int steps = (int) (from.distanceTo(to) * 4);
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double x = from.x + (to.x - from.x) * t + (serverLevel.random.nextDouble() - 0.5) * 0.2;
            double y = from.y + (to.y - from.y) * t + (serverLevel.random.nextDouble() - 0.5) * 0.2;
            double z = from.z + (to.z - from.z) * t + (serverLevel.random.nextDouble() - 0.5) * 0.2;
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, 0, 0, 0, 0.02);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 100) {
            this.discard();
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ELECTRIC_SPARK;
    }

    @Override
    public boolean isPickable() {
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
