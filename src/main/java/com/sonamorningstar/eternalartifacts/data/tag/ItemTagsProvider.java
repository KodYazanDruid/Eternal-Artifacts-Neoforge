package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {

    public ItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTagProvider, MODID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Items.FRUITS).add(
                ModItems.ORANGE.get(),
                ModItems.ANCIENT_FRUIT.get(),
                ModItems.BANANA.get()
        );
        tag(ModTags.Items.FRUITS_ORANGE).add(ModItems.ORANGE.get());
        tag(ModTags.Items.FRUITS_ANCIENT).add(ModItems.ANCIENT_FRUIT.get());
        tag(ModTags.Items.BANANA).add(ModItems.BANANA.get());

        tag(Tags.Items.INGOTS).add(
                ModItems.RAW_MEAT_INGOT.get(),
                ModItems.MEAT_INGOT.get(),
                ModItems.CHLOROPHYTE_INGOT.get(),
                ModItems.MANGANESE_INGOT.get(),
                ModItems.STEEL_INGOT.get()
        );
        tag(ModTags.Items.INGOTS_MEAT).add(ModItems.MEAT_INGOT.get());
        tag(ModTags.Items.INGOTS_RAW_MEAT).add(ModItems.RAW_MEAT_INGOT.get());
        tag(ModTags.Items.INGOTS_MANGANESE).add(ModItems.MANGANESE_INGOT.get());
        tag(ModTags.Items.INGOTS_STEEL).add(ModItems.STEEL_INGOT.get());
        tag(Tags.Items.NUGGETS).add(
                ModItems.COPPER_NUGGET.get(),
                ModItems.EXPERIENCE_BERRY.get(),
                ModItems.MANGANESE_NUGGET.get(),
                ModItems.STEEL_NUGGET.get()
        );
        tag(ModTags.Items.NUGGETS_COPPER).add(ModItems.COPPER_NUGGET.get());
        tag(ModTags.Items.NUGGETS_EXPERIENCE).add(ModItems.EXPERIENCE_BERRY.get());
        tag(ModTags.Items.NUGGETS_MANGANESE).add(ModItems.MANGANESE_NUGGET.get());
        tag(ModTags.Items.NUGGETS_STEEL).add(ModItems.STEEL_NUGGET.get());

        tag(Tags.Items.SLIMEBALLS).add(ModItems.PINK_SLIME.get());
        tag(ModTags.Items.SLIMEBALLS_PINK).add(ModItems.PINK_SLIME.get());
        tag(ItemTags.COALS).add(ModItems.SUGAR_CHARCOAL.get());
        tag(Tags.Items.STORAGE_BLOCKS_COAL).add(ModBlocks.SUGAR_CHARCOAL_BLOCK.asItem());
        tag(ItemTags.SMALL_FLOWERS).add(ModBlocks.FOUR_LEAF_CLOVER.asItem());
        tag(ItemTags.FLOWERS).add(ModBlocks.FOUR_LEAF_CLOVER.asItem());
        tag(Tags.Items.FEATHERS).add(ModItems.DUCK_FEATHER.get());
        tag(ItemTags.LOGS_THAT_BURN).add(ModBlocks.CITRUS_LOG.asItem(), ModBlocks.STRIPPED_CITRUS_LOG.asItem(), ModBlocks.CITRUS_WOOD.asItem(), ModBlocks.STRIPPED_CITRUS_WOOD.asItem());
        tag(ItemTags.LOGS).add(ModBlocks.CITRUS_LOG.asItem(), ModBlocks.STRIPPED_CITRUS_LOG.asItem(), ModBlocks.CITRUS_WOOD.asItem(), ModBlocks.STRIPPED_CITRUS_WOOD.asItem());
        tag(ItemTags.PLANKS).add(ModBlocks.CITRUS_PLANKS.asItem());
        tag(ModTags.Items.PLASTIC).add(ModItems.PLASTIC_SHEET.get());

        tag(ItemTags.SWORDS).add(
                ModItems.COPPER_SWORD.get(),
                ModItems.SWORD_OF_THE_GREEN_EARTH.get()
        );
        tag(ItemTags.PICKAXES).add(
                ModItems.COPPER_PICKAXE.get(),
                ModItems.CHLOROVEIN_PICKAXE.get()
        );
        tag(ItemTags.AXES).add(
                ModItems.COPPER_AXE.get(),
                ModItems.AXE_OF_REGROWTH.get()
        );
        tag(ItemTags.SHOVELS).add(
                ModItems.COPPER_SHOVEL.get(),
                ModItems.NATURAL_SPADE.get()
        );
        tag(ItemTags.HOES).add(
                ModItems.COPPER_HOE.get(),
                ModItems.LUSH_GRUBBER.get()
        );

        tag(ModTags.Items.GARDENING_POT_SUITABLE).addTags(
                ItemTags.TERRACOTTA,
                ItemTags.STONE_BRICKS,
                ItemTags.STONE_CRAFTING_MATERIALS,
                Tags.Items.SANDSTONE
        );
        tag(ModTags.Items.TABLETS).add(
                ModItems.STONE_TABLET.get(),
                ModItems.ENDER_TABLET.get(),
                ModItems.CHLOROPHYTE_TABLET.get(),
                ModItems.COPPER_TABLET.get()
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
    }
}
