package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModTiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class SwordOfTheGreenEarthItem extends SwordItem {
    public SwordOfTheGreenEarthItem(Properties pProperties) {
        super(ModTiers.CHLOROPHYTE, 3, -2.4f, pProperties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        //target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
        target.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 100, 0));

        return super.hurtEnemy(stack, target, attacker);
    }
}
