package com.sonamorningstar.eternalartifacts.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sonamorningstar.eternalartifacts.content.enchantment.SoulboundEnchantment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Unique
    private ItemStack eternal_Artifacts_Neoforge$stack;

    @Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object redirectStack(List<Object> instance, int i) {
        eternal_Artifacts_Neoforge$stack = ((ItemStack) instance.get(i));
        return instance.get(i);
    }

    @ModifyExpressionValue(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean dropAll(boolean original) {
        /*if (original) {
            SoulboundEnchantment.has(eternal_Artifacts_Neoforge$stack);
        }
        return false;*/
        return original || SoulboundEnchantment.has(eternal_Artifacts_Neoforge$stack);
    }
}
