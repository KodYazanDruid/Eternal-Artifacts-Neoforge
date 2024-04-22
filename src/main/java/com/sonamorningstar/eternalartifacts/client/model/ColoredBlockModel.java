package com.sonamorningstar.eternalartifacts.client.model;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.util.JsonHelper;
import com.sonamorningstar.eternalartifacts.util.LogicHelper;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.resources.model.SimpleBakedModel.Builder;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.renderer.block.model.BlockModel.FACE_BAKERY;

public class ColoredBlockModel extends SimpleBlockModel{
    public static final IGeometryLoader<SimpleBlockModel> LOADER = ColoredBlockModel::deserialize;

    private final List<ColorData> colorData;

    public ColoredBlockModel(@Nullable ResourceLocation parentLocation, Map<String, Either<Material, String>> textures, List<BlockElement> parts, List<ColorData> colorData) {
        super(parentLocation, textures, parts);
        this.colorData = colorData;
    }

    public ColoredBlockModel(SimpleBlockModel base, List<ColorData> colorData) {
        super(base);
        this.colorData = colorData;
    }

    public static void bakePart(Builder builder, IGeometryBakingContext owner, BlockElement part, int emissivity, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transform, IQuadTransformer quadTransformer, boolean uvlock, ResourceLocation location) {
        for(Direction direction : part.faces.keySet()) {
            BlockElementFace face = part.faces.get(direction);
            String texture = face.texture;
            if(texture.charAt(0) == '#') texture = texture.substring(1);
            TextureAtlasSprite sprite = spriteGetter.apply(owner.getMaterial(texture));
            BakedQuad quad = bakeFace(part, face, sprite, direction, transform, uvlock, emissivity, location);
            quadTransformer.processInPlace(quad);
            if(face.cullForDirection == null) builder.addUnculledFace(quad);
            else builder.addCulledFace(Direction.rotate(transform.getMatrix(), face.cullForDirection), quad);
        }
    }

