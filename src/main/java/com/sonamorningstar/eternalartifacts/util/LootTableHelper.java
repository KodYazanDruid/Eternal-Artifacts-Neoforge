package com.sonamorningstar.eternalartifacts.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.*;

public class LootTableHelper {

    public static LootTable getTable(ServerLevel serverLevel, ResourceLocation tableId){
        return serverLevel.getServer().getLootData().getLootTable(tableId);
    }

    public static List<LootPool> getPools(LootTable table) {
        return table.pools;
    }

    public static List<LootPoolEntryContainer> getEntries(LootPool pool) {
        return pool.entries;
    }
    //pool rolls
    private static List<Item> getItems(ServerLevel serverLevel, LootPoolEntryContainer entry) {
        List<Item> drops = new ArrayList<>();
        if (entry instanceof CompositeEntryBase composite)
            composite.children.forEach(child -> drops.addAll(getItems(serverLevel, child)));
        else if (entry instanceof LootItem lootItem)
            drops.add(lootItem.item.value());
        else if (entry instanceof LootTableReference reference) {
            getPools(getTable(serverLevel, reference.name)).forEach(pool -> getEntries(pool)
                .forEach(e -> drops.addAll(getItems(serverLevel, e)))
            );
        }
        drops.removeIf(Objects::isNull);
        return drops;
    }

    public static List<Item> getItems(ServerLevel serverLevel, ResourceLocation table) {
        List<Item> drops = new ArrayList<>();
        getPools(getTable(serverLevel, table)).forEach(pool -> getEntries(pool)
                .forEach(entry -> drops.addAll(getItems(serverLevel, entry)))
        );
        drops.removeIf(Objects::isNull);
        return drops.stream().distinct().toList();
    }
    
    public static Map<Item, Pair<Float, Float>> getItemsWithCounts(ServerLevel serverLevel, ResourceLocation table) {
        return getItemsWithCounts(serverLevel, table, 1.0F);
    }
    
    public static Map<Item, Pair<Float, Float>> getItemsWithCounts(ServerLevel serverLevel, ResourceLocation table, float luck) {
        Map<Item, Pair<Float, Float>> drops = new HashMap<>();
        getPools(getTable(serverLevel, table)).forEach(pool -> getEntries(pool).forEach(entry ->
             drops.putAll(getCount(drops, serverLevel, entry, pool, luck))
        ));
        return drops;
    }
    
    public static Map<Item, Pair<Float, Float>> getCount(Map<Item, Pair<Float, Float>> drops,
                                                         ServerLevel serverLevel, LootPoolEntryContainer entry,
                                                         LootPool pool, float luck) {
        if (entry instanceof CompositeEntryBase composite)
            composite.children.forEach(child -> drops.putAll(getCount(drops, serverLevel, child, pool, luck)));
        else if (entry instanceof LootItem lootItem) {
            Item value = lootItem.item.value();
            Pair<Float, Float> newMinMax = getItemCount(lootItem);
            Pair<Float, Float> rolls = getCount(pool.getRolls());
            Pair<Float, Float> bonusRolls = getCount(pool.getBonusRolls());
            Pair<Float, Float> totalRolls = Pair.of(
                rolls.getFirst() + bonusRolls.getFirst() * luck,
                rolls.getSecond() + bonusRolls.getSecond() * luck
            );
            if (drops.containsKey(value)) {
                Pair<Float, Float> minMax = drops.get(value);
                Pair<Float, Float> minPair = Pair.of(minMax.getFirst(), newMinMax.getFirst());
                Pair<Float, Float> maxPair = Pair.of(minMax.getSecond(), newMinMax.getSecond());
                drops.put(value, Pair.of(
                    Math.min(minPair.getFirst(), minPair.getSecond()) * totalRolls.getFirst(),
                    Math.max(maxPair.getFirst(), maxPair.getSecond()) * totalRolls.getSecond()
                ));
            } else drops.put(value, newMinMax);
        }
        else if (entry instanceof LootTableReference reference) {
            getPools(getTable(serverLevel, reference.name)).forEach(p -> getEntries(p)
                .forEach(e -> drops.putAll(getCount(drops, serverLevel, e, pool, luck)))
            );
        }
        return drops;
    }
    
    private static Pair<Float, Float> getItemCount(LootItem lootItem) {
        for (LootItemFunction function : lootItem.functions) {
            if (function instanceof SetItemCountFunction setCount) {
                return getCount(setCount.value);
            }
        }
        return Pair.of(1F, 1F);
    }
    
    private static Pair<Float, Float> getCount(NumberProvider provider) {
        if (provider instanceof ConstantValue constant)
            return Pair.of(constant.value(), constant.value());
        else if (provider instanceof UniformGenerator uniform) {
            Pair<Float, Float> minProvider = getCount(uniform.min());
            Pair<Float, Float> maxProvider = getCount(uniform.max());
            return Pair.of(Math.min(minProvider.getFirst(), minProvider.getSecond()),
                    Math.max(maxProvider.getFirst(), maxProvider.getSecond()));
        } else if (provider instanceof BinomialDistributionGenerator binomial) {
            Pair<Float, Float> binomialProvider = getCount(binomial.n());
            return Pair.of(0F, Math.max(binomialProvider.getFirst(), binomialProvider.getSecond()));
        } else return Pair.of(1F, 1F);
    }
}
