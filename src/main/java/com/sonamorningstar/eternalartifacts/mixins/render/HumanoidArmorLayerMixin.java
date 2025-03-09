package com.sonamorningstar.eternalartifacts.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.mixin_helper.RenderOverrides;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack renderArmorPiece(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        return switch (equipmentSlot) {
            case HEAD -> {
                //ItemStack head = CharmManager.getHeadEquipment(instance);
                ItemStack head = CharmStorage.get(instance).getStackInSlot(0);
                boolean shouldRender = RenderOverrides.shouldRender(equipmentSlot, head);
                yield head.isEmpty() || !shouldRender ? original.call(instance, equipmentSlot) : head;
            }
            case CHEST -> {
                //ItemStack chest = CharmManager.getChestEquipment(instance);
                ItemStack chest = CharmStorage.get(instance).getStackInSlot(9);
                chest = RenderOverrides.shouldRender(equipmentSlot, chest) ? chest : ItemStack.EMPTY;
                yield chest.isEmpty() ? original.call(instance, equipmentSlot) :
                        chest.getItem() instanceof ArmorItem ? chest : original.call(instance, equipmentSlot);
            }
            case LEGS -> {
                //ItemStack legs = CharmManager.getLegsEquipment(instance);
                ItemStack legs = CharmStorage.get(instance).getStackInSlot(4);
                legs = RenderOverrides.shouldRender(equipmentSlot, legs) ? legs : ItemStack.EMPTY;
                yield legs.isEmpty() ? original.call(instance, equipmentSlot) :
                        legs.getItem() instanceof ArmorItem ? legs : original.call(instance, equipmentSlot);
            }
            case FEET -> {
                //ItemStack feet = CharmManager.getFeetEquipment(instance);
                ItemStack feet = CharmStorage.get(instance).getStackInSlot(5);
                feet = RenderOverrides.shouldRender(equipmentSlot, feet) ? feet : ItemStack.EMPTY;
                yield feet.isEmpty() ? original.call(instance, equipmentSlot) :
                        feet.getItem() instanceof ArmorItem ? feet : original.call(instance, equipmentSlot);
            }
            default -> original.call(instance, equipmentSlot);
        };
    }
}
