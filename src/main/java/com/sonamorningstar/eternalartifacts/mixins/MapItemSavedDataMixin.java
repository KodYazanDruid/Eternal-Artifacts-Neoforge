package com.sonamorningstar.eternalartifacts.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MapItemSavedData.class)
public class MapItemSavedDataMixin {

    @WrapOperation(method = "tickCarriedBy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;contains(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean tickCarriedBy(Inventory instance, ItemStack stack, Operation<Boolean> original) {
        return PlayerCharmManager.findCharm(instance.player, stack) || original.call(instance, stack);
    }
}
