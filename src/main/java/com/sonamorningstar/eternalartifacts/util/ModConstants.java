package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public enum ModConstants {
   TRANSLATE_KEY_PREFIX("key."+MODID+".tooltip"),
   GUI("gui."+MODID);

   String string;
    ModConstants(String string) {
        this.string = string;
    }

    public String getString(){
        return string;
    }

    public String withSuffix(String suffix) {
        return string+"."+suffix;
    }

    public String ofItem(DeferredItem<? extends Item> item) {
        String path = item.getId().getPath();
        return withSuffix(path);
    }

    public static String withId(String text) {
        return MODID + ":" + text;
    }
}
