package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

public class EntityLootSubProvider extends net.minecraft.data.loot.EntityLootSubProvider {
    public EntityLootSubProvider() {
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




    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntities.ENTITY_TYPES.getEntries()
                .stream().map(DeferredHolder::value);
    }
}
