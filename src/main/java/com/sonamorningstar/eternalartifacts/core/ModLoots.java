package com.sonamorningstar.eternalartifacts.core;

import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.data.loot.condition.LootItemBlockTagCondition;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.*;
import com.sonamorningstar.eternalartifacts.loot.function.KeepContentsFunction;
import com.sonamorningstar.eternalartifacts.loot.function.KeepFluidsFunction;
import com.sonamorningstar.eternalartifacts.loot.function.RetexturedLootFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModLoots {
    public static final DeferredRegister<LootItemFunctionType> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_MODIFIER = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MODID);

    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> RETEXTURED_FUNCTION = FUNCTIONS.register("retexture", ()-> new LootItemFunctionType(RetexturedLootFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> KEEP_CONTENTS_FUNCTION = FUNCTIONS.register("keep_contents", ()-> new LootItemFunctionType(KeepContentsFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> KEEP_FLUIDS_FUNCTION = FUNCTIONS.register("keep_fluids", ()-> new LootItemFunctionType(KeepFluidsFunction.CODEC));

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<AddItemListModifier>> ADD_ITEM_LIST_SERIALIZER = register("add_item_list", AddItemListModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<ReplaceItemModifier>> REPLACE_ITEM_SERIALIZER = register("replace_item", ReplaceItemModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<ReplaceItemWithChanceModifier>> REPLACE_ITEM_WITH_CHANCE_SERIALIZER = register("replace_item_with_chance", ReplaceItemWithChanceModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<GlasscutterModifier>> GLASSCUTTER_SERIALIZER = register("glasscutter", GlasscutterModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<GrafterModifier>> GRAFTER_SERIALIZER = register("grafter", GrafterModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<CutlassModifier>> CUTLASS_SERIALIZER = register("cutlass", CutlassModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<HammeringModifier>> HAMMERING_SERIALIZER = register("hammering", HammeringModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<SmeltDropsModifier>> SMELT_DROPS_SERIALIZER = register("smelt_drops", SmeltDropsModifier.CODEC);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> BLOCK_TAG_CONDITION = CONDITIONS.register("block_tag_condition", ()-> new LootItemConditionType(LootItemBlockTagCondition.CODEC));

    private static <G extends IGlobalLootModifier> DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<G>> register(String name, Supplier<Codec<G>> codec) {
        return GLOBAL_MODIFIER.register(name, codec);
    }
}
