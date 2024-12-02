package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireballSpell extends Spell {
    public FireballSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack stack, Level level, LivingEntity caster, float amplifiedDamage) {
        BlockHitResult result = RayTraceHelper.retrace(caster, ClipContext.Fluid.NONE);
        if (result.getType() == HitResult.Type.MISS) {
            Vec3 eyePosition = caster.getEyePosition();
            Vec3 shootVector = eyePosition.vectorTo(result.getLocation());
            LargeFireball fireball = new LargeFireball(level, caster, shootVector.x, shootVector.y, shootVector.z, 2);
            fireball.setPosRaw(eyePosition.x, eyePosition.y, eyePosition.z);
            fireball.setDeltaMovement(fireball.getDeltaMovement().scale(1.3D));
            level.addFreshEntity(fireball);
            return true;
        }
        return false;
    }
}
