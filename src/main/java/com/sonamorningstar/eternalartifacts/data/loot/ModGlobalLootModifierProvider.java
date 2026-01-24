package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.data.loot.condition.LootItemBlockTagCondition;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.*;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModGlobalLootModifierProvider extends net.neoforged.neoforge.common.data.GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output) {
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
        add("banana_from_jungle_leaves", new AddItemListModifier(
                new LootItemCondition[]{
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.JUNGLE_LEAVES).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                }, List.of(ModItems.BANANA.get().getDefaultInstance()))
        );
        add("ancient_seeds_from_sniffer_digging", new ReplaceItemWithChanceModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(BuiltInLootTables.SNIFFER_DIGGING).build()
                }, ModItems.ANCIENT_SEED.get(), ConstantValue.exactly(1.0F), 0.2F)
        );
        add("replace_vanilla_apple", new ReplaceVanillaAppleModifier(
                new LootItemCondition[] {
                        LootItemBlockTagCondition.builder(BlockTags.LEAVES).build()
                }
        ));
        add("chlorophyte_smithing_template", new AddItemListModifier(
            new LootItemCondition[]{
                LootTableIdCondition.builder(BuiltInLootTables.JUNGLE_TEMPLE).build()
            }, List.of(ModItems.CHLOROPHYTE_UPGRADE_SMITHING_TEMPLATE.toStack())
        ));
        
        
        add("hammering", new HammeringModifier(
                new LootItemCondition[]{
                        MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModTags.Items.TOOLS_HAMMER)).build()
                }
        ));
        add("glasscutter", new GlasscutterModifier(
                new LootItemCondition[] {
                        LootItemBlockTagCondition.builder(ModTags.Blocks.MINEABLE_WITH_GLASSCUTTER).build(),
                        MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModItems.GLASSCUTTER.get())).build()
                }
        ));
        add("grafter", new GrafterModifier(
                new LootItemCondition[] {
                        LootItemBlockTagCondition.builder(ModTags.Blocks.MINEABLE_WITH_GRAFTER).build(),
                        MatchTool.toolMatches(ItemPredicate.Builder.item().of(ModItems.GRAFTER.get())).build()
                }
        ));
        add("ender_dragon_angelic_heart", new AddItemListModifier(
                new LootItemCondition[]{
                    LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS).build(),
                    LootTableIdCondition.builder(EntityType.ENDER_DRAGON.getDefaultLootTable()).build(),
                }, List.of(ModItems.ANGELIC_HEART.get().getDefaultInstance()))
        );
        add("cutlass", new CutlassModifier(new LootItemCondition[] {
            LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS).build(),
        }));
        add("smelting_drops", new SmeltDropsModifier(
            new LootItemCondition[]{
                MatchTool.toolMatches(ItemPredicate.Builder.item()
                    .hasEnchantment(new EnchantmentPredicate(ModEnchantments.MELTING_TOUCH.get(), MinMaxBounds.Ints.atLeast(1)))
                ).build()
            }
        ));
        add("shulker_shell_coloring", new ShulkerShellColoringModifier(
            new LootItemCondition[]{
                LootTableIdCondition.builder(EntityType.SHULKER.getDefaultLootTable()).build()
            }
        ));
        
        final Map<ResourceLocation, Float> charm_tables = Util.make(new HashMap<>(), map -> {
            map.put(BuiltInLootTables.SPAWN_BONUS_CHEST, 0.75f);
            map.put(BuiltInLootTables.SIMPLE_DUNGEON, 0.5f);
            map.put(BuiltInLootTables.ABANDONED_MINESHAFT, 0.5f);
            map.put(BuiltInLootTables.JUNGLE_TEMPLE, 0.5f);
            map.put(BuiltInLootTables.DESERT_PYRAMID, 0.5f);
            map.put(BuiltInLootTables.STRONGHOLD_CROSSING, 0.5f);
            map.put(BuiltInLootTables.STRONGHOLD_LIBRARY, 0.5f);
            map.put(BuiltInLootTables.STRONGHOLD_CORRIDOR, 0.5f);
            map.put(BuiltInLootTables.NETHER_BRIDGE, 0.35f);
            map.put(BuiltInLootTables.END_CITY_TREASURE, 0.85f);
            map.put(BuiltInLootTables.BURIED_TREASURE, 1.0f);
            map.put(BuiltInLootTables.PILLAGER_OUTPOST, 0.5f);
            map.put(BuiltInLootTables.WOODLAND_MANSION, 0.5f);
            map.put(BuiltInLootTables.ANCIENT_CITY, 0.75f);
            map.put(BuiltInLootTables.SHIPWRECK_TREASURE, 0.75f);
            map.put(BuiltInLootTables.SHIPWRECK_SUPPLY, 0.5f);
            map.put(BuiltInLootTables.BASTION_BRIDGE, 0.5f);
            map.put(BuiltInLootTables.BASTION_OTHER, 0.5f);
            map.put(BuiltInLootTables.BASTION_HOGLIN_STABLE, 0.5f);
            map.put(BuiltInLootTables.BASTION_TREASURE, 0.5f);
            map.put(BuiltInLootTables.TRIAL_CHAMBERS_REWARD, 0.85f);
            map.put(BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR, 0.35f);
            map.put(BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION, 0.35f);
            map.put(BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL, 0.35f);
            map.put(BuiltInLootTables.FISHING_TREASURE, 0.25f);
        });
        
        charm_tables.forEach((table, chance) -> {
            add(table.getNamespace() + "_" + table.getPath() + "_random_charm", new AddRandomCharmModifier(
                new LootItemCondition[]{
                    LootTableIdCondition.builder(table).build()
                }, chance
            ));
        });
    }
}
