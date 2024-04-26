package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.AddItemModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class GlobalLootModifierProvider extends net.neoforged.neoforge.common.data.GlobalLootModifierProvider {
    public GlobalLootModifierProvider(PackOutput output) {
        super(output, MODID);
    }

    @Override
    protected void start() {
        add("orange_from_acacia_leaves", new AddItemModifier(
                new LootItemCondition[]{
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.ACACIA_LEAVES).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build(),
                }, ModItems.ORANGE.get())
        );
    }
}
