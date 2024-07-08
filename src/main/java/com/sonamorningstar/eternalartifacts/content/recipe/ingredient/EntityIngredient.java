package com.sonamorningstar.eternalartifacts.content.recipe.ingredient;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EntityIngredient implements Predicate<EntityType<?>> {

    public static final EntityIngredient EMPTY = new EntityIngredient(Stream.empty());
    @Getter
    public final EntityIngredient.Value[] values;
    @Nullable
    private EntityType<?>[] entityTypes;
    @Nullable private Boolean areAllEntitiesNull;
    public static final Codec<EntityIngredient> MOD_CODEC ;
    public static final Codec<EntityIngredient> MOD_CODEC_NONEMPTY;
    //public static final Codec<EntityIngredient> CODEC;
    //public static final Codec<EntityIngredient> CODEC_NONEMPTY;
    public static final Codec<List<EntityIngredient>> LIST_CODEC;
    public static final Codec<List<EntityIngredient>> LIST_CODEC_NONEMPTY;

    protected EntityIngredient(Stream<? extends EntityIngredient.Value> values) {
        this.values = values.toArray(EntityIngredient.Value[]::new);
    }

    private EntityIngredient(EntityIngredient.Value[] values) {
        this.values = values;
    }

    public EntityType<?>[] getEntityTypes() {
        if (this.entityTypes == null) {
            this.entityTypes = Arrays.stream(this.values)
                    .flatMap(value -> value.getEntities().stream())
                    .distinct()
                    .toArray(EntityType<?>[]::new);
        }
        return this.entityTypes;
    }

    public boolean test(@Nullable EntityType<?> other) {
        if (other == null) {
            return false;
        } else if (!this.isEmpty()){
            for(EntityType<?> entity : this.getEntityTypes()) {
                if (areEntitiesEqual(entity, other)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean areEntitiesEqual(EntityType<?> left, EntityType<?> right) {
        return left.equals(right);
    }

    public void toNetwork(FriendlyByteBuf buff) {
        buff.writeCollection(Arrays.asList(this.getEntityTypes()), (writer, entity) -> writer.writeById(BuiltInRegistries.ENTITY_TYPE::getId, entity));
    }

    public static EntityIngredient fromNetwork(FriendlyByteBuf buff) {
        return EntityIngredient.of((EntityType<?>) buff.readCollection(NonNullList::createWithCapacity, writer -> writer.readById(BuiltInRegistries.ENTITY_TYPE::byId)).stream());
    }

    public static EntityIngredient fromJson(JsonElement element, boolean nonEmpty) {
        Codec<EntityIngredient> codec = nonEmpty ? MOD_CODEC : MOD_CODEC_NONEMPTY;
        return net.minecraft.Util.getOrThrow(codec.parse(com.mojang.serialization.JsonOps.INSTANCE, element), IllegalStateException::new);
    }

    private boolean areAllEntitiesNull() {
        Boolean empty = this.areAllEntitiesNull;
        if (empty == null) {
            boolean allEmpty = true;
            for (EntityType<?> entity : this.getEntityTypes()) {
                if (entity != null) {
                    allEmpty = false;
                    break;
                }
            }
            this.areAllEntitiesNull = empty = allEmpty;
        }
        return empty;
    }

    public boolean isEmpty() {
        return this.values.length == 0 || this.areAllEntitiesNull();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EntityIngredient ingredient && Arrays.equals(this.values, ingredient.values);
    }

    public static EntityIngredient fromValues(Stream<? extends EntityIngredient.Value> stream) {
        EntityIngredient ingredient = new EntityIngredient(stream);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static EntityIngredient of() {
        return EMPTY;
    }

    public static EntityIngredient of(EntityType<?>... entities) {
        return of(Arrays.stream(entities));
    }

    public static EntityIngredient of(Stream<EntityType<?>> entities) {
        return fromValues(entities.map(EntityIngredient.EntityValue::new));
    }

    public static EntityIngredient of(TagKey<EntityType<?>> tag) {
        return fromValues(Stream.of(new TagValue(tag)));
    }

    private static Codec<EntityIngredient> codec(boolean allowEmpty) {
        Codec<EntityIngredient.Value[]> codec = Codec.list(EntityIngredient.Value.CODEC)
                .comapFlatMap(list ->
                                !allowEmpty && list.isEmpty()
                                        ? DataResult.error(()-> "At least one entity must be defined!")
                                        : DataResult.success(list.toArray(new Value[0])),
                        List::of
                );
        return ExtraCodecs.either(codec, EntityIngredient.Value.CODEC)
                .flatComapMap(
                        either -> either.map(EntityIngredient::new, value -> new EntityIngredient(new EntityIngredient.Value[]{value})),
                        entityIngredient -> {
                            if (entityIngredient.values.length == 1) return DataResult.success(Either.right(entityIngredient.values[0]));
                            else return entityIngredient.values.length == 0 && !allowEmpty
                                    ? DataResult.error(()-> "At least one entity must be defined!")
                                    : DataResult.success(Either.left(entityIngredient.values));
                        }
                );
    }

    static {
        /*CODEC =
        CODEC_NONEMPTY = */
        MOD_CODEC = codec(true);
        MOD_CODEC_NONEMPTY = codec(false);
        LIST_CODEC = MOD_CODEC.listOf();
        LIST_CODEC_NONEMPTY = MOD_CODEC_NONEMPTY.listOf();
    }

    public record EntityValue(EntityType<?> entityType, BiFunction<EntityType<?>, EntityType<?>, Boolean> comparator) implements Value {
        public EntityValue(EntityType<?> entityType) {
            this(entityType, EntityValue::areEntitiesEqual);
        }

        static final Codec<EntityValue> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(entityValue -> entityValue.entityType)
        ).apply(inst, EntityValue::new));

        @Override
        public boolean equals(Object other) {
            if(!(other instanceof EntityValue entityValue)) return false;
            else return comparator().apply(entityType, entityValue.entityType);
        }

        @Override
        public Collection<EntityType<?>> getEntities() {
            return Collections.singleton(this.entityType);
        }

        private static boolean areEntitiesEqual(EntityType<?> a, EntityType<?> b) {
            return a.getClass().isInstance(b.getClass()) &&
                    a.equals(b);
        }
    }

    public record TagValue(TagKey<EntityType<?>> tag) implements Value {

        static final Codec<TagValue> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                TagKey.codec(Registries.ENTITY_TYPE).fieldOf("tag").forGetter(tagValue -> tagValue.tag)
        ).apply(inst, TagValue::new));

        @Override
        public boolean equals(Object other) {
            return other instanceof TagValue tagValue && tagValue.tag.location().equals(this.tag.location());
        }

        @Override
        public Collection<EntityType<?>> getEntities() {
            List<EntityType<?>> list = new ArrayList<>();
            for(Holder<EntityType<?>> holder : BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(this.tag)) {
                list.add(holder.value());
            }
            return list;
        }
    }

    public interface Value{
        Codec<EntityIngredient.Value> CODEC = ExtraCodecs.xor(EntityIngredient.EntityValue.CODEC, EntityIngredient.TagValue.CODEC)
                .xmap(either -> either.map(entityValue -> entityValue, tagValue -> tagValue), value -> {
                    if(value instanceof EntityIngredient.EntityValue entityValue) return Either.left(entityValue);
                    else if(value instanceof EntityIngredient.TagValue tagValue) return Either.right(tagValue);
                    else throw new UnsupportedOperationException("This is neither an entity value nor a tag value.");
                });

        Collection<EntityType<?>> getEntities();

    }
}
