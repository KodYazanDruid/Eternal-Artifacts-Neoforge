package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.content.block.AncientCropBlock;
import com.sonamorningstar.eternalartifacts.content.block.OreBerryBlock;
import com.sonamorningstar.eternalartifacts.content.block.PunjiBlock;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.loot.function.KeepFluidsFunction;
import com.sonamorningstar.eternalartifacts.loot.function.RetexturedLootFunction;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModBlockLootSubProvider extends net.minecraft.data.loot.BlockLootSubProvider {

    public ModBlockLootSubProvider() {
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
        dropSelf(ModBlocks.CHLOROPHYTE_DEBRIS.get());
        dropSelf(ModBlocks.SANDY_TILED_STONE_BRICKS.get());
        dropSelf(ModBlocks.SANDY_STONE_BRICKS.get());
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
        dropSelf(ModBlocks.FLUID_COMBUSTION_DYNAMO.get());
        dropSelf(ModBlocks.RAW_MANGANESE_BLOCK.get());
        dropSelf(ModBlocks.RAW_ARDITE_BLOCK.get());
        dropSelf(ModBlocks.ARDITE_BLOCK.get());
        dropOther(ModBlocks.PLASTIC_CAULDRON.get(), Blocks.CAULDRON);
        dropOther(ModBlocks.BLUE_PLASTIC_CAULDRON.get(), Blocks.CAULDRON);
        dropSelf(ModBlocks.SNOW_BRICKS.get());
        dropSelf(ModBlocks.ICE_BRICKS.get());
        dropSelf(ModBlocks.ASPHALT_BLOCK.get());
        dropSelf(ModBlocks.COPPER_CABLE.get());
        dropSelf(ModBlocks.COVERED_COPPER_CABLE.get());
        dropSelf(ModBlocks.STEEL_BLOCK.get());

        generateOreBerryTables(ModBlocks.COPPER_ORE_BERRY, ModLootTables.COPPER_OREBERRY_HARVEST);
        generateOreBerryTables(ModBlocks.IRON_ORE_BERRY, ModLootTables.IRON_OREBERRY_HARVEST);
        generateOreBerryTables(ModBlocks.GOLD_ORE_BERRY, ModLootTables.GOLD_OREBERRY_HARVEST);
        generateOreBerryTables(ModBlocks.EXPERIENCE_ORE_BERRY, ModLootTables.EXPERIENCE_OREBERRY_HARVEST);
        generateOreBerryTables(ModBlocks.MANGANESE_ORE_BERRY, ModLootTables.MANGANESE_OREBERRY_HARVEST);

        add(ModBlocks.GRAVEL_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(ModBlocks.GRAVEL_COPPER_ORE.get(), this::createCopperOreDrops);
        add(ModBlocks.GRAVEL_IRON_ORE.get(), block -> createOreDrop(block, Items.RAW_IRON));
        add(ModBlocks.GRAVEL_GOLD_ORE.get(), block -> createOreDrop(block, Items.RAW_GOLD));
        add(ModBlocks.MANGANESE_ORE.get(), block -> createOreDrop(block, ModItems.RAW_MANGANESE.get()));
        add(ModBlocks.DEEPSLATE_MANGANESE_ORE.get(), block -> createOreDrop(block, ModItems.RAW_MANGANESE.get()));
        add(ModBlocks.ARDITE_ORE.get(), block -> createOreDrop(block, ModItems.RAW_ARDITE.get()));

        dropSelfWithFunction(ModBlocks.GARDENING_POT, RetexturedLootFunction.builder());
        dropSelfWithFunction(ModBlocks.FANCY_CHEST, RetexturedLootFunction.builder());
        dropSelfWithFunction(ModBlocks.JAR, KeepFluidsFunction.builder());
        dropSelfWithFunction(ModBlocks.NOUS_TANK, KeepFluidsFunction.builder());
        dropSelfWithFunction(ModBlocks.COPPER_DRUM, KeepFluidsFunction.builder());
        dropSelfWithFunction(ModBlocks.IRON_DRUM, KeepFluidsFunction.builder());
        dropSelfWithFunction(ModBlocks.GOLD_DRUM, KeepFluidsFunction.builder());
        dropSelfWithFunction(ModBlocks.STEEL_DRUM, KeepFluidsFunction.builder());
        dropSelfWithFunction(ModBlocks.DIAMOND_DRUM, KeepFluidsFunction.builder());
        dropSelfWithFunction(ModBlocks.NETHERITE_DRUM, KeepFluidsFunction.builder());

        LootItemCondition.Builder ancientCropCondition = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.ANCIENT_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AncientCropBlock.AGE, AncientCropBlock.MAX_AGE));
        add(ModBlocks.ANCIENT_CROP.get(), createCropDrops(ModBlocks.ANCIENT_CROP.get(), ModItems.ANCIENT_FRUIT.get(),
                ModItems.ANCIENT_SEED.get(), ancientCropCondition));

        add(ModBlocks.SUGAR_CHARCOAL_BLOCK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModTags.Items.TOOLS_HAMMER)))
                        .add(LootItem.lootTableItem(ModItems.SUGAR_CHARCOAL_DUST))
                        .setRolls(UniformGenerator.between(3.0F, 6.0F))
                )
                .withPool(LootPool.lootPool()
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModTags.Items.TOOLS_HAMMER)).invert())
                        .add(LootItem.lootTableItem(ModBlocks.SUGAR_CHARCOAL_BLOCK))
                        .setRolls(ConstantValue.exactly(1.0F))
                )
        );
        add(ModBlocks.CHARCOAL_BLOCK.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModTags.Items.TOOLS_HAMMER)))
                        .add(LootItem.lootTableItem(ModItems.CHARCOAL_DUST))
                        .setRolls(UniformGenerator.between(3.0F, 6.0F))
                )
                .withPool(LootPool.lootPool()
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModTags.Items.TOOLS_HAMMER)).invert())
                        .add(LootItem.lootTableItem(ModBlocks.CHARCOAL_BLOCK))
                        .setRolls(ConstantValue.exactly(1.0F))
                )
        );

        add(ModBlocks.PUNJI_STICKS.get(), LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(applyExplosionDecay(
                        ModBlocks.PUNJI_STICKS.get(),
                        LootItem.lootTableItem(ModBlocks.PUNJI_STICKS.get())
                            .apply(
                                IntStream.rangeClosed(1, 5).boxed().toList(),
                                count -> SetItemCountFunction.setCount(ConstantValue.exactly((float) count))
                                    .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.PUNJI_STICKS.get())
                                            .setProperties(
                                                StatePropertiesPredicate.Builder.properties().hasProperty(PunjiBlock.STICKS, count)
                                            )
                                    )
                            )
                        )
                    )
            )
        );

        ModMachines.MACHINES.getMachines().forEach(holder -> dropSelf(holder.getBlock()));
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

    private void addLootPool(DeferredBlock<?> holder, LootPoolEntryContainer.Builder<?> builder) {
        add(holder.get(), LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(builder)
            )
        );
    }

    private void dropSelfWithFunction(DeferredBlock<?> holder, LootItemConditionalFunction.Builder<?> builder) {
        addLootPool(holder, LootItem.lootTableItem(holder).apply(builder));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> knownBlocks = ModBlocks.BLOCKS.getEntries()
                .stream().map(DeferredHolder::value)
                .collect(Collectors.toList());
        knownBlocks.addAll(ModMachines.MACHINES.getBlockHolders().stream().map(DeferredHolder::get).toList());
        return knownBlocks;
    }

}
