package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.MobHarvester;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingDasher;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingJumper;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.LivingEntityExposer;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingJumper, ILivingDasher, LivingEntityExposer {
    @Shadow protected abstract void jumpFromGround();
    @Shadow public abstract double getAttributeValue(Attribute pAttribute);
    
    @Shadow protected int fallFlyTicks;
    
    @Shadow private float swimAmount;
    @Shadow private float swimAmountO;
    
    @Shadow protected abstract void setLivingEntityFlag(int pKey, boolean pValue);
    
    @Shadow protected int autoSpinAttackTicks;
    
    @Shadow protected abstract void updateUsingItem(ItemStack pUsingItem);
    
    @Shadow protected ItemStack useItem;
    @Shadow protected int useItemRemaining;
    
    @Shadow protected int attackStrengthTicker;
    
    @Shadow @Nullable protected Player lastHurtByPlayer;
    @Unique
    public int dashCooldown = 0;
    
    @Unique
    public Vec3 prevDeltaMovement = Vec3.ZERO;
    
    @Inject(method = "baseTick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        prevDeltaMovement = living.getDeltaMovement();
    }
    
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
    
    @WrapOperation(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Ljava/util/Collection;forEach(Ljava/util/function/Consumer;)V"))
    private void dropLoot(Collection<ItemEntity> instance, Consumer<ItemEntity> consumer, Operation<Void> original, DamageSource source) {
        Entity entity = source.getEntity();
        if (entity instanceof FakePlayer fakePlayer && "EternalArtifactsMobHarvester".equals(fakePlayer.getGameProfile().getName())) {
            BlockEntity be = fakePlayer.level().getBlockEntity(fakePlayer.blockPosition());
            if (be instanceof MobHarvester harvester) {
                for (ItemEntity itemEntity : instance) {
                    ItemStack remainder = ItemHelper.insertItemStackedForced(harvester.inventory, itemEntity.getItem(), false);
                    if (!remainder.isEmpty()) {
                        consumer.accept(new ItemEntity(fakePlayer.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), remainder));
                    }
                }
                return;
            }
        }
        original.call(instance, consumer);
    }
    
    @WrapOperation(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void dropExp(ServerLevel serverLevel, Vec3 pos, int reward, Operation<Void> original) {
        Player player = this.lastHurtByPlayer;
        if (player instanceof FakePlayer fakePlayer && "EternalArtifactsMobHarvester".equals(fakePlayer.getGameProfile().getName())) {
            BlockEntity be = fakePlayer.level().getBlockEntity(fakePlayer.blockPosition());
            if (be instanceof MobHarvester harvester) {
                while (reward > 0) {
                    int filled = harvester.tank.fillForced(ModFluids.NOUS.getFluidStack(20), IFluidHandler.FluidAction.SIMULATE);
                    if (filled == 20) {
                        harvester.tank.fillForced(ModFluids.NOUS.getFluidStack(20), IFluidHandler.FluidAction.EXECUTE);
                        reward--;
                    } else break;
                }
            }
        }
        original.call(serverLevel, pos, reward);
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
    
    @Override
    public void setFallFlyTicks(int fallFlyTicks) {
        this.fallFlyTicks = fallFlyTicks;
    }
    
    @Override
    public void setLivingEntityFlagExp(int key, boolean value) {
        this.setLivingEntityFlag(key, value);
    }
    
    @Override
    public float getSwimAmountExp() {
        return this.swimAmount;
    }
    @Override
    public float getSwimAmount0Exp() {
        return swimAmountO;
    }
    @Override
    public void setSwimAmountExp(float amount) {
        this.swimAmount = amount;
    }
    @Override
    public void setSwimAmount0Exp(float amount) {
        this.swimAmountO = amount;
    }
    
    @Override
    public int getAutoSpinAttackTicks() {
        return this.autoSpinAttackTicks;
    }
    
    @Override
    public void setAutoSpinAttackTicks(int autoSpinAttackTicks) {
        this.autoSpinAttackTicks = autoSpinAttackTicks;
    }
    
    @Override
    public void setUseItemExp(ItemStack stack) {
        this.useItem = stack;
    }
    
    @Override
    public void setUseItemRemainingTicksExp(int ticks) {
        this.useItemRemaining = ticks;
    }
    
    @Override
    public void updateUsingItemExp(ItemStack stack) {
        this.updateUsingItem(stack);
    }
    
    @Override
    public int incrementAttackStrengthTicker(int amount) {
        attackStrengthTicker += amount;
        return attackStrengthTicker;
    }
    
}