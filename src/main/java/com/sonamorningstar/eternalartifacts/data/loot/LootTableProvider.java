package com.sonamorningstar.eternalartifacts.data.loot;

import com.google.common.collect.ImmutableList;
import com.sonamorningstar.eternalartifacts.core.ModLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Set;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {
    public LootTableProvider(PackOutput pOutput) {
        super(pOutput, ModLootTables.getMOD_LOOTTABLES(), ImmutableList.of(
                new SubProviderEntry(BlockLootSubProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(EntityLootSubProvider::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(GiftLootSubProvider::new, LootContextParamSets.GIFT),
                new SubProviderEntry(ModLootSubProvider::new, LootContextParamSets.EMPTY)
        ));
    }
}
