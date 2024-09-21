package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;

public class ModTextureMappings extends TextureMapping{

    public static TextureMapping cable(Block cable) {
        return new TextureMapping()
            .put(TextureSlot.TEXTURE, getBlockTexture(cable))
            .put(TextureSlot.PARTICLE, getBlockTexture(cable));
    }
}
