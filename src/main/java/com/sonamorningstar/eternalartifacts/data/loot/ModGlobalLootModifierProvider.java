package com.sonamorningstar.eternalartifacts.data.loot;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.data.loot.condition.LootItemBlockTagCondition;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.*;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;

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
        add("cutlass", new CutlassModifier(new LootItemCondition[] {}));
        add("smelting_drops", new SmeltDropsModifier(
            new LootItemCondition[]{
                MatchTool.toolMatches(ItemPredicate.Builder.item()
                    .hasEnchantment(new EnchantmentPredicate(ModEnchantments.MELTING_TOUCH.get(), MinMaxBounds.Ints.atLeast(1)))
                ).build()
            }
        ));
    }
}
