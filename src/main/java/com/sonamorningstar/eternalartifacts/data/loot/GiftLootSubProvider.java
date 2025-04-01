package com.sonamorningstar.eternalartifacts.data.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class GiftLootSubProvider implements net.minecraft.data.loot.LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        /*output.accept(BuiltInLootTables.SNIFFER_DIGGING,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ModItems.ANCIENT_SEED))
                ));*/
    }
}
