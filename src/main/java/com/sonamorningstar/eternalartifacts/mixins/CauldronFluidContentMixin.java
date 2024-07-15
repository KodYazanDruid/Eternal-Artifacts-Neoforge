package com.sonamorningstar.eternalartifacts.mixins;

import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Debug(export = true)
@Mixin(value = CauldronFluidContent.class)
public abstract class CauldronFluidContentMixin {

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/fluids/RegisterCauldronFluidContentEvent;register(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/material/Fluid;ILnet/minecraft/world/level/block/state/properties/IntegerProperty;)V", ordinal = 2), index = 3)
    private static IntegerProperty setLavaLevel(@Nullable IntegerProperty levelProperty) {
        return LayeredCauldronBlock.LEVEL;
    }
}
