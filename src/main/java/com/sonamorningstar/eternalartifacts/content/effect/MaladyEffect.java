package com.sonamorningstar.eternalartifacts.content.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

import java.util.Set;

public class MaladyEffect extends MobEffect {
    public MaladyEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }
    
    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(EffectCures.HONEY);
    }
}
