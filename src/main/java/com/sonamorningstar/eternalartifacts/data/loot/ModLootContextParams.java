package com.sonamorningstar.eternalartifacts.data.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ModLootContextParams extends LootContextParams {
    public static final LootContextParam<Boolean> HAMMERED = create("hammered");

    private static <T> LootContextParam<T> create(String id) {
        return new LootContextParam<>(new ResourceLocation(id));
    }
}
