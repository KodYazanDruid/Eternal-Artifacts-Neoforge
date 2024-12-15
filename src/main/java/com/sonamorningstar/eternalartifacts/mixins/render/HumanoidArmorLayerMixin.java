package com.sonamorningstar.eternalartifacts.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import com.sonamorningstar.eternalartifacts.mixin_helper.RenderOverrides;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack renderArmorPiece(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        //return eternal_Artifacts_Neoforge$getEquipment(instance, equipmentSlot, MixinHelper::getHeadEquipment);

        return switch (equipmentSlot) {
            case HEAD -> {
                ItemStack headEquipment = MixinHelper.getHeadEquipment(instance);
                /*headEquipment = RenderOverrides.shouldRender(equipmentSlot, headEquipment) ?
                        headEquipment : ItemStack.EMPTY;*/
                yield headEquipment.isEmpty() ? original.call(instance, equipmentSlot) : headEquipment;
            }
            case CHEST -> {
                ItemStack chestEquipment = MixinHelper.getChestEquipment(instance);
                chestEquipment = RenderOverrides.shouldRender(equipmentSlot, chestEquipment) ?
                        chestEquipment : ItemStack.EMPTY;
                yield chestEquipment.isEmpty() ? original.call(instance, equipmentSlot) :
                        chestEquipment.getItem() instanceof ArmorItem ? chestEquipment : original.call(instance, equipmentSlot);
            }
            case LEGS -> {
                ItemStack legsEquipment = MixinHelper.getLegsEquipment(instance);
                legsEquipment = RenderOverrides.shouldRender(equipmentSlot, legsEquipment) ?
                        legsEquipment : ItemStack.EMPTY;
                yield legsEquipment.isEmpty() ? original.call(instance, equipmentSlot) :
                        legsEquipment.getItem() instanceof ArmorItem ? legsEquipment : original.call(instance, equipmentSlot);
            }
            case FEET -> {
                ItemStack feetEquipment = MixinHelper.getFeetEquipment(instance);
                feetEquipment = RenderOverrides.shouldRender(equipmentSlot, feetEquipment) ?
                        feetEquipment : ItemStack.EMPTY;
                yield feetEquipment.isEmpty() ? original.call(instance, equipmentSlot) :
                        feetEquipment.getItem() instanceof ArmorItem ? feetEquipment : original.call(instance, equipmentSlot);
            }
            default -> original.call(instance, equipmentSlot);
        };
    }

    @Unique
    private ItemStack eternal_Artifacts_Neoforge$getEquipment(LivingEntity living, EquipmentSlot slot, Function<LivingEntity, ItemStack> equipmentGetter) {
        ItemStack equipment = equipmentGetter.apply(living);
        equipment = RenderOverrides.shouldRender(slot, equipment) ? equipment : ItemStack.EMPTY;
        return equipment;
    }
}
