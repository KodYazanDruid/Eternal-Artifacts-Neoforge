package com.sonamorningstar.eternalartifacts.content.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DivineProtectionEffect extends MobEffect {
    public DivineProtectionEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amp) {
        super.applyEffectTick(living, amp);
        if(living.getAbsorptionAmount() < 0f && !living.level().isClientSide()) {
            living.removeEffect(this);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amp) {
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity living, int amp) {
        super.onEffectStarted(living, amp);
        living.setAbsorptionAmount(Math.max(living.getAbsorptionAmount(), (float)(20 * (1 + amp))));
    }
}
