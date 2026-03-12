package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.PrismBeamEntity;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PrismBeamSpell extends Spell {
    public PrismBeamSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
        if (checkCooldown(caster, tome.getItem())) return false;
        if (!level.isClientSide()) {
            boolean beamExists = !level.getEntitiesOfClass(PrismBeamEntity.class,
                caster.getBoundingBox().inflate(2.0),
                beam -> caster.getUUID().equals(beam.getOwnerUUID())
            ).isEmpty();

            if (!beamExists) {
                PrismBeamEntity beam = new PrismBeamEntity(level, caster, amplifiedDamage);
                level.addFreshEntity(beam);
                level.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
        }
        return true;
    }
}
