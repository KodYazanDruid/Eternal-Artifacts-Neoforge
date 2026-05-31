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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SwordOfTheTwilightItem extends SwordItem {
	public static final Set<MobEffect> HARMFULL_EFFECTS = BuiltInRegistries.MOB_EFFECT.stream().filter(e -> e.getCategory() == MobEffectCategory.HARMFUL && !e.isInstantenous()).collect(Collectors.toSet());
	
	public SwordOfTheTwilightItem(Properties pProperties) {
		super(ModTiers.STEEL, 2, -2.4F, pProperties);
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker instanceof Player player && player.getAttackStrengthScale(0.0F) != 1.0F)
			return super.hurtEnemy(stack, target, attacker);
		
		RandomSource random = attacker.getRandom();
		target.addEffect(Objects.requireNonNull(HARMFULL_EFFECTS.stream()
			.skip(random.nextInt(HARMFULL_EFFECTS.size()))
			.findFirst()
			.map(mobEffect -> new MobEffectInstance(mobEffect, 100, 0))
			.orElse(null)));
		
		return super.hurtEnemy(stack, target, attacker);
	}
}
