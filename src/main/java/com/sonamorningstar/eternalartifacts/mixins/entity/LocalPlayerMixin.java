package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aiStep(LocalPlayer instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        ItemStack elytra = MixinHelper.getElytraFly(player);
        return elytra.isEmpty() ? original.call(instance, equipmentSlot) : elytra;
    }
}
