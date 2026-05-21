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
    
    public static TextureMapping hopper(Block hopper) {
        return new TextureMapping()
            .put(TextureSlot.TOP, getBlockTexture(hopper, "_top"))
            .put(TextureSlot.SIDE, getBlockTexture(hopper, "_outside"))
            .put(TextureSlot.INSIDE, getBlockTexture(hopper, "_inside"))
            .put(TextureSlot.PARTICLE, getBlockTexture(hopper, "_outside"));
    }
    
    public static ResourceLocation getBlockTexture(Block block, String textureSuffix) {
        ResourceLocation resourcelocation = BuiltInRegistries.BLOCK.getKey(block);
        return resourcelocation.withPath(p_248521_ -> "block/" + p_248521_ + textureSuffix);
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
