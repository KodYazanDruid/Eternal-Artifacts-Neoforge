package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.LightningStrikeProjectile;
import com.sonamorningstar.eternalartifacts.content.spell.base.AbstractProjectileSpell;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class LightningStrikeSpell extends AbstractProjectileSpell {
    public LightningStrikeSpell(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    protected Projectile createProjectile(Level level, LivingEntity caster, float amplifiedDamage, HitResult result, Vec3 shootVector) {
        level.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.4F, 1.8F + caster.getRandom().nextFloat() * 0.4F);
        return new LightningStrikeProjectile(level, caster, shootVector.x, shootVector.y, shootVector.z, amplifiedDamage);
    }

    @Override
    protected float getVelocity(LivingEntity caster, Projectile projectile) {
        return 2.0F;
    }
}
