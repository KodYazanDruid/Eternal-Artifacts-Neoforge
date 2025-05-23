package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModModelLayers {
    public static final ModelLayerLocation DEMON_EYE_LAYER = create("demon_eye_layer");
    public static final ModelLayerLocation DUCK_LAYER = create("duck_layer");
    //public static final ModelLayerLocation FANCY_CHEST_LAYER = new ModelLayerLocation(new ResourceLocation(MODID, "fancy_chest_layer"), "main");
    public static final ModelLayerLocation JAR_LAYER = create("jar_layer");
    public static final ModelLayerLocation DYNAMO_LAYER = create("dynamo_layer");
    public static final ModelLayerLocation NOUS_TANK_LAYER = create("nous_tank_layer");
    public static final ModelLayerLocation OIL_REFINERY_LAYER = create("oil_refinery_layer");
    public static final ModelLayerLocation TORNADO_LAYER = create("tornado_layer");
    public static final ModelLayerLocation METEORITE_LAYER = create("meteorite_layer");
    public static final ModelLayerLocation SPELL_TOME_LAYER = create("spell_tome_layer");
    public static final ModelLayerLocation CHARGED_SHEEP_SWIRL = create("charged_sheep_swirl", "swirl");
    public static final ModelLayerLocation ENERGY_DOCK_LAYER = create("energy_dock_layer");
    public static final ModelLayerLocation MISSILE_LAYER = create("missile_layer");
    public static final ModelLayerLocation PORTABLE_BATTERY_LAYER = create("portable_battery_layer");
    public static final ModelLayerLocation BUCKET_LAYER = create("bucket_layer");
    public static final ModelLayerLocation DROWNED_HEAD = create("drowned_head");
    public static final ModelLayerLocation DROWNED_HEAD_OVERLAY = create("drowned_head_overlay");
    public static final ModelLayerLocation HUSK_HEAD = create("husk_head");
    public static final ModelLayerLocation STRAY_SKULL = create("stray_skull");
    public static final ModelLayerLocation STRAY_SKULL_OVERLAY = create("stray_skull_overlay");
    public static final ModelLayerLocation BLAZE_HEAD = create("blaze_head");

    private static ModelLayerLocation create(String name) {
        return create(name, "main");
    }
    private static ModelLayerLocation create(String name, String sub) {
        return new ModelLayerLocation(new ResourceLocation(MODID, name), sub);
    }
}
