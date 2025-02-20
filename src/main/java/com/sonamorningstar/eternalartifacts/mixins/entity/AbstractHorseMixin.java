package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {
	
	@WrapOperation(method = "isFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;test(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean isFood(Ingredient instance, ItemStack stack, Operation<Boolean> original) {
		return stack.is(ModItems.GREEN_APPLE.get()) || stack.is(ModItems.YELLOW_APPLE.get()) || original.call(instance, stack);
	}
	
	@WrapOperation(method = "handleEating", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 3))
	private boolean handleEating(ItemStack instance, Item item, Operation<Boolean> original) {
		return instance.is(ModItems.GREEN_APPLE.get()) || instance.is(ModItems.YELLOW_APPLE.get()) || original.call(instance, item);
	}
}
