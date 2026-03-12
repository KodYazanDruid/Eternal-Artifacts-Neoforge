package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.MagicMissileEntity;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagicMissileSpell extends Spell {
    public MagicMissileSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
        if (checkCooldown(caster, tome.getItem())) return false;
        if (!level.isClientSide()) {
            Vec3 look = caster.getLookAngle();
            double spread = 0.05;
            double dx = look.x + (random.nextDouble() - 0.5) * spread;
            double dy = look.y + (random.nextDouble() - 0.5) * spread;
            double dz = look.z + (random.nextDouble() - 0.5) * spread;

            MagicMissileEntity missile = new MagicMissileEntity(level, caster, dx, dy, dz, amplifiedDamage);
            Vec3 startPos = caster.getEyePosition();
            missile.setPos(startPos.x, startPos.y - 0.1, startPos.z);
            level.addFreshEntity(missile);
            level.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                SoundEvents.ENDER_PEARL_THROW, SoundSource.PLAYERS, 0.5F, 1.6F + random.nextFloat() * 0.4F);
        }
        return true;
    }
}
