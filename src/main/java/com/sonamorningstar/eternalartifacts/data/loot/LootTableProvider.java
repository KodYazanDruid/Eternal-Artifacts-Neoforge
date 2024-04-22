package com.sonamorningstar.eternalartifacts.data.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Set;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {
    public LootTableProvider(PackOutput pOutput) {
        super(pOutput, Set.of(), ImmutableList.of(
                new SubProviderEntry(BlockLootSubProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(EntityLootSubProvider::new, LootContextParamSets.ENTITY)
        ));
    }
}
