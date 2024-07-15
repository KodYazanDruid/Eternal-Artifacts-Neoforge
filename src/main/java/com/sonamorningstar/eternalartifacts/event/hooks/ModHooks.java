package com.sonamorningstar.eternalartifacts.event.hooks;

import com.sonamorningstar.eternalartifacts.compat.mekanism.MekanismCompat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Predicate;

public final class ModHooks {

    static ModList modList = ModList.get();
    static Predicate<String> check = modList == null ? id -> false : modList::isLoaded;

    public static final String MEKANISM_ID = "mekanism";
    public static final String MEKANISM_GENERATORS_ID = "mekanismgenerators";

    public final boolean mekanismLoaded;
    public final boolean mekanismGeneratorsLoaded;

    public ModHooks() {
        mekanismLoaded = check.test(MEKANISM_ID);
        mekanismGeneratorsLoaded = check.test(MEKANISM_GENERATORS_ID);
    }

    public void construct(final IEventBus modEventBus) {
        if (mekanismLoaded) {
            NeoForge.EVENT_BUS.addListener(MekanismCompat::drinkEventMekanism);
        }
        if(mekanismGeneratorsLoaded) {
            NeoForge.EVENT_BUS.addListener(MekanismCompat::drinkEventMekanismGenerators);
        }
    }
}
