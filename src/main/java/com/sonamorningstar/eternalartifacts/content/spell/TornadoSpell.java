package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.Tornado;
import com.sonamorningstar.eternalartifacts.content.spell.base.AbstractProjectileSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TornadoSpell extends AbstractProjectileSpell {
    public TornadoSpell(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    protected Projectile createProjectile(Level level, LivingEntity caster, float amplifiedDamage, HitResult result, Vec3 shootVector) {
        return new Tornado(level, caster, shootVector.x, shootVector.y, shootVector.z, amplifiedDamage);
    }

    @Override
    protected boolean ignoreBlocks() {
        return true;
    }

    @Override
    protected Vec3 getStartVector(LivingEntity caster) {
        return caster.position().add(0, 0.2, 0);
    }

    @Override
    protected Vec3 getShootVector(LivingEntity caster, Vec3 start, HitResult result) {
        Vec3 direction = result.getLocation();
        Vec3 ray = start.vectorTo(new Vec3(direction.x, direction.y, direction.z));
        ray = new Vec3(ray.x, Math.min(ray.y, 0.5), ray.z);
        return ray;
    }
}
