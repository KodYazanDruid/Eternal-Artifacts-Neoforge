package com.sonamorningstar.eternalartifacts.core;

import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.data.loot.condition.LootItemBlockTagCondition;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.*;
import com.sonamorningstar.eternalartifacts.loot.function.KeepFluidsFunction;
import com.sonamorningstar.eternalartifacts.loot.function.RetexturedLootFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModLoots {
    public static final DeferredRegister<LootItemFunctionType> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_MODIFIER = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MODID);

    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> RETEXTURED_FUNCTION = FUNCTIONS.register("retexture", ()-> new LootItemFunctionType(RetexturedLootFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> KEEP_FLUIDS_FUNCTION = FUNCTIONS.register("keep_fluids", ()-> new LootItemFunctionType(KeepFluidsFunction.CODEC));

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<? extends IGlobalLootModifier>> ADD_ITEM_LIST_SERIALIZER = GLOBAL_MODIFIER.register("add_item_list", AddItemListModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<? extends IGlobalLootModifier>> REPLACE_ITEM_SERIALIZER = GLOBAL_MODIFIER.register("replace_item", ReplaceItemModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<? extends IGlobalLootModifier>> REPLACE_ITEM_WITH_CHANCE_SERIALIZER = GLOBAL_MODIFIER.register("replace_item_with_chance", ReplaceItemWithChanceModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<? extends IGlobalLootModifier>> GLASSCUTTER_SERIALIZER = GLOBAL_MODIFIER.register("glasscutter", GlasscutterModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<? extends IGlobalLootModifier>> GRAFTER_SERIALIZER = GLOBAL_MODIFIER.register("grafter", GrafterModifier.CODEC);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> BLOCK_TAG_CONDITION = CONDITIONS.register("block_tag_condition", ()-> new LootItemConditionType(LootItemBlockTagCondition.CODEC));

}
