package com.sonamorningstar.eternalartifacts.client.resources.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.sonamorningstar.eternalartifacts.client.resources.model.data.CableModelData;
import com.sonamorningstar.eternalartifacts.client.resources.model.util.GeometryContextWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CableModel implements IUnbakedGeometry<CableModel> {
    public static IGeometryLoader<CableModel> LOADER = CableModel::deserialize;

    public CableModel() {
    }

    private static CableModel deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {

        return new CableModel();
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        //return new CableBakedModel(baker.bake(modelLocation, modelState, spriteGetter));
        return null;
    }

    public static class CableBakedModel implements IDynamicBakedModel {

        private final CableContent context;
        private final TextureAtlasSprite particle;
        private final Map<Direction, List<BakedQuad>> centerModel;
        private final Map<Direction, List<BakedQuad>> centerFill;
        private final Map<Direction, List<BakedQuad>> sides;
        private final Map<Direction, List<BakedQuad>> fill;
        private final Map<Direction, List<BakedQuad>> connections;
        private final boolean isInventory;
        private final Map<CableModelData, List<BakedQuad>> modelCache = new HashMap<>();
        private final Map<ColoredBlockModel.ColorData, Map<Direction, List<BakedQuad>>> centerFillCache = new Object2ObjectOpenHashMap<>();
        private final Map<ColoredBlockModel.ColorData, Map<Direction, List<BakedQuad>>> fillCache = new Object2ObjectOpenHashMap<>();
        private final Map<ResourceLocation, Map<Direction, List<BakedQuad>>> attachmentCache = new Object2ObjectOpenHashMap<>();

        public CableBakedModel(CableContent context, TextureAtlasSprite particle, EnumMap<Direction, List<BakedQuad>> centerModel, EnumMap<Direction, List<BakedQuad>> centerFill, EnumMap<Direction, List<BakedQuad>> sides, EnumMap<Direction, List<BakedQuad>> fill, EnumMap<Direction, List<BakedQuad>> connections, boolean isInventory) {
            this.context = context;
            this.particle = particle;
            this.centerModel = ImmutableMap.copyOf(centerModel);
            this.centerFill = ImmutableMap.copyOf(centerFill);
            this.sides = ImmutableMap.copyOf(sides);
            this.fill = ImmutableMap.copyOf(fill);
            this.connections = ImmutableMap.copyOf(connections);
            this.isInventory = isInventory;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
            return List.of();
        }


        @Override public boolean useAmbientOcclusion() { return context.useAmbientOcclusion(); }
        @Override public boolean isGui3d() { return context.isGui3d(); }
        @Override public boolean usesBlockLight() { return context.useBlockLight(); }
        @Override public ItemTransforms getTransforms() { return context.getTransforms(); }
        @Override public boolean isCustomRenderer() { return false; }
        @Override public TextureAtlasSprite getParticleIcon() { return particle; }
        @Override public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
    }

    public static class CableContent extends GeometryContextWrapper {

        public CableContent(IGeometryBakingContext base) {
            super(base);
        }
    }
}
