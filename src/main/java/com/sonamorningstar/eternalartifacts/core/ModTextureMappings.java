package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ModTextureMappings extends TextureMapping{

    public static TextureMapping pipe(Block pipe) {
        return new TextureMapping()
            .put(TextureSlot.TEXTURE, getBlockTexture(pipe))
            .put(TextureSlot.PARTICLE, getBlockTexture(pipe));
    }
    
    public static TextureMapping cubeTopBottom(Block block) {
        return new TextureMapping()
            .put(TextureSlot.PARTICLE, getSideTexture(block))
            .put(TextureSlot.TOP, getTopTexture(block))
            .put(TextureSlot.BOTTOM, getBottomTexture(block))
            .put(TextureSlot.SIDE, getSideTexture(block));
    }

    public static TextureMapping inventoryWall(Block wall) {
        return new TextureMapping()
                .put(TextureSlot.WALL, getBlockTexture(wall));
    }
    
    private static ResourceLocation getTopTexture(Block block) {
        String namespace = BuiltInRegistries.BLOCK.getKey(block).getNamespace();
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        return new ResourceLocation(namespace, "block/" + path + "_top");
    }
    
    private static ResourceLocation getSideTexture(Block block) {
        String namespace = BuiltInRegistries.BLOCK.getKey(block).getNamespace();
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        return new ResourceLocation(namespace, "block/" + path + "_side");
    }
    
    private static ResourceLocation getBottomTexture(Block block) {
        String namespace = BuiltInRegistries.BLOCK.getKey(block).getNamespace();
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        return new ResourceLocation(namespace, "block/" + path + "_bottom");
    }
    
}
