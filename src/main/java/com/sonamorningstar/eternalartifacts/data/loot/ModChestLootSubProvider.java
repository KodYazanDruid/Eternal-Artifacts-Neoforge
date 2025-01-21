package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModLootTables;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ModChestLootSubProvider implements LootTableSubProvider {
	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
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
		
		output.accept(ModLootTables.PLAINS_HOUSE_ENTRANCE, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(Items.BREAD).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
				.add(LootItem.lootTableItem(Items.APPLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
				.add(LootItem.lootTableItem(Items.CARROT).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))))
				.add(LootItem.lootTableItem(Items.BAKED_POTATO).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
			).withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(2, 3))
				.add(LootItem.lootTableItem(Items.OAK_LOG).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
				.add(LootItem.lootTableItem(Items.OAK_PLANKS).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 6))))
				.add(LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 6))))
			).withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(1, 2))
				.add(LootItem.lootTableItem(Items.WHEAT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
				.add(LootItem.lootTableItem(Items.WHEAT_SEEDS).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
				.add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
			).withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(75).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
				.add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(90).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8))))
				.add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(50).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
				.add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(65).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 7))))
			)
		);
		output.accept(ModLootTables.PLAINS_HOUSE_DESK, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(Items.MAP)
					.apply(ExplorationMapFunction.makeExplorationMap()
						.setDestination(StructureTags.MINESHAFT)
						.setMapDecoration(MapDecoration.Type.RED_X)
						.setZoom((byte)1)
						.setSkipKnownStructures(false)
					)
					.apply(SetNameFunction.setName(ModConstants.FILLED_MAP.withSuffixTranslatable("mineshaft")))
				)
			).withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(1, 3))
				.add(LootItem.lootTableItem(Items.BOOK).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
				.add(LootItem.lootTableItem(Items.WRITABLE_BOOK).setWeight(1).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
				.add(LootItem.lootTableItem(Items.PAPER).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
			).withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(1, 2))
				.add(LootItem.lootTableItem(Items.INK_SAC).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
				.add(LootItem.lootTableItem(Items.FEATHER).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
			).withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(Items.BOOK).apply(EnchantRandomlyFunction.randomApplicableEnchantment()))
				.add(LootItem.lootTableItem(Items.GOLDEN_APPLE))
			)
		);
	}
}
