package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModLootTables;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ModLootSubProvider implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_DIRT, Items.DIRT, UniformGenerator.between(1, 3));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_COARSE_DIRT, Items.COARSE_DIRT, UniformGenerator.between(1, 3));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_ROOTED_DIRT, Items.ROOTED_DIRT, UniformGenerator.between(1, 3));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_PODZOL, Items.PODZOL, ConstantValue.exactly(1));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_MOSS_BLOCK, Items.MOSS_BLOCK, ConstantValue.exactly(1));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_MOSS_CARPET, Items.MOSS_CARPET, UniformGenerator.between(1, 3));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_GRAVEL, Items.GRAVEL, UniformGenerator.between(1, 2));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_SAND, Items.SAND, UniformGenerator.between(1, 2));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_RED_SAND, Items.RED_SAND, UniformGenerator.between(1, 2));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_CLAY_BLOCK, Items.CLAY, ConstantValue.exactly(1));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_SOUL_SAND, Items.SOUL_SAND, ConstantValue.exactly(1));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_SOUL_SOIL, Items.SOUL_SOIL, ConstantValue.exactly(1));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_MUD, Items.MUD, UniformGenerator.between(1, 2));
        generateSingleItem(output, ModLootTables.SLUDGE_REFINING_BONE_MEAL, Items.BONE_MEAL, UniformGenerator.between(1, 3));
    }

    private void generateSingleItem(BiConsumer<ResourceLocation, LootTable.Builder> output, ResourceLocation location, ItemLike generated, NumberProvider provider) {
        output.accept(location, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(generated)).apply(SetItemCountFunction.setCount(provider))
        ));
    }
}
