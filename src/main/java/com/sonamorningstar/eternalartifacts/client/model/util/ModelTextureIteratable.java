package com.sonamorningstar.eternalartifacts.client.model.util;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.client.model.SimpleBlockModel;
import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import net.neoforged.neoforge.client.model.geometry.BlockGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

@AllArgsConstructor
public class ModelTextureIteratable implements Iterable<Map<String, Either<Material, String>>> {
    @Nullable
    private final Map<String, Either<Material, String>> startMap;
    @Nullable
    private final BlockModel startModel;

    public ModelTextureIteratable(BlockModel model) {
        this(null, model);
    }

    public static ModelTextureIteratable of(IGeometryBakingContext owner, SimpleBlockModel fallback) {
        if(owner instanceof BlockGeometryBakingContext blockOwner) return new ModelTextureIteratable(null, blockOwner.owner);
        return new ModelTextureIteratable(fallback.getTextures(), fallback.getParent());
    }

    @Override
    public MapIterator iterator() {
        return new MapIterator(startMap, startModel);
    }

    @AllArgsConstructor
    private static class MapIterator implements Iterator<Map<String, Either<Material, String>>> {
        @Nullable
        private Map<String, Either<Material, String>> initial;
        @Nullable
        private BlockModel model;

        @Override
        public boolean hasNext() {
            return initial != null || model != null;
        }

        @Override
        public Map<String, Either<Material, String>> next() {
            Map<String, Either<Material, String>> map;
            if(initial != null){
                map = initial;
                initial = null;
            }else if(model != null) {
                map = model.textureMap;
                model = model.parent;
            }else throw new NoSuchElementException();
            return map;
        }
    }
}
