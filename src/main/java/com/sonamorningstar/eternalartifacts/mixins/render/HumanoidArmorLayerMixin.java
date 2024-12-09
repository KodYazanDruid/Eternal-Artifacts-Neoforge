package com.sonamorningstar.eternalartifacts.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack renderArmorPiece(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        if (equipmentSlot != EquipmentSlot.HEAD) return original.call(instance, equipmentSlot);
        ItemStack turtleHelmet = MixinHelper.getTurtleHelmet(instance);
        return turtleHelmet.isEmpty() ? original.call(instance, equipmentSlot) : turtleHelmet;
    }
}
