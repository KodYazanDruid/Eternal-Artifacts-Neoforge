package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.api.morph.MobModelRenderer;
import com.sonamorningstar.eternalartifacts.api.morph.PlayerMorphUtil;
import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aiStep(LocalPlayer instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        ItemStack elytra = MixinHelper.getElytraFly(player);
        return elytra.isEmpty() ? original.call(instance, equipmentSlot) : elytra;
    }
    
    //TODO: HMMMMM?
    @ModifyConstant(method = "aiStep", constant = @Constant(floatValue = 0.2F))
    private float movementImpulse(float constant) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getUseItem();
        if(!(stack.getItem() instanceof BowItem)) return constant;
        //ItemStack head = PlayerMorphUtil.getMorphItem(player);
        //if (head.is(Items.SKELETON_SKULL)) {
        if (MobModelRenderer.dummy == null) return constant;
        if (MobModelRenderer.dummy.getType() == EntityType.SKELETON) {
            return 1.0F;
        }
        return constant;
    }
}
