package com.sonamorningstar.eternalartifacts.content.recipe.ingredient;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SizedIngredient implements Predicate<ItemStack> {
    public static final SizedIngredient EMPTY = new SizedIngredient(Stream.empty());
    @Getter
    public final Value[] values;
    @Nullable
    private ItemStack[] itemStacks;
    @Nullable private Boolean areAllStacksEmpty;

    public static final Codec<SizedIngredient> CODEC = codec(true);
    public static final Codec<SizedIngredient> CODEC_NONEMPTY = codec(false);
    public static final Codec<List<SizedIngredient>> LIST_CODEC = CODEC.listOf();
    public static final Codec<List<SizedIngredient>> LIST_CODEC_NONEMPTY = CODEC_NONEMPTY.listOf();

    protected SizedIngredient(Stream<? extends Value> values) {
        this.values = values.toArray(Value[]::new);
    }

    private SizedIngredient(Value[] values) {
        this.values = values;
    }

    public ItemStack[] getItems() {
        if (this.itemStacks == null) {
            this.itemStacks = Arrays.stream(this.values)
                    .flatMap(value -> value.getItems().stream())
                    .distinct()
                    .toArray(ItemStack[]::new);
        }

        return this.itemStacks;
    }

    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) return false;
        else if (this.isEmpty()) return stack.isEmpty();
        else {
            for(ItemStack itemstack : this.getItems()) {
                if (areStacksEqual(itemstack, stack)) return true;
            }
            return false;
        }
    }

    public boolean testItem(@Nullable Item item) {
        if (item == null || isEmpty()) return false;
        else {
            for (ItemStack itemStack : getItems()) {
                if (itemStack.is(item)) return true;
            }
            return false;
        }
    }

    public boolean canBeSustained(@Nullable ItemStack stack) {
        if (stack == null) return false;
        else if (this.isEmpty()) return stack.isEmpty();
        else {
            for(ItemStack itemstack : this.getItems()) {
                if (isSameItem(itemstack, stack) && itemstack.getCount() <= stack.getCount()) return true;
            }
            return false;
        }
    }

    public Ingredient toIngredient() {
        return Ingredient.of(getItems());
    }

    protected boolean isSameItem(ItemStack left, ItemStack right) {
        return left.is(right.getItem());
    }

    protected boolean areStacksEqual(ItemStack left, ItemStack right) {
        return isSameItem(left, right) && left.getCount() == right.getCount();
    }

    public void toNetwork(FriendlyByteBuf buff) {
        buff.writeCollection(Arrays.asList(this.getItems()), FriendlyByteBuf::writeItem);
    }

    private boolean areAllStacksEmpty() {
        Boolean empty = this.areAllStacksEmpty;
        if (empty == null) {
            boolean allEmpty = true;
            for (ItemStack stack : this.getItems()) {
                if (!stack.isEmpty()) {
                    allEmpty = false;
                    break;
                }
            }
            this.areAllStacksEmpty = empty = allEmpty;
        }
        return empty;
    }

    public boolean isEmpty() {
        return this.values.length == 0 || this.areAllStacksEmpty();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SizedIngredient ingredient && Arrays.equals(this.values, ingredient.values);
    }

    public static SizedIngredient fromValues(Stream<? extends Value> stream) {
        SizedIngredient ingredient = new SizedIngredient(stream);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static SizedIngredient fromJson(JsonElement element, boolean nonEmpty) {
        Codec<SizedIngredient> codec = nonEmpty ? CODEC : CODEC_NONEMPTY;
        return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, element), IllegalStateException::new);
    }

    public static SizedIngredient fromNetwork(FriendlyByteBuf buff) {
        var size = buff.readVarInt();
        return new SizedIngredient(Stream.generate(() -> new SizedIngredient.ItemValue(buff.readItem())).limit(size));
    }

    public static SizedIngredient of() {
        return EMPTY;
    }

    public static SizedIngredient of(ItemLike... items) {
        return of(Arrays.stream(items).map(ItemStack::new));
    }

    public static SizedIngredient of(ItemStack... stacks) {
        return of(Arrays.stream(stacks));
    }

    public static SizedIngredient of(Stream<ItemStack> stacks) {
        return fromValues(stacks.filter(value -> !value.isEmpty()).map(ItemValue::new));
    }

    public static SizedIngredient of(TagKey<Item> tag, int amount) {
        return fromValues(Stream.of(new TagValue(tag, amount)));
    }

    private static Codec<SizedIngredient> codec(boolean allowEmpty) {
        Codec<Value[]> codec = Codec.list(Value.CODEC)
                .comapFlatMap(
                        list -> !allowEmpty && list.isEmpty()
                                ? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
                                : DataResult.success(list.toArray(new Value[0])),
                        List::of
                );
        return ExtraCodecs.either(codec, Value.CODEC)
                .flatComapMap(
                        either -> either.map(SizedIngredient::new, value -> new SizedIngredient(new Value[]{value})),
                        sizedIngredient -> {
                            if (sizedIngredient.values.length == 1) {
                                return DataResult.success(Either.right(sizedIngredient.values[0]));
                            } else {
                                return sizedIngredient.values.length == 0 && !allowEmpty
                                        ? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
                                        : DataResult.success(Either.left(sizedIngredient.values));
                            }
                        }
                );
    }

    public record ItemValue(ItemStack stack, BiFunction<ItemStack, ItemStack, Boolean> comparator) implements Value {
        public ItemValue(ItemStack stack) {
            this(stack, ItemValue::areStacksEqual);
        }

        static final Codec<ItemValue> CODEC = RecordCodecBuilder.create(
                inst -> inst.group(
                    ItemStack.CODEC.fieldOf("item_stack").forGetter(value -> value.stack)
                ).apply(inst, ItemValue::new)
        );

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ItemValue itemValue)) return false;
            else return comparator().apply(stack, itemValue.stack);

        }

        @Override
        public Collection<ItemStack> getItems() {
            return Collections.singleton(stack);
        }

        private static boolean areStacksEqual(ItemStack left, ItemStack right) {
            return left.getItem().equals(right.getItem())
                    && left.getCount() == right.getCount();
        }
    }

    public record TagValue(TagKey<Item> tag, int amount) implements Value {
        static final Codec<TagValue> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(value -> value.tag),
                PrimitiveCodec.INT.fieldOf("amount").forGetter(value -> value.amount)
            ).apply(inst, TagValue::new)
        );

        @Override
        public boolean equals(Object other) {
            return other instanceof TagValue tagValue &&
                    tagValue.tag.location().equals(this.tag.location()) &&
                    tagValue.amount == this.amount;
        }

        @Override
        public Collection<ItemStack> getItems() {
            List<ItemStack> list = Lists.newArrayList();

            for(Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
                list.add(new ItemStack(holder, amount));
            }

            if (list.isEmpty()) {
                list.add(new ItemStack(Blocks.BARRIER).setHoverName(Component.literal("Empty Tag: " + this.tag.location())));
            }

            return list;
        }
    }

    public interface Value {
        Codec<Value> CODEC = ExtraCodecs.xor(ItemValue.CODEC, TagValue.CODEC)
                .xmap(either -> either.map(itemValue -> itemValue, tagValue -> tagValue), value -> {
                    if (value instanceof TagValue tagValue) return Either.right(tagValue);
                    else if (value instanceof ItemValue itemValue) return Either.left(itemValue);
                    else throw new UnsupportedOperationException("This is neither an item value nor a tag value.");
                });

        Collection<ItemStack> getItems();
    }
}
