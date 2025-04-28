package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.enchantment.VersatilityEnchantment;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.event.common.CommonEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class MagicQuiverItem extends ArrowItem {
	public MagicQuiverItem(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == ModEnchantments.VERSATILITY.get() || super.canApplyAtEnchantingTable(stack, enchantment);
	}
	
	@Override
	public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
		boolean versatile = VersatilityEnchantment.has(stack);
		AbstractArrow arrow;
		if (versatile) arrow = new SpectralArrow(level, shooter, Items.SPECTRAL_ARROW.getDefaultInstance());
		else arrow =  new Arrow(level, shooter, Items.ARROW.getDefaultInstance());
		arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		return arrow;
	}
	
	public ItemStack getAmmoStack(ItemStack quiver) {
		ItemStack ammo;
		if (VersatilityEnchantment.has(quiver)) ammo = new ItemStack(Items.SPECTRAL_ARROW, 64);
		else ammo = new ItemStack(Items.ARROW, 64);
		CompoundTag tag = ammo.getOrCreateTag();
		tag.putBoolean(CommonEvents.TAG_KEY, true);
		return ammo;
	}
	
	@Override
	public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
		return true;
	}
}
