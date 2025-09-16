package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModLootTables;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

public class ModEntityLootSubProvider extends net.minecraft.data.loot.EntityLootSubProvider {
    public ModEntityLootSubProvider() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        add(ModEntities.DEMON_EYE.get(),
                LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.LENS.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 2)))
                    )
                )
        );

        add(ModEntities.PINKY.get(),
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.PINK_SLIME.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 2)))
                    )
                    .add(LootItem.lootTableItem(ModBlocks.ROSY_FROGLIGHT.asItem())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                        .when(killedByFrog())
                    )
                )
        );

        add(ModEntities.MAGICAL_BOOK.get(),
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(Items.PAPER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                    )
                )
        );
        
        add(ModEntities.HONEY_SLIME.get(),
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.HONEY_BOTTLE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 2)))
                    )
                    .add(LootItem.lootTableItem(Blocks.HONEY_BLOCK)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                        .when(killedByFrog())
                    )
                )
        );

        add(ModEntities.DUCK.get(),
            LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ModItems.DUCK_FEATHER)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                )
            )
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ModItems.DUCK_MEAT)
                    .apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))
                    )
                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                )
            )
        );
        add(ModEntities.CHARGED_SHEEP.get(),
                LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootTableReference.lootTableReference(ModEntities.CHARGED_SHEEP.get().getDefaultLootTable()))
        ));
        add(ModEntities.CHARGED_SHEEP.get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.MUTTON)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                            .apply(SmeltItemFunction.smelted()
                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))
                            )
                            .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                        )
                    )
        );
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_BLACK, createChargedSheepTable(Blocks.BLACK_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_BLUE, createChargedSheepTable(Blocks.BLUE_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_BROWN, createChargedSheepTable(Blocks.BROWN_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_CYAN, createChargedSheepTable(Blocks.CYAN_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_GRAY, createChargedSheepTable(Blocks.GRAY_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_GREEN, createChargedSheepTable(Blocks.GREEN_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_LIGHT_BLUE, createChargedSheepTable(Blocks.LIGHT_BLUE_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_LIGHT_GRAY, createChargedSheepTable(Blocks.LIGHT_GRAY_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_LIME, createChargedSheepTable(Blocks.LIME_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_MAGENTA, createChargedSheepTable(Blocks.MAGENTA_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_ORANGE, createChargedSheepTable(Blocks.ORANGE_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_PINK, createChargedSheepTable(Blocks.PINK_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_PURPLE, createChargedSheepTable(Blocks.PURPLE_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_RED, createChargedSheepTable(Blocks.RED_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_WHITE, createChargedSheepTable(Blocks.WHITE_WOOL));
        add(ModEntities.CHARGED_SHEEP.get(), ModLootTables.CHARGED_SHEEP_YELLOW, createChargedSheepTable(Blocks.YELLOW_WOOL));

    }

    protected static LootTable.Builder createChargedSheepTable(ItemLike pWoolItem) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(pWoolItem)))
                .withPool(
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootTableReference.lootTableReference(ModEntities.CHARGED_SHEEP.get().getDefaultLootTable()))
                );
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntities.ENTITY_TYPES.getEntries()
                .stream().map(DeferredHolder::value);
    }
}
