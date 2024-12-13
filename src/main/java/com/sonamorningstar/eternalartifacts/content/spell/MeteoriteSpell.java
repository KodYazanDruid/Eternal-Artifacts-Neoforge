package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.Meteorite;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MeteoriteSpell extends Spell {
    public MeteoriteSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
        BlockHitResult result = RayTraceHelper.retrace(caster, ClipContext.Fluid.NONE);
        if (!checkCooldown(caster, tome.getItem())) {
            Vec3 eyePosition = caster.getEyePosition();
            Vec3 shootVector = eyePosition.vectorTo(result.getLocation());
            Meteorite meteorite = new Meteorite(level, caster, shootVector.x, shootVector.y, shootVector.z, amplifiedDamage);
            meteorite.setPosRaw(eyePosition.x, eyePosition.y, eyePosition.z);
            level.addFreshEntity(meteorite);
            return true;
        }
        return false;
    }
}
