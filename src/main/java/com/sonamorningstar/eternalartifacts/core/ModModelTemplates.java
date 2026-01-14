package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModModelTemplates {

    public static final ModelTemplate BASE_DRUM = create("base_drum");
    public static final ModelTemplate CUBE4X4 = create("cube4");
    public static final ModelTemplate CUBE10X10 = create("cube10");
    public static final ModelTemplate COVERED_CABLE_INSIDE = create("covered_cable_inside", "_inside", TextureSlot.TEXTURE);
    public static final ModelTemplate COVERED_CABLE_SIDE = create("covered_cable_side", "_side", TextureSlot.TEXTURE);
    public static final ModelTemplate COVERED_CABLE_INVENTORY = create("covered_cable_inventory", "_inventory", TextureSlot.TEXTURE);
    public static final ModelTemplate CABLE_INSIDE = create("cable_inside", "_inside", TextureSlot.TEXTURE);
    public static final ModelTemplate CABLE_SIDE = create("cable_side", "_side", TextureSlot.TEXTURE);
    public static final ModelTemplate CABLE_INVENTORY = create("cable_inventory", "_inventory", TextureSlot.TEXTURE);
    public static final ModelTemplate PIPE_INSIDE = create("pipe_inside", "_inside", TextureSlot.TEXTURE);
    public static final ModelTemplate PIPE_SIDE = create("pipe_side", "_side", TextureSlot.TEXTURE);
    public static final ModelTemplate PIPE_SIDE_EXTRACTING = create("pipe_side_extracting", "_side_extracting", TextureSlot.TEXTURE);
    public static final ModelTemplate PIPE_SIDE_FILTERING = create("pipe_side_filtering", "_side_filtering", TextureSlot.TEXTURE);
    public static final ModelTemplate PIPE_INVENTORY = create("pipe_inventory", "_inventory", TextureSlot.TEXTURE);
    public static final ModelTemplate MOD_CAULDON_1_OF_4 = create("mod_cauldon_1_of_4", "_1_of_4", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate MOD_CAULDON_2_OF_4 = create("mod_cauldon_2_of_4", "_2_of_4", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate MOD_CAULDON_3_OF_4 = create("mod_cauldon_3_of_4", "_3_of_4", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate MOD_CAULDON_4_OF_4 = create("mod_cauldon_4_of_4", "_4_of_4", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);

    public static final ModelTemplate ENTITY_RENDER_TRANSFORMS = createItem("entity_renderer_transform");
    public static final ModelTemplate CUBE_BOTTOM_TOP_BEWLR = createItem("cube_bottom_top_bewlr");
    public static final ModelTemplate SPELL_TOME = createItem("spell_tome", TextureSlot.TEXTURE);
    
    public static final ModelTemplate[] MOD_CAULDRON_4 = new ModelTemplate[] {
            MOD_CAULDON_1_OF_4,
            MOD_CAULDON_2_OF_4,
            MOD_CAULDON_3_OF_4,
            MOD_CAULDON_4_OF_4
    };
    
    private static ModelTemplate create(String blockLoc, TextureSlot... slots) {
        return new ModelTemplate(Optional.of(new ResourceLocation(MODID, "block/" + blockLoc)), Optional.empty(), slots);
    }
    private static ModelTemplate create(String blockLoc, String suffix, TextureSlot... slots) {
        return new ModelTemplate(Optional.of(new ResourceLocation(MODID, "block/" + blockLoc)), Optional.of(suffix), slots);
    }
    private static ModelTemplate createItem(String itemLoc, TextureSlot... slots) {
        return new ModelTemplate(Optional.of(new ResourceLocation(MODID, "item/" + itemLoc)), Optional.empty(), slots);
    }

}