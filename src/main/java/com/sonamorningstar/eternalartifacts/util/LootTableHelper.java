package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

}
