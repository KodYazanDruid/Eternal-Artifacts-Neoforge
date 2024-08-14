package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModModelTemplates {

    public static ModelTemplate BASE_DRUM = create("base_drum");
    public static ModelTemplate CUBE4X4 = create("cube4");
    public static ModelTemplate CUBE10X10 = create("cube10");

    public static ModelTemplate ENTITY_RENDER_TRANSFORMS = createItem("entity_renderer_transform");

    private static ModelTemplate create(String blockLoc, TextureSlot... slots) {
        return new ModelTemplate(Optional.of(new ResourceLocation(MODID, "block/" + blockLoc)), Optional.empty(), slots);
    }
    private static ModelTemplate createItem(String itemLoc, TextureSlot... slots) {
        return new ModelTemplate(Optional.of(new ResourceLocation(MODID, "item/" + itemLoc)), Optional.empty(), slots);
    }
}
