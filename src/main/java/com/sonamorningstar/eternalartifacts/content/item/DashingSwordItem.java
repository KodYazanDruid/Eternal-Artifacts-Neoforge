package com.sonamorningstar.eternalartifacts.content.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DashingSwordItem extends SwordItem {
	public DashingSwordItem(Tier pTier, Properties pProperties) {
		super(pTier, 0, -2.0F, pProperties);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand hand) {
		var cd = player.getCooldowns();
		ItemStack stack = player.getItemInHand(hand);
		if (!cd.isOnCooldown(stack.getItem())){
			Vec3 looking = player.getLookAngle();
			Vec3 motion = looking.scale(10 * player.getAttributeValue(Attributes.MOVEMENT_SPEED));
			player.addDeltaMovement(motion);
			player.hasImpulse = true;
			player.resetFallDistance();
			player.getCooldowns().addCooldown(stack.getItem(), 50);
			return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
		}
		return super.use(pLevel, player, hand);
	}
}
