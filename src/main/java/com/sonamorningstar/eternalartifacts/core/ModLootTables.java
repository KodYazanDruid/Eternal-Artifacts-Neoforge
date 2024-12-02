package com.sonamorningstar.eternalartifacts.core;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModLootTables {

    @Getter
    private static final Set<ResourceLocation> MOD_LOOTTABLES = new HashSet<>();

    public static final ResourceLocation COPPER_OREBERRY_HARVEST = registerOreBerries("copper");
    public static final ResourceLocation IRON_OREBERRY_HARVEST = registerOreBerries("iron");
    public static final ResourceLocation GOLD_OREBERRY_HARVEST = registerOreBerries("gold");
    public static final ResourceLocation EXPERIENCE_OREBERRY_HARVEST = registerOreBerries("experience");
    public static final ResourceLocation MANGANESE_OREBERRY_HARVEST = registerOreBerries("manganese");
    public static final ResourceLocation CHARGED_SHEEP_WHITE = register("entities/charged_sheep/white");
    public static final ResourceLocation CHARGED_SHEEP_ORANGE = register("entities/charged_sheep/orange");
    public static final ResourceLocation CHARGED_SHEEP_MAGENTA = register("entities/charged_sheep/magenta");
    public static final ResourceLocation CHARGED_SHEEP_LIGHT_BLUE = register("entities/charged_sheep/light_blue");
    public static final ResourceLocation CHARGED_SHEEP_YELLOW = register("entities/charged_sheep/yellow");
    public static final ResourceLocation CHARGED_SHEEP_LIME = register("entities/charged_sheep/lime");
    public static final ResourceLocation CHARGED_SHEEP_PINK = register("entities/charged_sheep/pink");
    public static final ResourceLocation CHARGED_SHEEP_GRAY = register("entities/charged_sheep/gray");
    public static final ResourceLocation CHARGED_SHEEP_LIGHT_GRAY = register("entities/charged_sheep/light_gray");
    public static final ResourceLocation CHARGED_SHEEP_CYAN = register("entities/charged_sheep/cyan");
    public static final ResourceLocation CHARGED_SHEEP_PURPLE = register("entities/charged_sheep/purple");
    public static final ResourceLocation CHARGED_SHEEP_BLUE = register("entities/charged_sheep/blue");
    public static final ResourceLocation CHARGED_SHEEP_BROWN = register("entities/charged_sheep/brown");
    public static final ResourceLocation CHARGED_SHEEP_GREEN = register("entities/charged_sheep/green");
    public static final ResourceLocation CHARGED_SHEEP_RED = register("entities/charged_sheep/red");
    public static final ResourceLocation CHARGED_SHEEP_BLACK = register("entities/charged_sheep/black");

    public static final ResourceLocation SURVIVALISTS_IGLOO = register("chests/survivalists_igloo");

    private static ResourceLocation registerOreBerries(String material) {
        return register("oreberries/"+material);
    }

    private static ResourceLocation register(String id) {
        return register(new ResourceLocation(MODID, id));
    }

    private static ResourceLocation register(ResourceLocation id) {
        if (MOD_LOOTTABLES.add(id)) {
            return id;
        } else {
            throw new IllegalArgumentException(id + " is already a registered built-in loot table");
        }
    }

}
