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

    public static List<Item> getItems(LootPoolEntryContainer entry) {
        List<Item> drops = new ArrayList<>();
        if (entry instanceof CompositeEntryBase composite)
            composite.children.forEach(child -> drops.addAll(LootTableHelper.getItems(child)));
        if (entry instanceof LootItem lootItem)
            drops.add(lootItem.item.value());
        drops.removeIf(Objects::isNull);
        return drops;
    }

    public static List<Item> getItems(ServerLevel serverLevel, ResourceLocation table) {
        List<Item> drops = new ArrayList<>();
        getPools(getTable(serverLevel, table)).forEach(pool -> getEntries(pool)
                .forEach(entry -> drops.addAll(getItems(entry)))
        );
        drops.removeIf(Objects::isNull);
        return drops;
    }
    
    //Needs adjustements. Take pool functions into account and be aware of "add" parameter.
    public static Map<Item, Pair<Float, Float>> getItemsWithCounts(ServerLevel serverLevel, ResourceLocation table) {
        Map<Item, Pair<Float, Float>> drops = new HashMap<>();
        getPools(getTable(serverLevel, table)).forEach(pool -> getEntries(pool).forEach(entry ->
            drops.putAll(getCount(entry))
        ));
        return drops;
    }
    
    public static Map<Item, Pair<Float, Float>> getCount(LootPoolEntryContainer entry) {
        Map<Item, Pair<Float, Float>> drops = new HashMap<>();
        if (entry instanceof CompositeEntryBase composite)
            composite.children.forEach(child -> drops.putAll(LootTableHelper.getCount(child)));
        if (entry instanceof LootItem lootItem) {
            drops.put(lootItem.item.value(), getItemCount(lootItem));
        }
        return drops;
    }
    
    private static Pair<Float, Float> getItemCount(LootItem lootItem) {
        for (LootItemFunction function : lootItem.functions) {
            if (function instanceof SetItemCountFunction setCount) {
                NumberProvider provider = setCount.value;
                return getCount(provider);
            }
        }
        return Pair.of(1F, 1F);
    }
    
    private static Pair<Float, Float> getCount(NumberProvider provider) {
        if (provider instanceof ConstantValue constant)
            return Pair.of(constant.value(), constant.value());
        if (provider instanceof UniformGenerator uniform)
            return Pair.of(getCount(uniform.min()).getFirst(), getCount(uniform.max()).getSecond());
        if (provider instanceof BinomialDistributionGenerator binom)
            return Pair.of(0F, getCount(binom.n()).getSecond());
        return Pair.of(1F, 1F);
    }
}
