package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModTiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;

public class LushGrubberItem extends HoeItem {
    public LushGrubberItem(Properties pProperties) {
        super(ModTiers.CHLOROPHYTE, -3, 0.0f, pProperties);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity target, LivingEntity pAttacker) {
        target.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 100, 0));
        return super.hurtEnemy(pStack, target, pAttacker);
    }
}
