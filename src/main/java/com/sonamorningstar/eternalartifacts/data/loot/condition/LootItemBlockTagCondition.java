package com.sonamorningstar.eternalartifacts.data.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.core.ModLoots;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public record LootItemBlockTagCondition(TagKey<Block> tag) implements LootItemCondition {
    public static final Codec<LootItemBlockTagCondition> CODEC =
            RecordCodecBuilder.create(
                    inst -> inst.group(
                                    TagKey.codec(BuiltInRegistries.BLOCK.key()).fieldOf("tag").forGetter(LootItemBlockTagCondition::tag)
                            )
                            .apply(inst, LootItemBlockTagCondition::new)
            );

    @Override
    public LootItemConditionType getType() {
        return ModLoots.BLOCK_TAG_CONDITION.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.BLOCK_STATE);
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockState blockstate = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        return blockstate != null && blockstate.is(this.tag);
    }

    public static LootItemBlockTagCondition.Builder builder(TagKey<Block> tag) {
        return new LootItemBlockTagCondition.Builder(tag);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final TagKey<Block> tag;

        public Builder(TagKey<Block> tag) {
            this.tag = tag;
        }

        @Override
        public LootItemCondition build() {
            return new LootItemBlockTagCondition(this.tag/*, this.properties*/);
        }
    }
}
