package com.sonamorningstar.eternalartifacts.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.content.item.ChlorophyteRepeaterItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
	
	@Inject(method = "isCharged", at = @At("HEAD"), cancellable = true)
	private static void isCharged(ItemStack crossbow, CallbackInfoReturnable<Boolean> cir) {
		if (crossbow.getItem() instanceof ChlorophyteRepeaterItem) {
			cir.setReturnValue(ChlorophyteRepeaterItem.isRepeaterCharged(crossbow));
		}
	}
	
	@WrapWithCondition(method = "setCharged", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putBoolean(Ljava/lang/String;Z)V"))
	private static boolean setCharged(CompoundTag instance, String pKey, boolean pValue, ItemStack stack) {
		return !(stack.getItem() instanceof ChlorophyteRepeaterItem);
	}
	
	@WrapOperation(method = "onCrossbowShot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;clearChargedProjectiles(Lnet/minecraft/world/item/ItemStack;)V"))
	private static void clear(ItemStack stack, Operation<Void> original) {
		if (stack.getItem() instanceof ChlorophyteRepeaterItem) {
			ChlorophyteRepeaterItem.spendProjectile(stack);
		} else original.call(stack);
	}
	
	@Inject(method = "performShooting", at = @At("HEAD"), cancellable = true)
	private static void performShooting(Level pLevel, LivingEntity pShooter, InteractionHand pUsedHand, ItemStack crossbow, float pVelocity, float pInaccuracy, CallbackInfo ci) {
		if (crossbow.getItem() instanceof ChlorophyteRepeaterItem) {
			ChlorophyteRepeaterItem.performRepeaterShooting(pLevel, pShooter, pUsedHand, crossbow, pVelocity, pInaccuracy);
			ci.cancel();
		}
	}
	
	@Inject(method = "tryLoadProjectiles", at = @At("HEAD"), cancellable = true)
	private static void tryLoadProjectiles(LivingEntity shooter, ItemStack crossbow, CallbackInfoReturnable<Boolean> cir) {
		if (crossbow.getItem() instanceof ChlorophyteRepeaterItem) {
			cir.setReturnValue(ChlorophyteRepeaterItem.tryLoadRepeater(shooter, crossbow));
			cir.cancel();
		}
	}
}
