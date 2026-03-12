package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.SonicBoomSpellEntity;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SonicBoomSpell extends Spell {
    public SonicBoomSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
        if (checkCooldown(caster, tome.getItem())) return false;

        if (!level.isClientSide()) {
            SonicBoomSpellEntity sonicBoom = new SonicBoomSpellEntity(level, caster, amplifiedDamage);
            level.addFreshEntity(sonicBoom);
        }
        return true;
    }
}
