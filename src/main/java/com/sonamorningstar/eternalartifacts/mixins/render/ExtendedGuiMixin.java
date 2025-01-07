package com.sonamorningstar.eternalartifacts.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExtendedGui.class)
public abstract class ExtendedGuiMixin {

    @Shadow public abstract Minecraft getMinecraft();

    @WrapOperation(method = "renderHelmet", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getArmor(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack renderHelmet(Inventory instance, int pSlot, Operation<ItemStack> original) {
        ItemStack carved = PlayerCharmManager.findCharm(getMinecraft().player, Items.CARVED_PUMPKIN);
        return carved.isEmpty() ? original.call(instance, pSlot) : carved;
    }
}
