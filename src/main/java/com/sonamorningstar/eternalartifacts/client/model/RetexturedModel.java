package com.sonamorningstar.eternalartifacts.client.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.client.model.util.DynamicBakedWrapper;
import com.sonamorningstar.eternalartifacts.client.model.util.GeometryContextWrapper;
import com.sonamorningstar.eternalartifacts.client.model.util.ModelHelper;
import com.sonamorningstar.eternalartifacts.client.model.util.ModelTextureIteratable;
import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

//Custom model loader.
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RetexturedModel implements IUnbakedGeometry<RetexturedModel> {
    public static IGeometryLoader<RetexturedModel> LOADER = RetexturedModel::deserialize;

    private final SimpleBlockModel model;
    private final Set<String> retextured;

    public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
        return model.getMaterials(owner, modelGetter, missingTextureErrors);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
        BakedModel baked = model.bake(owner, baker, spriteGetter, transform, overrides, location);
        return new Baked(baked, owner, model, transform, getAllRetextured(owner, this.model, retextured));
    }

    public static Set<String> getAllRetextured(IGeometryBakingContext owner, SimpleBlockModel model, Set<String> originalSet) {
        Set<String> retextured = Sets.newHashSet(originalSet);
        for(Map<String, Either<Material, String>> textures : ModelTextureIteratable.of(owner, model)) {
            textures.forEach((name, either) ->
                either.ifRight(parent -> {
                    if(retextured.contains(parent)) retextured.add(name);
                })
            );
        }
        return ImmutableSet.copyOf(retextured);
    }

    public static RetexturedModel deserialize(JsonObject json, JsonDeserializationContext context) {
        ColoredBlockModel model = ColoredBlockModel.deserialize(json, context);
        Set<String> retextured = getRetexturedNames(json);

        return new RetexturedModel(model, retextured);
    }

    public static Set<String> getRetexturedNames(JsonObject json) {
        if(json.has("retextured")) {
            JsonElement retextured = json.get("retextured");
            if(retextured.isJsonArray()) {
                JsonArray array = retextured.getAsJsonArray();
                if(array.size() == 0) throw new JsonSyntaxException("Must have atleast one texture in the retextured");
                ImmutableSet.Builder<String> builder = ImmutableSet.builder();

                for(int i = 0; i < array.size(); i++) {
                    builder.add(GsonHelper.convertToString(array.get(i), "retextured[" + i + "]"));
                }
                return builder.build();
            }
            if(retextured.isJsonPrimitive()) return ImmutableSet.of(retextured.getAsString());
        }
        throw new JsonSyntaxException("Missing retextured, expected to find a String or a JsonArray");
    }

    public static class Baked extends DynamicBakedWrapper<BakedModel> {
        private final Map<ResourceLocation, BakedModel> cache = new ConcurrentHashMap<>();
        private final IGeometryBakingContext owner;
        private final SimpleBlockModel model;
        private final ModelState transform;
        private final Set<String> retextured;

        protected Baked(BakedModel baked, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, Set<String> retextured) {
            super(baked);
            this.owner = owner;
            this.model = model;
            this.transform = transform;
            this.retextured = retextured;
        }

        private BakedModel getRetexturedModel(ResourceLocation name) {
            return model.bakeDynamic(new RetexturedContent(owner, retextured, name), transform);
        }

        private BakedModel getCachedModel(Block block) {
            return cache.computeIfAbsent(ModelHelper.getParticleTexture(block), this::getRetexturedModel);
        }

        @Override
        public TextureAtlasSprite getParticleIcon(ModelData data) {
            if(retextured.contains("particle")) {
                Block block = data.get(RetexturedHelper.PROPERTY);
                if(block != null) return getCachedModel(block).getParticleIcon(data);
            }
            return originalModel.getParticleIcon(data);
        }

        @NotNull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
            Block block = data.get(RetexturedHelper.PROPERTY);
            if(block == null) return originalModel.getQuads(state, side, rand, data, null);
            return getCachedModel(block).getQuads(state, side, rand, data, null);
        }

        @Override
        public ItemOverrides getOverrides() {
            return RetexturedOverride.INSTANCE;
        }
    }

    private static class RetexturedContent extends GeometryContextWrapper {
        private final Set<String> retextured;
        private final Material texture;

        public RetexturedContent(IGeometryBakingContext base, Set<String> retextured, ResourceLocation texture) {
            super(base);
            this.retextured = retextured;
            this.texture = new Material(InventoryMenu.BLOCK_ATLAS, texture);
        }

        @Override
        public boolean hasMaterial(String name) {
            return retextured.contains(name) ? !MissingTextureAtlasSprite.getLocation().equals(texture.texture()) : super.hasMaterial(name);
        }

        @Override
        public Material getMaterial(String name) {
            return retextured.contains(name) ? texture : super.getMaterial(name);
        }
    }

    private static class RetexturedOverride extends ItemOverrides {
        private static final RetexturedOverride INSTANCE = new RetexturedOverride();

        @Nullable
        @Override
        public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
            if(pStack.isEmpty() || !pStack.hasTag()) return pModel;

            Block block = RetexturedBlockItem.getTexture(pStack);
            if(block == Blocks.AIR) return pModel;

            return ((Baked) pModel).getCachedModel(block);
        }
    }
}
