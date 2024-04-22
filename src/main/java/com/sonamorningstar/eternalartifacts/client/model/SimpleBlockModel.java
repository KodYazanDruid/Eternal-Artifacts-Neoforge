package com.sonamorningstar.eternalartifacts.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SimpleBlockModel implements IUnbakedGeometry<SimpleBlockModel> {
    public static final IGeometryLoader<SimpleBlockModel> LOADER = SimpleBlockModel::deserialize;
    static final ResourceLocation BAKE_LOCATION = new ResourceLocation(MODID, "dynamic_model_baking");

    @Getter
    @Nullable
    private ResourceLocation parentLocation;
    @Getter
    private final Map<String, Either<Material, String>> textures;
    private final List<BlockElement> parts;
    @Getter
    private BlockModel parent;

    public SimpleBlockModel(@Nullable ResourceLocation parentLocation, Map<String, Either<Material, String>> textures, List<BlockElement> parts) {
        this.parentLocation = parentLocation;
        this.textures = textures;
        this.parts = parts;
    }

    public SimpleBlockModel(SimpleBlockModel base) {
        this.parts = base.parts;
        this.textures = base.textures;
        this.parentLocation = base.parentLocation;
        this.parent = base.parent;
    }

    @SuppressWarnings("Deprecated")
    public List<BlockElement> getElements() {
        return parts.isEmpty() && parent != null ? parent.getElements() : parts;
    }

    public void fetchParent(IGeometryBakingContext owner, Function<ResourceLocation, UnbakedModel> modelGetter) {
        if(parent != null && parentLocation == null) return;

        Set<UnbakedModel> chain = Sets.newLinkedHashSet();
        parent = getParent(modelGetter, chain, parentLocation, owner.getModelName());
        if(parent == null) {
            parent = getMissing(modelGetter);
            parentLocation = ModelBakery.MISSING_MODEL_LOCATION;
        }

        for(BlockModel link = parent; link.getParentLocation() != null; link = link.parent) {
            chain.add(link);
            link.parent = getParent(modelGetter, chain, link.parentLocation, link.name);
            if(link.parent == null) {
                link.parent = getMissing(modelGetter);
                link.parentLocation = ModelBakery.MISSING_MODEL_LOCATION;
            }
        }
    }

    private static BlockModel getParent(Function<ResourceLocation, UnbakedModel> modelGetter, Set<UnbakedModel> chain, ResourceLocation parentLocation, String modelName) {
        UnbakedModel unbaked = modelGetter.apply(parentLocation);
        if(unbaked == null) {
            EternalArtifacts.LOGGER.warn("No parent '{}' while loading model '{}'", parentLocation, modelName);
            return null;
        }
        if(chain.contains(unbaked)) {
            EternalArtifacts.LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", modelName, chain.stream().map(Objects::toString).collect(Collectors.joining(" -> ")), parentLocation);
            return null;
        }
        if(!(unbaked instanceof BlockModel)) {
            throw new IllegalStateException("BlockModel parent has to be a block model.");
        }
        return (BlockModel) unbaked;
    }

    private static BlockModel getMissing(Function<ResourceLocation, UnbakedModel> modelGetter) {
        UnbakedModel unbaked = modelGetter.apply(ModelBakery.MISSING_MODEL_LOCATION);
        if(!(unbaked instanceof BlockModel)) {
            throw new IllegalStateException("Failed to load missing model.");
        }
        return (BlockModel) unbaked;
    }

    public static Collection<Material> getTextures(IGeometryBakingContext owner, List<BlockElement> elements, Set<Pair<String, String>> missingTextureErrors) {
        Set<Material> textures = Sets.newHashSet(owner.getMaterial("particle"));

        for(BlockElement element : elements) {
            for(BlockElementFace face : element.faces.values()) {
                Material material = owner.getMaterial(face.texture);
                if(Objects.equals(material.texture(), MissingTextureAtlasSprite.getLocation())) {
                    missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
                }
                textures.add(material);
            }
        }
        return textures;
    }

    public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        this.fetchParent(owner, modelGetter);
        return getTextures(owner, getElements(), missingTextureErrors);
    }

    public static SimpleBakedModel.Builder bakedBuilder(IGeometryBakingContext owner, ItemOverrides overries) {
        return new SimpleBakedModel.Builder(owner.useAmbientOcclusion(), owner.useBlockLight(), owner.isGui3d(), owner.getTransforms(), overries);
    }

    public static void bakePart(SimpleBakedModel.Builder builder, IGeometryBakingContext owner, BlockElement part, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, IQuadTransformer quadTransformer, ResourceLocation location) {
        for(Direction direction : part.faces.keySet()) {
            BlockElementFace face = part.faces.get(direction);
            String texture = face.texture;
            if(texture.charAt(0) == '#') {
                texture = texture.substring(1);
            }
            TextureAtlasSprite sprite = spriteGetter.apply(owner.getMaterial(texture));
            BakedQuad bakedQuad = BlockModel.bakeFace(part, face, sprite, direction, transform, location);
            quadTransformer.processInPlace(bakedQuad);
            if(face.cullForDirection == null) {
                builder.addUnculledFace(bakedQuad);
            }else {
                builder.addCulledFace(Direction.rotate(transform.getRotation().getMatrix(), face.cullForDirection), bakedQuad);
            }
        }
    }

    public static RenderTypeGroup getRenderTypeGroup(IGeometryBakingContext owner) {
        ResourceLocation renderTypeHint = owner.getRenderTypeHint();
        return renderTypeHint != null ? owner.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
    }

    public static IQuadTransformer applyTransform(ModelState modelState, Transformation transformation) {
        if(transformation.isIdentity()) {
            return QuadTransformers.empty();
        }else {
            return UnbakedGeometryHelper.applyRootTransform(modelState, transformation);
        }
    }

    public static BakedModel bakeModel(IGeometryBakingContext owner, List<BlockElement> elements, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
        TextureAtlasSprite particle = spriteGetter.apply(owner.getMaterial("particle"));
        SimpleBakedModel.Builder builder = bakedBuilder(owner, overrides).particle(particle);
        IQuadTransformer quadTransformer = applyTransform(transform, owner.getRootTransform());
        for(BlockElement element : elements) {
            bakePart(builder, owner, element, spriteGetter, transform, quadTransformer, location);
        }
        return builder.build(getRenderTypeGroup(owner));
    }

    public static BakedModel bakeDynamic(IGeometryBakingContext owner, List<BlockElement> elements, ModelState transform) {
        return bakeModel(owner, elements, Material::sprite, transform, ItemOverrides.EMPTY, BAKE_LOCATION);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
        return bakeModel(owner, this.getElements(), spriteGetter, transform, overrides, location);
    }

    public BakedModel bakeDynamic(IGeometryBakingContext owner, ModelState transform) {
        return bakeDynamic(owner, this.getElements(), transform);
    }

    public static SimpleBlockModel deserialize(JsonObject json, JsonDeserializationContext context) {
        String parentName = GsonHelper.getAsString(json, "parent", "");
        ResourceLocation parent = parentName.isEmpty() ? null : new ResourceLocation(parentName);
        Map<String, Either<Material, String>> textureMap;
        if(json.has("textures")) {
            ImmutableMap.Builder<String, Either<Material, String>> builder = new ImmutableMap.Builder<>();
            ResourceLocation atlas = InventoryMenu.BLOCK_ATLAS;
            JsonObject textures = GsonHelper.getAsJsonObject(json, "textures");
            for(Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                builder.put(entry.getKey(), BlockModel.Deserializer.parseTextureLocationOrReference(atlas, entry.getValue().getAsString()));
            }
            textureMap = builder.build();
        }else {
            textureMap = Collections.emptyMap();
        }
        List<BlockElement> parts;
        if(json.has("elements")) {
            parts = getModelElements(context, GsonHelper.getAsJsonArray(json, "elements"), "elements");
        } else {
            parts = Collections.emptyList();
        }
        return new SimpleBlockModel(parent, textureMap, parts);
    }

    private static List<BlockElement> getModelElements(JsonDeserializationContext context, JsonArray elements, String name) {
        if(elements.isJsonObject()) {
            return ImmutableList.of(context.deserialize(elements.getAsJsonObject(), BlockElement.class));
        }
        if(elements.isJsonArray()) {
            ImmutableList.Builder<BlockElement> builder = ImmutableList.builder();
            for(JsonElement json : elements.getAsJsonArray()) {
                builder.add((BlockElement) context.deserialize(json, BlockElement.class));
            }
            return builder.build();
        }

        throw new JsonSyntaxException("Missing " + name + ", expected to find a JsonArray or JsonObject.");
    }
}
