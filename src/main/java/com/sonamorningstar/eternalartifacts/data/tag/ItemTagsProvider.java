package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.compat.ModHooks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {

    public ItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTagProvider, MODID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.FRUITS).add(
            ModItems.ORANGE.get(),
            ModItems.ANCIENT_FRUIT.get(),
            ModItems.BANANA.get()
        );
        tag(ModTags.Items.FRUITS).addTag(ModTags.Items.FRUITS_APPLE);
        tag(ModTags.Items.FRUITS_ORANGE).add(ModItems.ORANGE.get());
        tag(ModTags.Items.FRUITS_ANCIENT).add(ModItems.ANCIENT_FRUIT.get());
        tag(ModTags.Items.FRUITS_BANANA).add(ModItems.BANANA.get());
        tag(ModTags.Items.FRUITS_APPLE).add(
            Items.APPLE,
            ModItems.GREEN_APPLE.get(),
            ModItems.YELLOW_APPLE.get()
        );

        tag(Tags.Items.INGOTS).add(
            ModItems.RAW_MEAT_INGOT.get(),
            ModItems.COOKED_MEAT_INGOT.get(),
            ModItems.CHLOROPHYTE_INGOT.get(),
            ModItems.MANGANESE_INGOT.get(),
            ModItems.STEEL_INGOT.get(),
            ModItems.MARIN_INGOT.get()
        );
        tag(ModTags.Items.INGOTS_MEAT).add(ModItems.COOKED_MEAT_INGOT.get());
        tag(ModTags.Items.INGOTS_RAW_MEAT).add(ModItems.RAW_MEAT_INGOT.get());
        tag(ModTags.Items.INGOTS_MANGANESE).add(ModItems.MANGANESE_INGOT.get());
        tag(ModTags.Items.INGOTS_STEEL).add(ModItems.STEEL_INGOT.get());
        tag(Tags.Items.NUGGETS).add(
            ModItems.COPPER_NUGGET.get(),
            ModItems.EXPERIENCE_BERRY.get(),
            ModItems.MANGANESE_NUGGET.get(),
            ModItems.STEEL_NUGGET.get()
        );
        tag(Tags.Items.DUSTS).add(
            ModItems.COAL_DUST.get(),
            ModItems.CHARCOAL_DUST.get(),
            ModItems.SUGAR_CHARCOAL_DUST.get()
        );
        tag(ModTags.Items.DUSTS_COAL).add(ModItems.COAL_DUST.get());
        tag(ModTags.Items.DUSTS_CHARCOAL).add(ModItems.CHARCOAL_DUST.get());
        tag(ModTags.Items.DUSTS_SUGAR_CHARCOAL).add(ModItems.SUGAR_CHARCOAL_DUST.get());
        tag(Tags.Items.RAW_MATERIALS).add(
            ModItems.RAW_MANGANESE.get(),
            ModItems.RAW_MARIN.get()
        );
        tag(ModTags.Items.NUGGETS_COPPER).add(ModItems.COPPER_NUGGET.get());
        tag(ModTags.Items.NUGGETS_EXPERIENCE).add(ModItems.EXPERIENCE_BERRY.get());
        tag(ModTags.Items.NUGGETS_MANGANESE).add(ModItems.MANGANESE_NUGGET.get());
        tag(ModTags.Items.NUGGETS_STEEL).add(ModItems.STEEL_NUGGET.get());
        copy(ModTags.Blocks.STORAGE_BLOCKS_STEEL, ModTags.Items.STORAGE_BLOCKS_STEEL);

        tag(Tags.Items.ORES_COAL).add(ModBlocks.GRAVEL_COAL_ORE.asItem());
        tag(Tags.Items.ORES_COPPER).add(ModBlocks.GRAVEL_COPPER_ORE.asItem());
        tag(Tags.Items.ORES_IRON).add(ModBlocks.GRAVEL_IRON_ORE.asItem());
        tag(Tags.Items.ORES_GOLD).add(ModBlocks.GRAVEL_GOLD_ORE.asItem());

        tag(Tags.Items.SLIMEBALLS).add(ModItems.PINK_SLIME.get());
        tag(ModTags.Items.SLIMEBALLS_PINK).add(ModItems.PINK_SLIME.get());
        tag(ItemTags.COALS).add(ModItems.SUGAR_CHARCOAL.get());
        tag(Tags.Items.STORAGE_BLOCKS_COAL).add(ModBlocks.SUGAR_CHARCOAL_BLOCK.asItem());
        tag(ItemTags.SMALL_FLOWERS).add(ModBlocks.FOUR_LEAF_CLOVER.asItem(), ModBlocks.TIGRIS_FLOWER.asItem());
        tag(ItemTags.FLOWERS).add(ModBlocks.FOUR_LEAF_CLOVER.asItem(), ModBlocks.TIGRIS_FLOWER.asItem());
        tag(Tags.Items.FEATHERS).add(ModItems.DUCK_FEATHER.get());
        tag(ItemTags.LOGS_THAT_BURN).add(ModBlocks.CITRUS_LOG.asItem(), ModBlocks.STRIPPED_CITRUS_LOG.asItem(), ModBlocks.CITRUS_WOOD.asItem(), ModBlocks.STRIPPED_CITRUS_WOOD.asItem());
        tag(ItemTags.LOGS).add(ModBlocks.CITRUS_LOG.asItem(), ModBlocks.STRIPPED_CITRUS_LOG.asItem(), ModBlocks.CITRUS_WOOD.asItem(), ModBlocks.STRIPPED_CITRUS_WOOD.asItem());
        tag(ItemTags.PLANKS).add(ModBlocks.CITRUS_PLANKS.asItem());
        tag(ModTags.Items.PLASTIC).add(ModItems.PLASTIC_SHEET.get());
        tag(ModTags.Items.FLOUR_WHEAT).add(ModItems.FLOUR.get());
        tag(ModTags.Items.DUSTS_FLOUR).addTag(ModTags.Items.FLOUR_WHEAT);
        tag(ModTags.Items.DOUGH_WHEAT).add(ModItems.DOUGH.get());
        tag(ModTags.Items.DOUGH).addTag(ModTags.Items.DOUGH_WHEAT);
        tag(ModTags.Items.DUSTS_CLAY).add(ModItems.CLAY_DUST.get());
        tag(ItemTags.PIGLIN_LOVED).add(ModItems.GOLD_RING.get());
        
        tag(Tags.Items.HEADS).add(
            ModItems.DROWNED_HEAD.get(),
            ModItems.HUSK_HEAD.get(),
            ModItems.STRAY_SKULL.get()
        );

        tag(ItemTags.SWORDS).add(
            ModItems.COPPER_SWORD.get(),
            ModItems.SWORD_OF_THE_GREEN_EARTH.get(),
            ModItems.STEEL_SWORD.get()
        );
        tag(ItemTags.SWORDS).addTags(ModTags.Items.TOOLS_CUTLASS);
        tag(ItemTags.PICKAXES).add(
            ModItems.COPPER_PICKAXE.get(),
            ModItems.CHLOROVEIN_PICKAXE.get(),
            ModItems.STEEL_PICKAXE.get()
        );
        tag(ItemTags.AXES).add(
            ModItems.COPPER_AXE.get(),
            ModItems.AXE_OF_REGROWTH.get(),
            ModItems.STEEL_AXE.get()
        );
        tag(ItemTags.SHOVELS).add(
            ModItems.COPPER_SHOVEL.get(),
            ModItems.NATURAL_SPADE.get(),
            ModItems.STEEL_SHOVEL.get()
        );
        tag(ItemTags.HOES).add(
            ModItems.COPPER_HOE.get(),
            ModItems.LUSH_GRUBBER.get(),
            ModItems.STEEL_HOE.get()
        );
        tag(ModTags.Items.TOOLS_WRENCH).add(ModItems.WRENCH.get());
        tag(ModTags.Items.TOOLS_HAMMER).add(
            ModItems.WOODEN_HAMMER.get(),
            ModItems.STONE_HAMMER.get(),
            ModItems.COPPER_HAMMER.get(),
            ModItems.IRON_HAMMER.get(),
            ModItems.GOLDEN_HAMMER.get(),
            ModItems.DIAMOND_HAMMER.get(),
            ModItems.NETHERITE_HAMMER.get(),
            ModItems.HAMMAXE.get(),
            ModItems.STEEL_HAMMER.get()
    );
        tag(ModTags.Items.TOOLS_CUTLASS).add(
            ModItems.WOODEN_CUTLASS.get(),
            ModItems.STONE_CUTLASS.get(),
            ModItems.COPPER_CUTLASS.get(),
            ModItems.IRON_CUTLASS.get(),
            ModItems.GOLDEN_CUTLASS.get(),
            ModItems.DIAMOND_CUTLASS.get(),
            ModItems.NETHERITE_CUTLASS.get(),
            ModItems.CHLOROPHYTE_CUTLASS.get(),
            ModItems.STONE_CUTLASS.get(),
            ModItems.STEEL_CUTLASS.get()
        );
        tag(ModTags.Items.TOOLS_SICKLE).add(
            ModItems.WOODEN_SICKLE.get(),
            ModItems.STONE_SICKLE.get(),
            ModItems.COPPER_SICKLE.get(),
            ModItems.IRON_SICKLE.get(),
            ModItems.GOLDEN_SICKLE.get(),
            ModItems.DIAMOND_SICKLE.get(),
            ModItems.NETHERITE_SICKLE.get(),
            ModItems.CHLOROPHYTE_SICKLE.get(),
            ModItems.STEEL_SICKLE.get()
        );
        tag(ModTags.Items.TABLETS).add(
            ModItems.STONE_TABLET.get(),
            ModItems.ENDER_TABLET.get(),
            ModItems.CHLOROPHYTE_TABLET.get(),
            ModItems.COPPER_TABLET.get()
        );

        tag(ModTags.Items.CHARMS_HEAD).add(
            Items.TURTLE_HELMET,
            Items.DISPENSER,
            Items.BUCKET,
            Items.CARVED_PUMPKIN,
            Items.OBSERVER,
            Items.PIGLIN_HEAD
        );
        tag(ModTags.Items.CHARMS_HEAD).addTags(
            ModTags.Items.SHULKER_SHELL,
            Tags.Items.GLASS,
            ItemTags.BANNERS,
            Tags.Items.HEADS
        );
        tag(ModTags.Items.CHARMS_NECKLACE).add(
            ModItems.HEART_NECKLACE.get(),
            ModItems.SAGES_TALISMAN.get()
        );
        tag(ModTags.Items.CHARMS_HAND).add(
            ModItems.POWER_GAUNTLET.get()
        );
        tag(ModTags.Items.CHARMS_RING).add(
            ModItems.GOLD_RING.get(),
            ModItems.BAND_OF_ARCANE.get(),
            ModItems.EMERALD_SIGNET.get()
        );
        tag(ModTags.Items.CHARMS_BRACELET).add(
            Items.CLOCK,
            Items.COMPASS,
            Items.RECOVERY_COMPASS
        );
        tag(ModTags.Items.CHARMS_BACK).add(
            ModItems.KNAPSACK.get(),
            ModItems.TANK_KNAPSACK.get(),
            ModItems.ENDER_KNAPSACK.get(),
            Items.ELYTRA,
            ModItems.PORTABLE_BATTERY.get()
        );
        tag(ModTags.Items.CHARM_FEET).add(
            ModItems.COMFY_SHOES.get(),
            ModItems.FROG_LEGS.get(),
            ModItems.SKYBOUND_TREADS.get()
        );
        tag(ModTags.Items.CHARMS_BELT).add(
            Items.FILLED_MAP,
            Items.BUNDLE,
            ModItems.GALE_SASH.get()
        );
        tag(ModTags.Items.CHARMS_CHARM).add(
            ModItems.MAGIC_FEATHER.get(),
            ModItems.PORTABLE_CRAFTER.get(),
            ModItems.HOLY_DAGGER.get(),
            ModItems.ENCUMBATOR.get(),
            ModItems.MEDKIT.get(),
            Items.RABBIT_FOOT,
            Items.COD,
            Items.TOTEM_OF_UNDYING
        );
        /*tag(ModTags.Items.CHARMS_WILDCARD_BLACKLISTED).add(
        );*/
        tag(ModTags.Items.CHARMS).addTags(
            ModTags.Items.CHARMS_HEAD,
            ModTags.Items.CHARMS_NECKLACE,
            ModTags.Items.CHARMS_HAND,
            ModTags.Items.CHARMS_RING,
            ModTags.Items.CHARMS_BRACELET,
            ModTags.Items.CHARMS_BELT,
            ModTags.Items.CHARMS_BACK,
            ModTags.Items.CHARM_FEET,
            ModTags.Items.CHARMS_CHARM
        );

        tag(ModTags.Items.SHULKER_SHELL).add(
                ModItems.WHITE_SHULKER_SHELL.get(),
                ModItems.ORANGE_SHULKER_SHELL.get(),
                ModItems.MAGENTA_SHULKER_SHELL.get(),
                ModItems.LIGHT_BLUE_SHULKER_SHELL.get(),
                ModItems.YELLOW_SHULKER_SHELL.get(),
                ModItems.LIME_SHULKER_SHELL.get(),
                ModItems.PINK_SHULKER_SHELL.get(),
                ModItems.GRAY_SHULKER_SHELL.get(),
                ModItems.LIGHT_GRAY_SHULKER_SHELL.get(),
                ModItems.CYAN_SHULKER_SHELL.get(),
                ModItems.PURPLE_SHULKER_SHELL.get(),
                ModItems.BLUE_SHULKER_SHELL.get(),
                ModItems.BROWN_SHULKER_SHELL.get(),
                ModItems.GREEN_SHULKER_SHELL.get(),
                ModItems.RED_SHULKER_SHELL.get(),
                ModItems.BLACK_SHULKER_SHELL.get(),
                Items.SHULKER_SHELL
        );

        tag(ModTags.Items.GARDENING_POT_SUITABLE).addTags(
                ItemTags.TERRACOTTA,
                ItemTags.STONE_BRICKS,
                ItemTags.STONE_CRAFTING_MATERIALS,
                Tags.Items.SANDSTONE,
                Tags.Items.COBBLESTONE,
                Tags.Items.COBBLESTONE_MOSSY,
                Tags.Items.COBBLESTONE_DEEPSLATE
        );
        tag(ModTags.Items.GARDENING_POT_SUITABLE).add(
                Blocks.BRICKS.asItem(),
                Blocks.QUARTZ_BLOCK.asItem(),
                Blocks.QUARTZ_BRICKS.asItem(),
                Blocks.MELON.asItem(),
                Blocks.PUMPKIN.asItem(),
                Blocks.HONEYCOMB_BLOCK.asItem(),
                Blocks.PRISMARINE.asItem(),
                Blocks.DARK_PRISMARINE.asItem(),
                Blocks.SHROOMLIGHT.asItem(),
                Blocks.GLOWSTONE.asItem(),
                Blocks.RED_MUSHROOM_BLOCK.asItem(),
                Blocks.BROWN_MUSHROOM_BLOCK.asItem(),
                Blocks.MUSHROOM_STEM.asItem(),
                Blocks.OCHRE_FROGLIGHT.asItem(),
                Blocks.VERDANT_FROGLIGHT.asItem(),
                Blocks.PEARLESCENT_FROGLIGHT.asItem(),
                ModBlocks.ROSY_FROGLIGHT.asItem(),
                ModBlocks.SNOW_BRICKS.asItem(),
                ModBlocks.OBSIDIAN_BRICKS.asItem(),
                //Glazed terracottas
                Blocks.WHITE_GLAZED_TERRACOTTA.asItem(),
                Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(),
                Blocks.GRAY_GLAZED_TERRACOTTA.asItem(),
                Blocks.BLACK_GLAZED_TERRACOTTA.asItem(),
                Blocks.BROWN_GLAZED_TERRACOTTA.asItem(),
                Blocks.RED_GLAZED_TERRACOTTA.asItem(),
                Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(),
                Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(),
                Blocks.LIME_GLAZED_TERRACOTTA.asItem(),
                Blocks.GREEN_GLAZED_TERRACOTTA.asItem(),
                Blocks.CYAN_GLAZED_TERRACOTTA.asItem(),
                Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(),
                Blocks.BLUE_GLAZED_TERRACOTTA.asItem(),
                Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(),
                Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(),
                Blocks.PINK_GLAZED_TERRACOTTA.asItem(),
                //Logs
                Blocks.OAK_LOG.asItem(),
                Blocks.STRIPPED_OAK_LOG.asItem(),
                Blocks.SPRUCE_LOG.asItem(),
                Blocks.STRIPPED_SPRUCE_LOG.asItem(),
                Blocks.BIRCH_LOG.asItem(),
                Blocks.STRIPPED_BIRCH_LOG.asItem(),
                Blocks.JUNGLE_LOG.asItem(),
                Blocks.STRIPPED_JUNGLE_LOG.asItem(),
                Blocks.ACACIA_LOG.asItem(),
                Blocks.STRIPPED_ACACIA_LOG.asItem(),
                Blocks.DARK_OAK_LOG.asItem(),
                Blocks.STRIPPED_DARK_OAK_LOG.asItem(),
                Blocks.MANGROVE_LOG.asItem(),
                Blocks.STRIPPED_MANGROVE_LOG.asItem(),
                Blocks.CHERRY_LOG.asItem(),
                Blocks.STRIPPED_CHERRY_LOG.asItem(),
                Blocks.BAMBOO_BLOCK.asItem(),
                Blocks.STRIPPED_BAMBOO_BLOCK.asItem(),
                Blocks.CRIMSON_STEM.asItem(),
                Blocks.STRIPPED_CRIMSON_STEM.asItem(),
                Blocks.WARPED_STEM.asItem(),
                Blocks.STRIPPED_WARPED_STEM.asItem()
        );
        ModHooks.ItemTagAppender.itemTags.forEach((tagKey, itemSupList) -> itemSupList.stream().map(Supplier::get).forEach(item -> {
            ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item);
            tag(tagKey).addOptional(rl);
        }));
        ModHooks.ItemTagAppender.tagKeyTags.forEach((tagKey, tagKeys) -> tagKeys.forEach(keys -> tag(tagKey).addOptionalTag(keys)));
    }
}
