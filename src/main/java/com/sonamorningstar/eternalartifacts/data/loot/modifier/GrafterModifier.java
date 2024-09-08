package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.function.Supplier;

public class GrafterModifier extends LootModifier {

    public static final Supplier<Codec<GrafterModifier>> CODEC = Suppliers.memoize(()-> RecordCodecBuilder.create(instance -> codecStart(instance)
            .apply(instance, GrafterModifier::new)));

    public GrafterModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for(LootItemCondition condition : conditions) if(!condition.test(context)) return generatedLoot;
        BlockState minedBlockState = context.getParam(LootContextParams.BLOCK_STATE);
        ServerLevel serverLevel = context.getLevel();
        List<Item> possibleLoots = LootTableHelper.getItems(serverLevel, minedBlockState.getBlock());
        ItemStack sapling = ItemStack.EMPTY;
        for (Item item : possibleLoots) {
            if (item instanceof BlockItem bi) {
                Block block = bi.getBlock();
                if (block.defaultBlockState().is(BlockTags.SAPLINGS))
                    sapling = item.getDefaultInstance();
            }
            if (!sapling.isEmpty()) break;
        }
        ItemStack finalSapling = sapling;
        boolean flag = generatedLoot.stream().anyMatch(stack -> stack.is(finalSapling.getItem()));
        if (!sapling.isEmpty() && !flag) generatedLoot.add(sapling);
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
