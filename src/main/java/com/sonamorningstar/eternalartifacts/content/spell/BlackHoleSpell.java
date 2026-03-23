package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.BlackHoleEntity;
import com.sonamorningstar.eternalartifacts.content.spell.base.AbstractProjectileSpell;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlackHoleSpell extends AbstractProjectileSpell {
    public BlackHoleSpell(Spell.Properties props) {
        super(props);
    }
    
    @Nullable
    @Override
    protected Projectile createProjectile(Level level, LivingEntity caster, float amplifiedDamage, HitResult result, Vec3 shootVector) {
        return caster instanceof Player p ? new BlackHoleEntity(level, p) : null;
    }
}
