package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.EntityJumpFactor;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingDasher;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingJumper;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingJumper, ILivingDasher {
    @Shadow protected abstract void jumpFromGround();
    @Shadow public abstract double getAttributeValue(Attribute pAttribute);
    @Shadow public abstract float getJumpBoostPower();
    
    @Unique
    public int dashCooldown = 0;
    
    @Inject(method = "aiStep", at = @At(value = "HEAD"))
    private void aiStep(CallbackInfo ci) {
        if (dashCooldown > 0) {
            dashCooldown--;
        }
    }
    
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
    
    @Unique
    @Override
    public void jumpGround() {
        this.jumpFromGround();
    }
    
    @Override
    public void dashAir(LivingEntity living) {
        Vec3 oldMotion = living.getDeltaMovement();
        Vec3 dashDirection = new Vec3(oldMotion.x(), 0, oldMotion.z()).normalize();
        double dashFactor = 5;
        Vec3 dashVector = dashDirection.scale(dashFactor * getAttributeValue(Attributes.MOVEMENT_SPEED));
        living.addDeltaMovement(dashVector);
        living.hasImpulse = true;
        living.resetFallDistance();
    }
    
    @Override
    public int dashCooldown() {
        return dashCooldown;
    }
    
    @Override
    public void setDashCooldown(int dashCooldown) {
        this.dashCooldown = dashCooldown;
    }
}