package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.spell.base.AbstractProjectileSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBulletsSpell extends AbstractProjectileSpell {
    public ShulkerBulletsSpell(Properties props) {
        super(props);
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity caster, float amplifiedDamage, HitResult result, Vec3 shootVector) {
        if (result instanceof EntityHitResult entityResult && entityResult.getEntity() instanceof LivingEntity target){
            return new ShulkerBullet(level, caster, target, caster.getDirection().getAxis());
        }
        return null;
    }

    @Override
    protected boolean ignoreEntities() {
        return false;
    }

    @Override
    protected HitResult.Type getHitType() {
        return HitResult.Type.ENTITY;
    }

    @Override
    protected double getReachDistance(LivingEntity caster) {
        return 32;
    }
}
