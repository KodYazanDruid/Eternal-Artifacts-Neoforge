package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ShulkerBulletsSpell extends Spell {
    public ShulkerBulletsSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
        HitResult result = RayTraceHelper.retraceGeneric(caster, 32);
        if (result.getType() == HitResult.Type.ENTITY && result instanceof EntityHitResult entityResult &&
                entityResult.getEntity() instanceof LivingEntity target){
            ShulkerBullet bullet = new ShulkerBullet(level, caster, target, caster.getDirection().getAxis());
            level.addFreshEntity(bullet);
            caster.playSound(
                    SoundEvents.SHULKER_SHOOT, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F
            );
            return true;
        }
        return false;
    }
}
