package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack updateFallFlying(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        LivingEntity living = (LivingEntity) (Object) this;
        ItemStack elytra = MixinHelper.getElytraFly(living);
        return elytra.isEmpty() ? original.call(instance, equipmentSlot) : elytra;
    }

    @WrapOperation(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack checkTotemDeath(LivingEntity instance, InteractionHand hand, Operation<ItemStack> original) {
        LivingEntity living = (LivingEntity) (Object) this;
        ItemStack totem = MixinHelper.getUndyingTotem(living);
        return totem.isEmpty() ? original.call(instance, hand) : totem;
    }
}
