package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTiers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

import java.util.List;

public class SwordOfTheTwilightItem extends SwordItem {
	public static final List<MobEffect> HARMFUL_EFFECTS = BuiltInRegistries.MOB_EFFECT.stream().filter(e -> e.getCategory() == MobEffectCategory.HARMFUL && !e.isInstantenous()).toList();
	
	public SwordOfTheTwilightItem(Properties pProperties) {
		super(ModTiers.STEEL, 2, -2.4F, pProperties);
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker instanceof Player player && player.getAttackStrengthScale(0.0F) != 1.0F)
			return super.hurtEnemy(stack, target, attacker);
		
		RandomSource random = target.getRandom();
		for (int i = 0; i < HARMFUL_EFFECTS.size(); i++) {
			MobEffect effect = HARMFUL_EFFECTS.get(random.nextInt(HARMFUL_EFFECTS.size()));
			MobEffectInstance instance = new MobEffectInstance(effect, 100);
			if (target.canBeAffected(instance)) {
				target.addEffect(instance);
				break;
			}
		}
		
		return super.hurtEnemy(stack, target, attacker);
	}
}