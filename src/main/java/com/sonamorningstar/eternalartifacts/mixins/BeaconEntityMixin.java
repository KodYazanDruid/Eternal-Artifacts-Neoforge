package com.sonamorningstar.eternalartifacts.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Objects;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Debug(export = true)
@Mixin(value = BeaconBlockEntity.class)
public class BeaconEntityMixin {

    @ModifyExpressionValue(method = "applyEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private static List<Player> toggleFlight(List<Player> original) {
        for (Player player : original) {
            if (PlayerHelper.findInStack(player, ModItems.MAGIC_FEATHER.get())) {
                //How can i do this without adding effects?
                if(!player.mayFly()) Objects.requireNonNull(player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT.value())).setBaseValue(1);
            }else{
                Objects.requireNonNull(player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT.value())).setBaseValue(0);
            }

        }
        return original;
    }



}
