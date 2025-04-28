package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;

public class ChlorophyteRepeaterItem extends CrossbowItem {
	private static final int CAPACITY = 3;
	
	public ChlorophyteRepeaterItem(Properties pProperties) {
		super(pProperties);
	}
	
	public int getMagSize(ItemStack repeater) {
		int holding = repeater.getEnchantmentLevel(ModEnchantments.VOLUME.get());
		return CAPACITY + holding;
	}
	
	public static boolean isRepeaterCharged(ItemStack repeater) {
		CompoundTag tag = repeater.getTag();
		int loaded = getChargedProjectiles(repeater).size();
		return tag != null && loaded >= ((ChlorophyteRepeaterItem) repeater.getItem()).getMagSize(repeater);
	}
	
	public static void spendProjectile(ItemStack repeater) {
		CompoundTag compoundtag = repeater.getTag();
		if (compoundtag != null && compoundtag.contains("ChargedProjectiles", 9)) {
			ListTag listtag = compoundtag.getList("ChargedProjectiles", 10);
			listtag.remove(listtag.size() - 1);
			compoundtag.put("ChargedProjectiles", listtag);
		}
	}
	
	public static void performRepeaterShooting(Level level, LivingEntity shooter, InteractionHand hand,
											   ItemStack repeater, float velocity, float inaccuracy) {
		if (shooter instanceof Player player && net.neoforged.neoforge.event.EventHooks.onArrowLoose(repeater, shooter.level(), player, 1, true) < 0) return;
		List<ItemStack> list = getChargedProjectiles(repeater);
		float[] afloat = getShotPitches(shooter.getRandom());
		
		ItemStack itemstack = list.get(list.size() - 1);
		boolean flag = shooter instanceof Player && ((Player)shooter).getAbilities().instabuild;
		boolean hasMultishot = repeater.getEnchantmentLevel(Enchantments.MULTISHOT) > 0;
		shootProjectile(level, shooter, hand, repeater, itemstack, afloat[0], flag, velocity, inaccuracy, 0.0F);
		if (hasMultishot) {
			shootProjectile(level, shooter, hand, repeater, itemstack, afloat[1], flag, velocity, inaccuracy, -10.0F);
			shootProjectile(level, shooter, hand, repeater, itemstack, afloat[2], flag, velocity, inaccuracy, 10.0F);
		}
		
		onCrossbowShot(level, shooter, repeater);
	}
	
	public static boolean tryLoadRepeater(LivingEntity shooter, ItemStack repeater) {
		boolean flag = shooter instanceof Player && ((Player)shooter).getAbilities().instabuild;
		ItemStack itemstack = shooter.getProjectile(repeater);
		
		if (itemstack.isEmpty() && flag) {
			itemstack = new ItemStack(Items.ARROW);
		}
		boolean isMagicQuiver = itemstack.getItem() instanceof MagicQuiverItem;
		if (isMagicQuiver) itemstack = ((MagicQuiverItem) itemstack.getItem()).getAmmoStack(itemstack);
		
		return loadProjectile(shooter, repeater, itemstack, isMagicQuiver, flag);
	}
	
	public static boolean hasAnyProjectile(ItemStack repeater) {
		CompoundTag tag = repeater.getTag();
		return tag != null && tag.contains("ChargedProjectiles", 9) && !tag.getList("ChargedProjectiles", 10).isEmpty();
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pHand) {
		ItemStack itemstack = player.getItemInHand(pHand);
		if (hasAnyProjectile(itemstack) && !player.isCrouching()) {
			performShooting(pLevel, player, pHand, itemstack, getShootingPower(itemstack), 1.0F);
			return InteractionResultHolder.consume(itemstack);
		} else if (!player.getProjectile(itemstack).isEmpty() &&
				player.isCrouching() && !isCharged(itemstack)) {
			startSoundPlayed = false;
			midLoadSoundPlayed = false;
			player.startUsingItem(pHand);
			return InteractionResultHolder.consume(itemstack);
		} else {
			return InteractionResultHolder.fail(itemstack);
		}
	}
	
	@Override
	public void onUseTick(Level pLevel, LivingEntity livingEntity, ItemStack pStack, int pCount) {
		if (livingEntity.isCrouching()) super.onUseTick(pLevel, livingEntity, pStack, pCount);
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag flag) {
		var projectiles = getChargedProjectiles(pStack);
		if (!projectiles.isEmpty()) {
			tooltip.add(ModConstants.TOOLTIP.withSuffixTranslatable("repeater.projectiles"));
			for (int i = projectiles.size() - 1; i >= 0; i--) {
				ItemStack projectile = projectiles.get(i);
				tooltip.add(Component.literal(" ").append(projectile.getDisplayName()));
				if (flag.isAdvanced()) {
					List<Component> advancedTooltips = Lists.newArrayList();
					projectile.getItem().appendHoverText(projectile, pLevel, advancedTooltips, flag);
					if (!advancedTooltips.isEmpty()) {
						advancedTooltips.replaceAll(pSibling -> Component.literal("   ")
							.append(pSibling).withStyle(ChatFormatting.GRAY));
						tooltip.addAll(advancedTooltips);
					}
				}
			}
		}
	}
}
