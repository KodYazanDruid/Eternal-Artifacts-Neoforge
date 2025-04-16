package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import com.sonamorningstar.eternalartifacts.content.recipe.custom.DemonIngotCraftingInWorld;
import com.sonamorningstar.eternalartifacts.content.recipe.custom.SteelCraftingInWorld;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<? extends ItemEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(at = @At("RETURN"), method = "tick")
    private void handleTransform(CallbackInfo ci) {
        if(this.isRemoved()) return;
        ItemEntity self = (ItemEntity) (Object) this;
        if(SteelCraftingInWorld.isValidItem(self.getItem()) && SteelCraftingInWorld.isInCorrectEnvironment(blockPosition(), level())) {
            if(!level().isClientSide) SteelCraftingInWorld.tryTransform(self);
        }
        if(DemonIngotCraftingInWorld.isDemonIngotCandidate(self.getItem().getItem()) && DemonIngotCraftingInWorld.isInCorrectEnvironment(blockPosition(), level())) {
            if(!level().isClientSide) DemonIngotCraftingInWorld.tryTransform(self);
        }
    }
    
    @WrapOperation(method = "fireImmune", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isFireResistant()Z"))
    private boolean fireImmune(Item instance, Operation<Boolean> original){
        ItemStack stack = getItem();
        if (stack.getItem() instanceof MachineBlockItem) {
            return stack.getEnchantmentLevel(Enchantments.FIRE_PROTECTION) > 0;
        }
        return original.call(instance);
    }
    
    @Inject(method = "hurt", at = @At(value = "HEAD"), cancellable = true)
    private void hurt(DamageSource src, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = getItem();
        if (stack.getItem() instanceof MachineBlockItem) {
            if (stack.getEnchantmentLevel(Enchantments.BLAST_PROTECTION) >= 4) {
                cir.setReturnValue(false);
            }
        }
    }
}
