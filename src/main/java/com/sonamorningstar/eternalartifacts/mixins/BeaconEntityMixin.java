package com.sonamorningstar.eternalartifacts.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.content.item.MagicFeatherItem;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = BeaconBlockEntity.class)
public abstract class BeaconEntityMixin {

    @Shadow
    private static void applyEffects(Level pLevel, BlockPos pPos, int pLevels, @Nullable MobEffect pPrimary, @Nullable MobEffect pSecondary) {}

    @WrapWithCondition(method = "applyEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 0))
    private static boolean applyEffectsCheck(Player player, MobEffectInstance mobEffectInstance, Level level, BlockPos pos, int j, @Nullable MobEffect primary, @Nullable MobEffect secondary) {
        int duration = mobEffectInstance.getDuration();
        return primary != ModEffects.FLIGHT.get() || (primary == ModEffects.FLIGHT.get() && eternal_Artifacts_Neoforge$findFeather(player, duration));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/effect/MobEffect;Lnet/minecraft/world/effect/MobEffect;)V"))
    private static void tick(Level pLevel, BlockPos pPos, BlockState pState, BeaconBlockEntity pBlockEntity, CallbackInfo ci) {
        applyEffects(pLevel, pPos, pBlockEntity.levels, ModEffects.FLIGHT.get(), null);
    }

    @Unique
    private static boolean eternal_Artifacts_Neoforge$findFeather(Player player, int ticks){
        MagicFeatherItem feather = (MagicFeatherItem) ModItems.MAGIC_FEATHER.get();
        if(PlayerHelper.findItem(player, feather)) {
            MagicFeatherItem.activeTicks = Pair.of(true, ticks);
            return true;
        }else {
            return false;
        }
    }

}
