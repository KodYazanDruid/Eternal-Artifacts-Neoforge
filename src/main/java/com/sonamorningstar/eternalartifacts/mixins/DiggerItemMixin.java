package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.TierSortingRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin extends TieredItem {

    @Shadow @Final
    protected float speed;
    //stack.getItem() instanceof PickaxeItem
    public DiggerItemMixin(Tier pTier, Properties pProperties) {super(pTier, pProperties);}

    @Inject(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"), cancellable = true)
    private void getDestroySpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (stack.canApplyAtEnchantingTable(ModEnchantments.VERSATILITY.get()) && stack.getEnchantmentLevel(ModEnchantments.VERSATILITY.get()) > 0) {
            cir.setReturnValue(state.is(ModTags.Blocks.VERSATILITY_MINEABLES) ? this.speed : 1.0F);
        }
    }

    @Inject(method = "isCorrectToolForDrops(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"), cancellable = true)
    private void isCorrectToolForDrops(ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (stack.canApplyAtEnchantingTable(ModEnchantments.VERSATILITY.get()) && stack.getEnchantmentLevel(ModEnchantments.VERSATILITY.get()) > 0) {
            cir.setReturnValue(state.is(ModTags.Blocks.VERSATILITY_MINEABLES) && TierSortingRegistry.isCorrectTierForDrops(getTier(), state));
        }
    }
}
