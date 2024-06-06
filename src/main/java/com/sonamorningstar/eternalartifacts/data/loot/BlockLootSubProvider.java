package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.content.block.AncientCropBlock;
import com.sonamorningstar.eternalartifacts.content.block.OreBerryBlock;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModLootTables;
import com.sonamorningstar.eternalartifacts.loot.function.RetexturedLootFunction;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.stream.Collectors;

public class BlockLootSubProvider extends net.minecraft.data.loot.BlockLootSubProvider {

    public BlockLootSubProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.ANVILINATOR.get());
        dropSelf(ModBlocks.BOOK_DUPLICATOR.get());
        dropSelf(ModBlocks.BIOFURNACE.get());
        dropSelf(ModBlocks.RESONATOR.get());
        dropSelf(ModBlocks.PINK_SLIME_BLOCK.get());
        dropSelf(ModBlocks.ROSY_FROGLIGHT.get());
        dropSelf(ModBlocks.MACHINE_BLOCK.get());
        dropSelf(ModBlocks.SUGAR_CHARCOAL_BLOCK.get());
        add(ModBlocks.FORSYTHIA.get(), createSinglePropConditionTable(ModBlocks.FORSYTHIA.get(), DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
        dropSelf(ModBlocks.FOUR_LEAF_CLOVER.get());
        dropSelf(ModBlocks.MEAT_PACKER.get());
        dropSelf(ModBlocks.CHLOROPHYTE_DEBRIS.get());
        dropSelf(ModBlocks.SANDY_TILED_STONE_BRICKS.get());
        dropSelf(ModBlocks.SANDY_STONE_BRICKS.get());
        dropSelf(ModBlocks.MEAT_SHREDDER.get());
        dropSelf(ModBlocks.SANDY_PRISMARINE.get());
        dropSelf(ModBlocks.VERY_SANDY_PRISMARINE.get());
        dropSelf(ModBlocks.SANDY_DARK_PRISMARINE.get());
        dropSelf(ModBlocks.VERY_SANDY_DARK_PRISMARINE.get());
        dropSelf(ModBlocks.PAVED_PRISMARINE_BRICKS.get());
        dropSelf(ModBlocks.SANDY_PAVED_PRISMARINE_BRICKS.get());
        dropSelf(ModBlocks.SANDY_PRISMARINE_BRICKS.get());
        dropSelf(ModBlocks.LAYERED_PRISMARINE.get());
        dropSelf(ModBlocks.CITRUS_LOG.get());
        dropSelf(ModBlocks.STRIPPED_CITRUS_LOG.get());
        dropSelf(ModBlocks.CITRUS_WOOD.get());
        dropSelf(ModBlocks.STRIPPED_CITRUS_WOOD.get());
        dropSelf(ModBlocks.CITRUS_PLANKS.get());
        dropSelf(ModBlocks.BATTERY_BOX.get());

        generateOreBerryTables(ModBlocks.COPPER_ORE_BERRY, ModLootTables.COPPER_OREBERRY_HARVEST);
        generateOreBerryTables(ModBlocks.IRON_ORE_BERRY, ModLootTables.IRON_OREBERRY_HARVEST);
        generateOreBerryTables(ModBlocks.GOLD_ORE_BERRY, ModLootTables.GOLD_OREBERRY_HARVEST);
        generateOreBerryTables(ModBlocks.EXPERIENCE_ORE_BERRY, ModLootTables.EXPERIENCE_OREBERRY_HARVEST);

        add(ModBlocks.GRAVEL_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(ModBlocks.GRAVEL_COPPER_ORE.get(), this::createCopperOreDrops);
        add(ModBlocks.GRAVEL_IRON_ORE.get(), block -> createOreDrop(block, Items.RAW_IRON));
        add(ModBlocks.GRAVEL_GOLD_ORE.get(), block -> createOreDrop(block, Items.RAW_GOLD));

        add(ModBlocks.GARDENING_POT.get(), LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(
                        LootItem.lootTableItem(ModItems.GARDENING_POT.get())
                            .apply(RetexturedLootFunction.builder())
                    )
            )
        );
        add(ModBlocks.FANCY_CHEST.get(), LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        LootItem.lootTableItem(ModItems.FANCY_CHEST.get())
                                                .apply(RetexturedLootFunction.builder())
                                )
                )
        );

        LootItemCondition.Builder ancientCropCondition = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.ANCIENT_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AncientCropBlock.AGE, AncientCropBlock.MAX_AGE));
        add(ModBlocks.ANCIENT_CROP.get(), createCropDrops(ModBlocks.ANCIENT_CROP.get(), ModItems.ANCIENT_FRUIT.get(),
                ModItems.ANCIENT_SEED.get(), ancientCropCondition));

    }

    private void generateOreBerryTables(DeferredBlock<OreBerryBlock> holder, ResourceLocation berryLoc) {
        add(holder.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(holder.get())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(OreBerryBlock.AGE, OreBerryBlock.MAX_AGE)))
                    .add(LootTableReference.lootTableReference(berryLoc)))
                .withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(holder)))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries()
                .stream().map(DeferredHolder::value)
                .collect(Collectors.toList());
    }

}
