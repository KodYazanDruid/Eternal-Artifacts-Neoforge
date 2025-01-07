package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.data.loot.ModLootContextParams;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class HammeringModifier extends LootModifier {
    public static Set<TagKey<Block>> gatheredTags = new HashSet<>();

    public static final Supplier<Codec<HammeringModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(instance ->
            codecStart(instance).apply(instance, HammeringModifier::new)));

    public HammeringModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.params.hasParam(ModLootContextParams.HAMMERED) && context.getParam(ModLootContextParams.HAMMERED)) return generatedLoot;
        for(LootItemCondition condition : conditions) if(!condition.test(context)) return generatedLoot;
        ItemStack tool = context.getParam(LootContextParams.TOOL);
        if (tool.isEmpty() || EnchantmentHelper.hasSilkTouch(tool)) return generatedLoot;
        BlockState blockState = context.getParam(LootContextParams.BLOCK_STATE);
        ServerLevel level = context.getLevel();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
        ResourceLocation targetTable = new ResourceLocation(MODID, "hammering/blocks/" + id.getNamespace() + "/" + id.getPath());
        LootTable table = LootTableHelper.getTable(level, targetTable);
        if (table == LootTable.EMPTY) {
            for (TagKey<Block> tag : gatheredTags) {
                if (blockState.is(tag)) {
                    ResourceLocation loc = tag.location();
                    table = LootTableHelper.getTable(level, new ResourceLocation(MODID, "hammering/tags/"+loc.getNamespace()+"/"+loc.getPath()));
                    break;
                }
            }
        }
        LootParams newParams = new LootParams(context.getLevel(), context.params.params, context.params.dynamicDrops, context.getLuck());
        newParams.params.put(ModLootContextParams.HAMMERED, true);
        return table != LootTable.EMPTY ? table.getRandomItems(newParams) : generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
