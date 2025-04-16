package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;

public class ModTextureMappings extends TextureMapping{

    public static TextureMapping pipe(Block pipe) {
        return new TextureMapping()
            .put(TextureSlot.TEXTURE, getBlockTexture(pipe))
            .put(TextureSlot.PARTICLE, getBlockTexture(pipe));
    }

    public static TextureMapping inventoryWall(Block wall) {
        return new TextureMapping()
                .put(TextureSlot.WALL, getBlockTexture(wall));
    }
}
