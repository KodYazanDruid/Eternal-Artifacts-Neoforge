package com.sonamorningstar.eternalartifacts.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.content.block.entity.IRetexturedBlockEntity;
import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModLoots;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class RetexturedLootFunction extends LootItemConditionalFunction {
    public static final Codec<RetexturedLootFunction> CODEC = RecordCodecBuilder.create(p -> commonFields(p).apply(p, RetexturedLootFunction::new));

    public static @NotNull Builder builder() {
        return simpleBuilder(RetexturedLootFunction::new);
    }

    public RetexturedLootFunction(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        BlockEntity entity = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if(entity instanceof IRetexturedBlockEntity retextured) {
            RetexturedBlockItem.setTexture(stack, retextured.getTextureName());
        }else {
            String name = entity == null ? "null" : entity.getClass().getName();
            EternalArtifacts.LOGGER.warn("Found wrong block entity for loot function, expected IRetexturedBlockEntity, found {}", name);
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLoots.RETEXTURED_FUNCTION.get();
    }

}
