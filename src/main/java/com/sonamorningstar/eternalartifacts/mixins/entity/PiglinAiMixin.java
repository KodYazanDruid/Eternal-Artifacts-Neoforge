package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.sonamorningstar.eternalartifacts.mixin_helper.MixinHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {

    @Inject(method = "isWearingGold", at = @At(value = "HEAD"), cancellable = true)
    private static void isWearingGold(LivingEntity living, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = MixinHelper.getPiglinPacifier(living);
        if (!stack.isEmpty()) cir.setReturnValue(true);
    }
}
