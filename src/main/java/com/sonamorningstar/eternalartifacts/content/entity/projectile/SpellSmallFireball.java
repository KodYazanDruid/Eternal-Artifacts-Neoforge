package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SpellSmallFireball extends SmallFireball {
    private float damage;

    public SpellSmallFireball(Level level, LivingEntity shooter, double dx, double dy, double dz, float damage) {
        super(level, shooter, dx, dy, dz);
        this.damage = damage;
    }

    public SpellSmallFireball(EntityType<? extends SpellSmallFireball> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide()) {
            Entity hit = result.getEntity();
            if (!hit.fireImmune()) {
                SpellDamageHelper.hurtWithSpellDamage(this, hit, damage);
                hit.setSecondsOnFire(5);
            }
        }
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
