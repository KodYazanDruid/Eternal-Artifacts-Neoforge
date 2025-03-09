package com.sonamorningstar.eternalartifacts.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.client.renderer.blockentity.ModSkullBlockRenderer;
import com.sonamorningstar.eternalartifacts.client.resources.model.TwoLayerSkullModel;
import com.sonamorningstar.eternalartifacts.mixin_helper.RenderOverrides;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CustomHeadLayer.class)
public class CustomHeadLayerMixin {

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack renderHead(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        //Ignoring the wildcard slot.
        ItemStack headEquipment = CharmStorage.get(instance).getStackInSlot(0);
        headEquipment = RenderOverrides.shouldRender(equipmentSlot, headEquipment) ?
                headEquipment : ItemStack.EMPTY;
        return headEquipment.isEmpty() ? original.call(instance, equipmentSlot) : headEquipment;
    }
    
    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;renderSkull(Lnet/minecraft/core/Direction;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/SkullModelBase;Lnet/minecraft/client/renderer/RenderType;)V"))
    private void renderSkull(Direction direction, float yRot, float mouthAnim, PoseStack pose, MultiBufferSource buffer, int light, SkullModelBase model, RenderType renderType, Operation<Void> original) {
        if (model instanceof TwoLayerSkullModel tlsm) {
            ModSkullBlockRenderer.renderModSkull(direction, yRot, mouthAnim, pose, buffer, light, tlsm);
        } else {
            original.call(direction, yRot, mouthAnim, pose, buffer, light, model, renderType);
        }
    }
}
