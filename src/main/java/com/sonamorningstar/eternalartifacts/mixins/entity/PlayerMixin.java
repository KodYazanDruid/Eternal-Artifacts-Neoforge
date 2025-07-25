package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.content.enchantment.SoulboundEnchantment;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @WrapOperation(method = "turtleHelmetTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            ))
    private ItemStack turtleHelmetTick(Player instance, EquipmentSlot pSlot1, Operation<ItemStack> original) {
        Player player = (Player) (Object) this;
        ItemStack helmet = CharmManager.findCharm(player, Items.TURTLE_HELMET);
        return helmet.isEmpty() ? original.call(instance, pSlot1) : helmet;
    }

    @WrapOperation(method = "tryToStartFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            ))
    private ItemStack tryToStartFallFlying(Player instance, EquipmentSlot pSlot1, Operation<ItemStack> original) {
        Player player = (Player) (Object) this;
        ItemStack elytra = MixinHelper.getElytraFly(player);
        return elytra.isEmpty() ? original.call(instance, pSlot1) : elytra;
    }
    
    @Inject(method = "getProjectile", at = @At(value = "HEAD"), cancellable = true)
    private void getProjectile(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
        Player player = (Player) (Object) this;
        ItemStack quiver = CharmManager.findCharm(player, ModItems.MAGIC_QUIVER.get());
        if (!quiver.isEmpty()) cir.setReturnValue(quiver);
    }
    
    @Inject(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"))
    private void dropEquipment(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
        var charms = CharmStorage.get(player);
        for (int i = 0; i < charms.getSlots(); i++) {
            var charm = charms.getStackInSlot(i);
            if (!charm.isEmpty()) {
                if (EnchantmentHelper.hasVanishingCurse(charm)) {
                    charms.setStackInSlot(i, ItemStack.EMPTY);
                    continue;
                }
                if (SoulboundEnchantment.has(charm)) {
                    continue;
                }
            }
            player.drop(charm, true, false);
            charms.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
    
}
