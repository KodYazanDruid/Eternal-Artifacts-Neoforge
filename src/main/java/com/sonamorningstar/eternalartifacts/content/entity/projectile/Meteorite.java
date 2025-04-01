package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class Meteorite extends AbstractHurtingProjectile {
    private float damage;
    public Meteorite(
            Level level,
            LivingEntity shooter,
            double landingX,
            double landingY,
            double landingZ,
            float damage
    ) {
        super(ModEntities.METEORITE.get(), shooter, landingX, landingY, landingZ, level);
        this.damage = damage;
    }
    public Meteorite(EntityType<? extends Meteorite> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(result.getEntity().damageSources().onFire(), damage);
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        level().explode(this, null, null, this.getX(), this.getY(), this.getZ(), 6.0F, true, Level.ExplosionInteraction.BLOCK);
        discard();
    }
}
