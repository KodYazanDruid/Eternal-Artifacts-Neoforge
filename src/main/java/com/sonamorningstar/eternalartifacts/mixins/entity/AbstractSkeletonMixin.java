package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeleton.class)
public class AbstractSkeletonMixin {

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/AbstractSkeleton;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aiStep(AbstractSkeleton instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        ItemStack shell = MixinHelper.getShulkerShell(instance);
        ItemStack turtleHelmet = MixinHelper.getTurtleHelmet(instance);
        return shell.isEmpty() ? turtleHelmet.isEmpty() ? original.call(instance, equipmentSlot) : turtleHelmet : shell;
    }
}
