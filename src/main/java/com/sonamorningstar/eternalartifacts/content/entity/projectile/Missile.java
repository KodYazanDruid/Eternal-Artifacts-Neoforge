package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;

public class Missile extends AbstractHurtingProjectile {
    private float damage;

    public Missile(
            Level level,
            LivingEntity shooter,
            double offsetX,
            double offsetY,
            double offsetZ,
            float damage
    ) {
        super(ModEntities.MISSILE.get(), shooter, offsetX, offsetY, offsetZ, level);
        this.damage = damage;
    }
    public Missile(EntityType<? extends Missile> type, Level level) {
        super(type, level);
    }
}
