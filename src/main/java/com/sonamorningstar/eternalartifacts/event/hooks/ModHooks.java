package com.sonamorningstar.eternalartifacts.event.hooks;

import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import lombok.NoArgsConstructor;
import mekanism.common.tags.MekanismTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Predicate;

@NoArgsConstructor
public final class ModHooks {

    static ModList modList = ModList.get();
    static Predicate<String> check = modList == null ? id -> false : modList::isLoaded;

    public static final String MEKANISM_ID = "mekanism";

    public static boolean mekanismLoaded = check.test(MEKANISM_ID);

    public static void hookJarDrinkEvent(JarDrinkEvent event) {
        FluidStack fluidStack = event.getFluidStack();
        if (mekanismLoaded) {
            if(fluidStack.is(MekanismTags.Fluids.URANIUM_OXIDE)) {
                event.setDefaultUseTime();
                event.setAfterDrink((player, itemStack) -> {
                    MobEffectInstance effect = new MobEffectInstance(MobEffects.POISON, 600, 4);
                    player.addEffect(effect);
                });
            }
        }
    }
}
