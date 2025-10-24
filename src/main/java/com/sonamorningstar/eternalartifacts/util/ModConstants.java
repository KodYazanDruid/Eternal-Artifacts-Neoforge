package com.sonamorningstar.eternalartifacts.util;

import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Getter
public enum ModConstants {
    TRANSLATE_KEY_PREFIX("key."+MODID+".tooltip"),
    TRANSLATE_BUTTON_PREFIX("key."+MODID+".button"),
    GUI("gui."+MODID),
    TITLE("title."+MODID),
    TOOLTIP("tooltip."+MODID),
    OVERLAY("overlay."+MODID),
    WARPS("warps."+MODID),
    CHLOROPHYTE_UPGRADE_APPLIES_TO(Util.makeDescriptionId("item", new ResourceLocation(MODID, "smithing_template.chlorophyte_upgrade.applies_to"))),
    CHLOROPHYTE_UPGRADE_INGREDIENTS(Util.makeDescriptionId("item", new ResourceLocation(MODID, "smithing_template.chlorophyte_upgrade.ingredients"))),
    CHLOROPHYTE_UPGRADE_BASE_SLOT_DESCRIPTION(Util.makeDescriptionId("item", new ResourceLocation(MODID, "smithing_template.chlorophyte_upgrade.base_slot_description"))),
    CHLOROPHYTE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION(Util.makeDescriptionId("item", new ResourceLocation(MODID,"smithing_template.chlorophyte_upgrade.additions_slot_description"))),
    INVENTORY_TAB("inventory_tab."+MODID),
    CHARM_TYPE("charm_type."+MODID),
    CHARM_SLOT_MODIFIER("charm_slot."+MODID+".modifier"),
    COMMAND("command."+MODID),
    FILLED_MAP("filled_map."+MODID),
    DROPDOWN_MENU("widget."+MODID, "dropdown_menu"),
    SCROLLABLE_PANEL("widget."+MODID, "scrollable_panel"),
    SCROLLABLE_PANEL_COMPONENT("widget."+MODID, "scrollable_panel_component"),
    NETWORK_COMPONENT("widget."+MODID, "network_component"),
    ITEM_CAPABILITY("capability."+MODID, "item"),
    FLUID_CAPABILITY("capability."+MODID, "fluid"),
    ENERGY_CAPABILITY("capability."+MODID, "energy"),
    GAS_CAPABILITY("capability."+MODID, "gas"),
    BLOCK("block."+MODID),
    FILTER("filter."+MODID),
    WIDGET("widget."+MODID);
    
    final String string;
    @Nullable
    String subType;
    ModConstants(String string) {
        this.string = string;
    }
    
    ModConstants(String string, String subType) {
        this.string = string;
        this.subType = subType;
    }
    
    @Override
    public String toString() {
        return string + (subType != null ? "."+subType : "");
    }
    
    public String withSuffix(String suffix) {
        if (subType != null) return string+"."+subType+"."+suffix;
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
        if (subType != null) return Component.translatable(string+"."+subType);
        return Component.translatable(string);
    }


    public static String withId(String text) {
        return MODID + ":" + text;
    }

}
