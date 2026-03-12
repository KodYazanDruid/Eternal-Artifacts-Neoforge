package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class MagicMissileEntity extends AbstractHurtingProjectile {
    private float damage;

    public MagicMissileEntity(EntityType<? extends MagicMissileEntity> type, Level level) {
        super(type, level);
    }

    public MagicMissileEntity(Level level, LivingEntity shooter, double dx, double dy, double dz, float damage) {
        super(ModEntities.MAGIC_MISSILE.get(), shooter, dx, dy, dz, level);
        this.damage = damage;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide()) {
            SpellDamageHelper.hurtWithSpellDamageBypassIFrame(this, result.getEntity(), damage);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 60) {
            this.discard();
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ENCHANT;
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
