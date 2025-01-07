package com.sonamorningstar.eternalartifacts.api.charm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;
import lombok.Getter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.*;

@Getter
public class CharmAttributes {
    public static final String ATTR_KEY = "CharmAttributeModifiers";

    private final Either<Item, TagKey<Item>> holder;
    private final Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
    private final Set<CharmType> types = new HashSet<>();

    private CharmAttributes(Either<Item, TagKey<Item>> holder) {
        this.holder = holder;
    }

    public void addModifier(Attribute attribute, AttributeModifier modifier) {
        modifiers.put(attribute, modifier);
    }

    public boolean isStackCorrect(ItemStack stack) {
        return holder.map(stack::is, stack::is);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharmAttributes that = (CharmAttributes) o;
        return Objects.equals(getHolder(), that.getHolder()) &&
                Objects.equals(getModifiers(), that.getModifiers()) &&
                Objects.equals(getTypes(), that.getTypes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHolder(), getModifiers(), getTypes());
    }

    public static class Builder {
        private final CharmAttributes attributes;

        private Builder(Either<Item, TagKey<Item>> item) {
            this.attributes = new CharmAttributes(item);
        }

        public static Builder of(ItemLike item) {
            return new Builder(Either.left(item.asItem()));
        }

        public static Builder of(TagKey<Item> item) {
            return new Builder(Either.right(item));
        }

        public Builder addModifier(Attribute attribute, AttributeModifier modifier) {
            attributes.addModifier(attribute, modifier);
            return this;
        }

        public Builder addType(CharmType type) {
            attributes.getTypes().add(type);
            return this;
        }

        public CharmAttributes build() {
            return attributes;
        }
    }
}
