package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandMenu.PotionSlot.class)
public class PotionSlotMixin {
	
	@Inject(method = "mayPlaceItem", at = @At("HEAD"), cancellable = true)
	private static void mayPlaceItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.is(ModItems.REINFORCED_GLASS_BOTTLE)) {
			cir.setReturnValue(true);
		}
	}
}
