package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.loot.function.RetexturedLootFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModLoots {
    public static final DeferredRegister<LootItemFunctionType> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);

    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> RETEXTURED_FUNCTION = FUNCTIONS.register("retexture", ()-> new LootItemFunctionType(RetexturedLootFunction.CODEC));
}
