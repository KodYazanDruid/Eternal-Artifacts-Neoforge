package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Meteorite extends AbstractHurtingProjectile {
    private float damage;
    public Meteorite(
            Level level,
            LivingEntity shooter,
            double offsetX,
            double offsetY,
            double offsetZ,
            float damage
    ) {
        super(ModEntities.METEORITE.get(), shooter, offsetX, offsetY, offsetZ, level);
        this.damage = damage;
    }
    public Meteorite(
        Level level,
        LivingEntity shooter,
        double landingX,
        double landingY,
        double landingZ,
        double offsetX,
        double offsetY,
        double offsetZ,
        float damage
    ) {
        super(ModEntities.METEORITE.get(), landingX, landingY, landingZ, offsetX, offsetY, offsetZ, level);
        setOwner(shooter);
        this.damage = damage;
    }
    public Meteorite(EntityType<? extends Meteorite> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), calculateExplosionRadius(damage), true, Level.ExplosionInteraction.BLOW);
            this.discard();
        }
    }
    
    
    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            Entity entity = pResult.getEntity();
            Entity owner = this.getOwner();
            entity.hurt(this.damageSources().source(DamageTypes.MOB_PROJECTILE, this, owner), damage);
            if (owner instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)owner, entity);
            }
        }
    }
    
    private float calculateExplosionRadius(float damage) {
        final float BASE_DAMAGE = 20.0f;
        final float BASE_RADIUS = 2.0f;
        final float MIN_RADIUS = 1.0f;
        final float SCALING_FACTOR = 0.5f;
        
        float radius = BASE_RADIUS * (float) Math.pow(damage / BASE_DAMAGE, SCALING_FACTOR);
        
        return Math.max(radius, MIN_RADIUS);
    }
}
