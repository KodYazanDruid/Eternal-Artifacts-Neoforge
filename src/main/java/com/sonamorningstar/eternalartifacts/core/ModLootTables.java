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
