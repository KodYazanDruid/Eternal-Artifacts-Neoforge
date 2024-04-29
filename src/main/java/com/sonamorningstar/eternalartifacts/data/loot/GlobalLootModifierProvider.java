package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.AddItemListModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class GlobalLootModifierProvider extends net.neoforged.neoforge.common.data.GlobalLootModifierProvider {
    public GlobalLootModifierProvider(PackOutput output) {
        super(output, MODID);
    }

    @Override
    protected void start() {
        add("orange_from_acacia_leaves", new AddItemListModifier(
                new LootItemCondition[]{
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.ACACIA_LEAVES).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build(),
                }, List.of(ModItems.ORANGE.get()))
        );

        add("ancient_seeds_from_sniffer_digging", new AddItemListModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(new ResourceLocation("gameplay/sniffer_digging")).build()
                }, List.of(ModItems.ANCIENT_SEED.get()))
        );

    }
}
