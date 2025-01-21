package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class CutlassItem extends SwordItem {
    public CutlassItem(Tier tier, Properties props) {
        super(tier, 4, -2.4F, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity target, LivingEntity pAttacker) {
        if (pStack.is(ModItems.CHLOROPHYTE_CUTLASS)) {
            target.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 100, 0));
        }
        return super.hurtEnemy(pStack, target, pAttacker);
    }
}
