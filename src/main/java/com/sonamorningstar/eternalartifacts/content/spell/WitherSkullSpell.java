package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.SpellWitherSkull;
import com.sonamorningstar.eternalartifacts.content.spell.base.AbstractProjectileSpell;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WitherSkullSpell extends AbstractProjectileSpell {

    public WitherSkullSpell(Properties props) {
        super(props);
    }
    
    @Nullable
    @Override
    protected Projectile createProjectile(Level level, LivingEntity caster, float amplifiedDamage, HitResult result, Vec3 shootVector) {
        return new SpellWitherSkull(level, caster, shootVector.x, shootVector.y, shootVector.z, amplifiedDamage);
    }
    
    
}
