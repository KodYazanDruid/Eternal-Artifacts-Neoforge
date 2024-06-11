package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public enum ModConstants {
    TRANSLATE_KEY_PREFIX("key."+MODID+".tooltip"),
    TRANSLATE_BUTTON_PREFIX("key."+MODID+".button"),
    GUI("gui."+MODID),
    WARPS("warps."+MODID),
    CHLOROPHYTE_UPGRADE_APPLIES_TO(Util.makeDescriptionId("item", new ResourceLocation(MODID, "smithing_template.chlorophyte_upgrade.applies_to"))),
    CHLOROPHYTE_UPGRADE_INGREDIENTS(Util.makeDescriptionId("item", new ResourceLocation(MODID, "smithing_template.chlorophyte_upgrade.ingredients"))),
    CHLOROPHYTE_UPGRADE_BASE_SLOT_DESCRIPTION(Util.makeDescriptionId("item", new ResourceLocation(MODID, "smithing_template.chlorophyte_upgrade.base_slot_description"))),
    CHLOROPHYTE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION(Util.makeDescriptionId("item", new ResourceLocation(MODID,"smithing_template.chlorophyte_upgrade.additions_slot_description")));

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

    public MutableComponent withSuffixTranslatable(String suffix) {
        return Component.translatable(withSuffix(suffix));
    }

    public MutableComponent translatable() {
        return Component.translatable(string);
    }


    public static String withId(String text) {
        return MODID + ":" + text;
    }

}
