package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModLootTables;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ModLootSubProvider implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        generateSimplePool(output, ModLootTables.COPPER_OREBERRY_HARVEST, ModItems.COPPER_NUGGET, UniformGenerator.between(1, 3));
        generateSimplePool(output, ModLootTables.IRON_OREBERRY_HARVEST, Items.IRON_NUGGET, ConstantValue.exactly(1));
        generateSimplePool(output, ModLootTables.GOLD_OREBERRY_HARVEST, Items.GOLD_NUGGET, ConstantValue.exactly(1));
        generateSimplePool(output, ModLootTables.EXPERIENCE_OREBERRY_HARVEST, ModItems.EXPERIENCE_BERRY, UniformGenerator.between(1, 2));

    }

    private void generateSimplePool(BiConsumer<ResourceLocation, LootTable.Builder> output, ResourceLocation location, ItemLike generated, NumberProvider provider) {
        output.accept(location, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(provider)
                        .add(LootItem.lootTableItem(generated)))
        );
    }
}
