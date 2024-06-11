package com.sonamorningstar.eternalartifacts.core;

import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.AddItemListModifier;
import com.sonamorningstar.eternalartifacts.loot.function.JarKeepFluidFunction;
import com.sonamorningstar.eternalartifacts.loot.function.RetexturedLootFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModLoots {
    public static final DeferredRegister<LootItemFunctionType> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_MODIFIER = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> RETEXTURED_FUNCTION = FUNCTIONS.register("retexture", ()-> new LootItemFunctionType(RetexturedLootFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> JAR_KEEP_FUNCTION = FUNCTIONS.register("jar_keep", ()-> new LootItemFunctionType(JarKeepFluidFunction.CODEC));

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<? extends IGlobalLootModifier>> ADD_ITEM_SERIALIZER = GLOBAL_MODIFIER.register("add_item", AddItemListModifier.CODEC);

}
