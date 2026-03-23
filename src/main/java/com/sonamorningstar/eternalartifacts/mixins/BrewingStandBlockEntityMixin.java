package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin {
	
	@Shadow public abstract ItemStack getItem(int index);
	
	@Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
	private void acceptedItems(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if ((index == 0 || index == 1 || index == 2) && getItem(index).isEmpty() && stack.is(ModItems.REINFORCED_GLASS_BOTTLE)) {
			cir.setReturnValue(true);
		}
	}
}
