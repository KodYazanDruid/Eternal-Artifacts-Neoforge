package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.Tornado;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TornadoSpell extends Spell {
    public TornadoSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack stack, Level level, LivingEntity caster) {
        BlockHitResult result = RayTraceHelper.retrace(caster, ClipContext.Fluid.NONE);

        Vec3 casterPos = caster.position();
        Vec3 shootVector = caster.getEyePosition().vectorTo(result.getLocation());
        Tornado tornado = new Tornado(level, caster, shootVector.x, shootVector.y, shootVector.z, applySpellDamage(caster, 2.0F));
        tornado.setPos(casterPos.x, casterPos.y, casterPos.z);
        level.addFreshEntity(tornado);
        return true;
    }
}