    public static BakedModel bakeModel(IGeometryBakingContext owner, List<BlockElement> elements, List<ColorData> colorData, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
        TextureAtlasSprite particle = spriteGetter.apply(owner.getMaterial("particle"));
        SimpleBakedModel.Builder builder = bakedBuilder(owner, overrides).particle(particle);
        int size = elements.size();
        IQuadTransformer quadTransformer = applyTransform(transform, owner.getRootTransform());
        Transformation transformation = transform.getRotation();
        boolean uvlock = transform.isUvLocked();
        for(int i = 0; i < size; i++) {
            BlockElement part = elements.get(i);
            ColorData colors = LogicHelper.getOrDefault(colorData, i, ColorData.DEFAULT);
            if(colors.luminosity != -1 && !location.equals(BAKE_LOCATION)) EternalArtifacts.LOGGER.warn("Deprecated");

            IQuadTransformer partTransformer = colors.color == -1 ? quadTransformer : quadTransformer.andThen(applyColorQuadTransformer(colors.color));
            bakePart(builder, owner, part, colors.luminosity, spriteGetter, transformation, partTransformer, colors.isUvlock(uvlock), location);
        }
        return builder.build(getRenderTypeGroup(owner));
    }

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
        return bakeModel(owner, getElements(), colorData, spriteGetter, transform, overrides, location);
    }

    @Override
    public BakedModel bakeDynamic(IGeometryBakingContext owner, ModelState transform) {
        return bakeModel(owner, getElements(), colorData, Material::sprite, transform, ItemOverrides.EMPTY, BAKE_LOCATION);
    }

    public record ColorData(int color, int luminosity, @Nullable Boolean uvlock) {
        public static final ColorData DEFAULT = new ColorData(-1, -1, null);

        public boolean isUvlock(boolean defaultLock) {
            if(uvlock == null) return  defaultLock;
            return uvlock;
        }

        public static ColorData fromJson(JsonObject json){
            String key = "color";
            JsonElement element = json.get("key");

            int color = element != null || !element.isJsonNull() ? parseString(GsonHelper.convertToString(json, key), key) : -1;
            int luminosity = GsonHelper.getAsInt(json, "luminosity", -1);
            Boolean uvlock = null;
            if(json.has("uvlock")) {
                uvlock = GsonHelper.getAsBoolean(json, "uvlock");
            }
            return new ColorData(color, luminosity, uvlock);
        }

        private static Integer parseString(String color, String key) {
            if(color.charAt(0) != '-') {
                try {
                    int lenght = color.length();
                    if(lenght == 8) return (int)Long.parseLong(color, 16);
                    if(lenght == 6) return 0xFF000000 | Integer.parseInt(color, 16);
                } catch (NumberFormatException ex) {
                    //Bombaclat
                }
            }
            throw new JsonSyntaxException("Invalid color '" + color + "' at " + key);
        }
    }

    public static ColoredBlockModel deserialize(JsonObject json, JsonDeserializationContext context) {
        SimpleBlockModel model = SimpleBlockModel.deserialize(json, context);
        List<ColorData> colorData = json.has("colors") ? JsonHelper.parseList(GsonHelper.getAsJsonArray(json, "colors"),"colors", ColorData::fromJson) : Collections.emptyList();
        return new ColoredBlockModel(model, colorData);
    }

    public static int swapColorRedBlue(int color) {
        return (color | 0xFF00FF00)
                | ((color >> 16) & 0x000000FF)
                | ((color << 16) & 0x00FF0000);
    }

    public static IQuadTransformer applyColorQuadTransformer(int color) {
        int agrb = swapColorRedBlue(color);
        return quad -> {
            int[] vertices = quad.getVertices();
            for(int i = 0; i < 4; i++) {
                vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR] = agrb;
            }
        };
    }

    private static BakedQuad bakeFace(BlockElement part, BlockElementFace face, TextureAtlasSprite sprite, Direction facing, Transformation transform, boolean uvlock, int emissivity, ResourceLocation location) {
        return bakeQuad(part.from, part.to, face, sprite, facing, transform, uvlock, part.rotation, part.shade, emissivity, location);
    }

    private static BakedQuad bakeQuad(Vector3f from, Vector3f to, BlockElementFace face, TextureAtlasSprite sprite, Direction facing, Transformation transform, boolean uvlock, BlockElementRotation rotation, boolean shade, int emissivity, ResourceLocation location) {
        BlockFaceUV faceUV = face.uv;
        if(uvlock) faceUV = FaceBakery.recomputeUVs(faceUV, facing, transform, location);

        float[] originalUV = new float[faceUV.uvs.length];
        System.arraycopy(faceUV.uvs, 0, originalUV, 0, originalUV.length);
        float shrinkRatio = sprite.uvShrinkRatio();
        float u = (faceUV.uvs[0] + faceUV.uvs[0] + faceUV.uvs[2] + faceUV.uvs[2]) / 4.0F;
        float v = (faceUV.uvs[1] + faceUV.uvs[1] + faceUV.uvs[3] + faceUV.uvs[3]) / 4.0F;
        faceUV.uvs[0] = Mth.lerp(shrinkRatio, faceUV.uvs[0], u);
        faceUV.uvs[2] = Mth.lerp(shrinkRatio, faceUV.uvs[2], u);
        faceUV.uvs[1] = Mth.lerp(shrinkRatio, faceUV.uvs[1], v);
        faceUV.uvs[3] = Mth.lerp(shrinkRatio, faceUV.uvs[3], v);

        int[] vertexData = FACE_BAKERY.makeVertices(faceUV, sprite, facing, FACE_BAKERY.setupShape(from, to), transform, rotation, shade);
        Direction direction = FaceBakery.calculateFacing(vertexData);
        System.arraycopy(originalUV, 0, faceUV.uvs, 0, originalUV.length);
        if(rotation == null) FACE_BAKERY.recalculateWinding(vertexData, direction);
        ClientHooks.fillNormal(vertexData, direction);


        BakedQuad quad = new BakedQuad(vertexData, face.tintIndex, direction, sprite, shade);
        if(emissivity == -1) emissivity = face.getFaceData().blockLight();
        else if(emissivity > 0) QuadTransformers.settingEmissivity(emissivity).processInPlace(quad);

        return quad;
    }
}
