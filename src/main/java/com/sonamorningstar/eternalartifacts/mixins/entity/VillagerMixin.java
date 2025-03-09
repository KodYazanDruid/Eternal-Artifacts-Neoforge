package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin {
	
	@Inject(method = "updateSpecialPrices", at = @At(value = "TAIL"))
	private void updatePrices(Player player, CallbackInfo ci) {
		ItemStack signet = CharmManager.findCharm(player, ModItems.EMERALD_SIGNET.get());
		if (!signet.isEmpty()) {
			Villager villager = (Villager) (Object) this;
			for (MerchantOffer offer : villager.getOffers()) {
				int discount = Mth.floor(offer.getBaseCostA().getCount() * 0.35);
				offer.addToSpecialPriceDiff(-Math.max(discount, 1));
			}
		}
	}
}
