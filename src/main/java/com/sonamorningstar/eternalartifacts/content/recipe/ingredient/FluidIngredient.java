package com.sonamorningstar.eternalartifacts.content.recipe.ingredient;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FluidIngredient implements Predicate<FluidStack> {

    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());
    @Getter
    public final FluidIngredient.Value[] values;
    @Nullable
    private FluidStack[] fluidStacks;
    @Nullable private Boolean areAllStacksEmpty;

    public static final Codec<FluidIngredient> CODEC = codec(true);
    public static final Codec<FluidIngredient> CODEC_NONEMPTY = codec(false);
    public static final Codec<List<FluidIngredient>> LIST_CODEC = CODEC.listOf();
    public static final Codec<List<FluidIngredient>> LIST_CODEC_NONEMPTY = CODEC_NONEMPTY.listOf();

    protected FluidIngredient(Stream<? extends FluidIngredient.Value> values) {
        this.values = values.toArray(Value[]::new);
    }

    private FluidIngredient(FluidIngredient.Value[] values) {
        this.values = values;
    }

    public FluidStack[] getFluidStacks() {
        if (this.fluidStacks == null) {
            this.fluidStacks = Arrays.stream(this.values)
                    .flatMap(value -> value.getFluids().stream())
                    .distinct()
                    .toArray(FluidStack[]::new);
        }
        return this.fluidStacks;
    }

    @Override
    public boolean test(@Nullable FluidStack other) {
        if (other == null) {
            return false;
        } else if (this.isEmpty()) {
            return other.isEmpty();
        } else {
            for(FluidStack stack : this.getFluidStacks()) {
                if (areStacksEqual(stack, other)) {
                    return true;
                }
            }
            return false;
        }
    }

    protected boolean areStacksEqual(FluidStack left, FluidStack right) {
        return left.is(right.getFluid());
    }

    public void toNetwork(FriendlyByteBuf buff) {
        buff.writeCollection(Arrays.asList(this.getFluidStacks()), FriendlyByteBuf::writeFluidStack);
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buff) {
        /*int size = buff.readVarInt();
        if (size == -1) return buff.readWithCodecTrusted(net.minecraft.nbt.NbtOps.INSTANCE, CODEC);
        else*/ return new FluidIngredient(Stream.generate(() -> new FluidIngredient.FluidValue(buff.readFluidStack())));
    }

    public static FluidIngredient fromJson(JsonElement element, boolean nonEmpty) {
        Codec<FluidIngredient> codec = nonEmpty ? CODEC : CODEC_NONEMPTY;
        return net.minecraft.Util.getOrThrow(codec.parse(com.mojang.serialization.JsonOps.INSTANCE, element), IllegalStateException::new);
    }

    private boolean areAllStacksEmpty() {
        Boolean empty = this.areAllStacksEmpty;
        if (empty == null) {
            boolean allEmpty = true;
            for (FluidStack stack : this.getFluidStacks()) {
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
        return other instanceof FluidIngredient ingredient && Arrays.equals(this.values, ingredient.values);
    }

    public static FluidIngredient fromValues(Stream<? extends FluidIngredient.Value> stream) {
        FluidIngredient ingredient = new FluidIngredient(stream);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static FluidIngredient of() {
        return EMPTY;
    }

    public static FluidIngredient of(FluidStack... stacks) {
        return of(Arrays.stream(stacks));
    }

    public static FluidIngredient of(Stream<FluidStack> stacks) {
        return fromValues(stacks.filter(p_43944_ -> !p_43944_.isEmpty()).map(FluidIngredient.FluidValue::new));
    }

    public static FluidIngredient of(TagKey<Fluid> tag) {
        return fromValues(Stream.of(new FluidIngredient.TagValue(tag)));
    }

    private static Codec<FluidIngredient> codec(boolean allowEmpty) {
        Codec<FluidIngredient.Value[]> codec = Codec.list(Value.CODEC)
                .comapFlatMap(list ->
                        !allowEmpty && list.isEmpty()
                        ? DataResult.error(()-> "At least one fluidstack must be defined.")
                        : DataResult.success(list.toArray(new FluidIngredient.Value[0])),
                        List::of
                );
        return ExtraCodecs.either(codec, Value.CODEC)
                .flatComapMap(
                        either -> either.map(FluidIngredient::new, value -> new FluidIngredient(new FluidIngredient.Value[]{value})),
                        fluidIngredient -> {
                            if (fluidIngredient.values.length == 1) return DataResult.success(Either.right(fluidIngredient.values[0]));
                            else return fluidIngredient.values.length == 0 && !allowEmpty
                                ? DataResult.error(()-> "At least one fluidstack must be defined.")
                                : DataResult.success(Either.left(fluidIngredient.values));
                        }
                );
    }

    public record FluidValue(FluidStack fluidStack, BiFunction<FluidStack, FluidStack, Boolean> comparator) implements FluidIngredient.Value{
        public FluidValue(FluidStack fluidStack) {
            this(fluidStack, FluidValue::areStacksEqual);
        }

        static final Codec<FluidValue> CODEC = RecordCodecBuilder.create( inst -> inst.group(
                FluidStack.CODEC.fieldOf("fluid").forGetter(fluidValue -> fluidValue.fluidStack)
        ).apply(inst, FluidIngredient.FluidValue::new));

        @Override
        public boolean equals(Object other) {
            if(!(other instanceof FluidValue fluidValue)) return false;
            else return comparator.apply(fluidStack, fluidValue.fluidStack);
        }

        @Override
        public Collection<FluidStack> getFluids() { return Collections.singleton(this.fluidStack); }

        private static boolean areStacksEqual(FluidStack a, FluidStack b) {
            return a.getFluid().equals(b.getFluid()) &&
                    a.getAmount() == b.getAmount();
        }
    }

    public record TagValue(TagKey<Fluid> tag) implements FluidIngredient.Value {

        public TagValue(TagKey<Fluid> tag) {
            this.tag = tag;
        }

        static final Codec<TagValue> CODEC = RecordCodecBuilder.create( inst -> inst.group(
                TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(tagValue -> tagValue.tag)
        ).apply(inst, FluidIngredient.TagValue::new));

        @Override
        public boolean equals(Object other) {
            return other instanceof TagValue tagValue && tagValue.tag.location().equals(this.tag.location());
        }


        @Override
        public Collection<FluidStack> getFluids() {
            List<FluidStack> list = new ArrayList<>();
            for(Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(this.tag)) {
                list.add(new FluidStack(holder, 1000));
            }
            return list;
        }

    }

    public interface Value{
        Codec<FluidIngredient.Value> CODEC = ExtraCodecs.xor(FluidIngredient.FluidValue.CODEC, FluidIngredient.TagValue.CODEC)
                .xmap(either -> either.map(fluidValue -> fluidValue, tagValue -> tagValue), value -> {
                    if(value instanceof FluidIngredient.FluidValue fluidValue) return Either.left(fluidValue);
                    else if(value instanceof FluidIngredient.TagValue tagValue) return Either.right(tagValue);
                    else throw new UnsupportedOperationException("This is neither a fluid value nor a tag value.");
                });

        Collection<FluidStack> getFluids();
    }

}
