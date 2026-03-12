package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class SpellEvokerFangs extends EvokerFangs {
    private float damage;

    public SpellEvokerFangs(Level level, double x, double y, double z, float rotation, int delay, @Nullable LivingEntity owner, float damage) {
        super(level, x, y, z, rotation, delay, owner);
        this.damage = damage;
    }

    public SpellEvokerFangs(EntityType<? extends SpellEvokerFangs> type, Level level) {
        super(type, level);
    }

    @Override
    public void dealDamageTo(LivingEntity target) {
        LivingEntity owner = this.getOwner();
        if (target.isAlive() && !target.isInvulnerable() && target != owner) {
            SpellDamageHelper.hurtWithSpellDamage(this, owner, target, damage);
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
