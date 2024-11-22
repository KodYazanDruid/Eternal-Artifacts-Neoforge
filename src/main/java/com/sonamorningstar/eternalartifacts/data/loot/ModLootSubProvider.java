package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
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
        generateSingleItem(output, ModLootTables.COPPER_OREBERRY_HARVEST, ModItems.COPPER_NUGGET, UniformGenerator.between(1, 3));
        generateSingleItem(output, ModLootTables.IRON_OREBERRY_HARVEST, Items.IRON_NUGGET, ConstantValue.exactly(1));
        generateSingleItem(output, ModLootTables.GOLD_OREBERRY_HARVEST, Items.GOLD_NUGGET, ConstantValue.exactly(1));
        generateSingleItem(output, ModLootTables.EXPERIENCE_OREBERRY_HARVEST, ModItems.EXPERIENCE_BERRY, UniformGenerator.between(1, 2));
        generateSingleItem(output, ModLootTables.MANGANESE_OREBERRY_HARVEST, ModItems.MANGANESE_NUGGET, ConstantValue.exactly(1));

        output.accept(ModLootTables.SURVIVALISTS_IGLOO, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(Items.RABBIT_STEW).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(Items.COOKED_COD).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 4))))
                .add(LootItem.lootTableItem(Items.COOKED_SALMON).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
                .add(LootItem.lootTableItem(Items.COOKED_RABBIT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                .add(LootItem.lootTableItem(Items.SWEET_BERRIES).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 8))))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(3, 4))
                .add(LootItem.lootTableItem(ModBlocks.SNOW_BRICKS).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 8))))
                .add(LootItem.lootTableItem(ModBlocks.ICE_BRICKS).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 7))))
                .add(LootItem.lootTableItem(Items.SPRUCE_LOG).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 5))))
                .add(LootItem.lootTableItem(Items.STRING).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
                .add(LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                .add(LootItem.lootTableItem(Items.RABBIT_HIDE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                .add(LootItem.lootTableItem(Items.FERN).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                .add(LootItem.lootTableItem(Items.LARGE_FERN).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
            ).withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(75).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(50).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(25).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(15).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
            )
        );


    }

    private void generateSingleItem(BiConsumer<ResourceLocation, LootTable.Builder> output, ResourceLocation location, ItemLike generated, NumberProvider provider) {
        output.accept(location, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(generated)).apply(SetItemCountFunction.setCount(provider))
        ));
    }
}
