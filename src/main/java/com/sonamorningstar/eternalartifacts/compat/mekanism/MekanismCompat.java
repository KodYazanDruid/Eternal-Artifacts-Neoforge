package com.sonamorningstar.eternalartifacts.compat.mekanism;

import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import mekanism.common.tags.MekanismTags;
import mekanism.generators.common.GeneratorTags;

public class MekanismCompat {

    public static void drinkEventMekanism(JarDrinkEvent event) {
        FluidStack fluidStack = event.getFluidStack();
        if(fluidStack.is(MekanismTags.Fluids.URANIUM_OXIDE)) {
            event.setDefaultUseTime();
            event.setAfterDrink((player, itemStack) -> {
                MobEffectInstance effect = new MobEffectInstance(MobEffects.POISON, 600, 4);
                player.addEffect(effect);
            });
        }
    }

    public static void drinkEventMekanismGenerators(JarDrinkEvent event) {
        FluidStack fluidStack = event.getFluidStack();
        if (fluidStack.is(GeneratorTags.Fluids.FUSION_FUEL)) {
            event.setDefaultUseTime();
            event.setAfterDrink((player, itemStack) -> {
                player.level().explode(null, player.getX(), player.getY(), player.getZ(), 10.0F, true, Level.ExplosionInteraction.BLOCK);
            });
        }
    }
}
