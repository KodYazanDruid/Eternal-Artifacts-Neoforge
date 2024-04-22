package com.sonamorningstar.eternalartifacts.client.model.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelHelper {
    private static final Map<Block, ResourceLocation> TEXTURE_NAME_CACHE = new ConcurrentHashMap<>();
    public static final ResourceManagerReloadListener LISTENER = manager -> TEXTURE_NAME_CACHE.clear();

    @Nullable
    public static <T extends BakedModel> T getBakedModel(BlockState state, Class<T> tClass) {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft == null) return null;

        BakedModel baked = minecraft.getModelManager().getBlockModelShaper().getBlockModel(state);
        if(baked instanceof MultiPartBakedModel) baked = ((MultiPartBakedModel)baked).selectors.get(0).getRight();
        if(baked instanceof WeightedBakedModel) baked = ((WeightedBakedModel)baked).wrapped;
        if(tClass.isInstance(baked)) return tClass.cast(baked);
        return null;
    }

    @Nullable
    public static <T extends BakedModel> T getBakedModel(ItemLike item, Class<T> tClass) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return null;
        BakedModel baked = minecraft.getItemRenderer().getItemModelShaper().getItemModel(item.asItem());
        if (tClass.isInstance(baked)) return tClass.cast(baked);
        return null;
    }

    public static ResourceLocation getParticleTextureInternal(Block block) {
        TextureAtlasSprite particle = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(block.defaultBlockState()).getParticleIcon();
        if(particle != null) return particle.contents().name();
        return MissingTextureAtlasSprite.getLocation();
    }

    public static ResourceLocation getParticleTexture(Block block) {
        return TEXTURE_NAME_CACHE.computeIfAbsent(block, ModelHelper::getParticleTextureInternal);
    }

    public static <T> T arrayToObject(JsonObject json, String name, int size, Function<float[], T> mapper) {
        JsonArray array = GsonHelper.getAsJsonArray(json, name);
        if (array.size() != size) throw new JsonParseException("Expected " + size + " " + name + " values, found: " + array.size());

        float[] vec = new float[size];
        for(int i = 0; i < size; ++i) {
            vec[i] = GsonHelper.convertToFloat(array.get(i), name + "[" + i + "]");
        }
        return mapper.apply(vec);
    }

    public static Vector3f arrayToVector(JsonObject json, String name) {
        return arrayToObject(json, name, 3, arr -> new Vector3f(arr[0], arr[1], arr[2]));
    }

    public static int getRotation(JsonObject json, String key) {
        int i = GsonHelper.getAsInt(json, key, 0);
        if (i >= 0 && i % 90 == 0 && i / 90 <= 3) return i;
        else throw new JsonParseException("Invalid '" + key + "' " + i + " found, only 0/90/180/270 allowed");

    }
}
