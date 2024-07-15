package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.AddItemListModifier;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.ReplaceItemModifier;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.ReplaceItemWithChanceModifier;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
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
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                }, List.of(ModItems.ORANGE.get().getDefaultInstance()))
        );

        add("ancient_seeds_from_sniffer_digging", new ReplaceItemWithChanceModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(new ResourceLocation("gameplay/sniffer_digging")).build()
                }, ModItems.ANCIENT_SEED.get(), ConstantValue.exactly(1.0F), 0.2F)
        );

        add("coal_dust_from_coal_block", new ReplaceItemModifier(
                new LootItemCondition[] {
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.COAL_BLOCK).build(),
                        MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModTags.Items.TOOLS_HAMMER)).build(),
                }, ModItems.COAL_DUST.get(), UniformGenerator.between(3.0F, 6.0F))
        );

    }
}
