package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.content.enchantment.VersatilityEnchantment;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "canPerformAction", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), cancellable = true)
    private void canPerformAction(ItemStack stack, ToolAction toolAction, CallbackInfoReturnable<Boolean> cir) {
        if (VersatilityEnchantment.has(stack)) {
            cir.setReturnValue(ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction));
        }
    }
}
