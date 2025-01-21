package com.sonamorningstar.eternalartifacts.compat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.compat.appleskin.AppleSkinCompat;
import com.sonamorningstar.eternalartifacts.compat.emi.EmiCompat;
import com.sonamorningstar.eternalartifacts.compat.mekanism.MekanismCompat;
import com.sonamorningstar.eternalartifacts.compat.pneumaticcraft.PneumaticCraftCompat;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ModHooks {

    private static final ModList modList = ModList.get();
    private static final Predicate<String> check = modList == null ? id -> false : modList::isLoaded;

    public static final String MEKANISM_ID = "mekanism";
    public static final String MEKANISM_GENERATORS_ID = "mekanismgenerators";
    public static final String PNEUMATICCRAFT_ID = "pneumaticcraft";
    public static final String APPLESKIN_ID = "appleskin";
    public static final String EMI_ID = "emi";

    public final boolean mekanismLoaded;
    public final boolean mekanismGeneratorsLoaded;
    public final boolean pneumaticcraftLoaded;
    public final boolean appleskinLoaded;
    public final boolean emiLoaded;

    public ModHooks() {
        mekanismLoaded = check.test(MEKANISM_ID);
        mekanismGeneratorsLoaded = check.test(MEKANISM_GENERATORS_ID);
        pneumaticcraftLoaded = check.test(PNEUMATICCRAFT_ID);
        appleskinLoaded = check.test(APPLESKIN_ID);
        emiLoaded = check.test(EMI_ID);
    }

    public void construct(final IEventBus modEventBus) {
        if (mekanismLoaded) {
            NeoForge.EVENT_BUS.addListener(MekanismCompat::drinkEventMekanism);
            MekanismCompat.runMek(modEventBus);
        }
        if (mekanismGeneratorsLoaded) {
            NeoForge.EVENT_BUS.addListener(MekanismCompat::drinkEventMekanismGenerators);
            MekanismCompat.runMekGens(modEventBus);
        }
        if (pneumaticcraftLoaded) {
            modEventBus.addListener(PneumaticCraftCompat::registerHeat);
            PneumaticCraftCompat.run(modEventBus);
        }
        if (appleskinLoaded) {
            NeoForge.EVENT_BUS.addListener(AppleSkinCompat::registerFoodValues);
            AppleSkinCompat.run(modEventBus);
        }
        if (emiLoaded) {
            EmiCompat.runData();
        }
    }

    public static class ItemTagAppender
    {
        public static final Map<TagKey<Item>, List<Supplier<Item>>> itemTags = new ConcurrentHashMap<>();
        public static final Map<TagKey<Item>, List<TagKey<Item>>> tagKeyTags = new ConcurrentHashMap<>();

        @SafeVarargs
        public static void appendTag(TagKey<Item> tagKey, Supplier<Item>... item) {
            itemTags.put(tagKey, List.of(item));
        }
        @SafeVarargs
        public static void appendTagKey(TagKey<Item> tagKey, TagKey<Item>... appended) {
            tagKeyTags.put(tagKey, List.of(appended));
        }
    }
    
    public static class LanguageProvider {
        public static final Multimap<String, Pair<String, String>> langMap = HashMultimap.create();
        
        public static void appendLang(String loc, String key, String value) {
            langMap.put(loc, Pair.of(key, value));
        }
    }
}
