package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        tag(ModTags.Items.FRUITS).add(ModItems.ORANGE.get(), ModItems.ANCIENT_FRUIT.get());
        tag(ModTags.Items.FRUITS_ORANGE).add(ModItems.ORANGE.get());
        tag(ModTags.Items.FRUITS_ANCIENT).add(ModItems.ANCIENT_FRUIT.get());
        tag(ModTags.Items.GARDENING_POT_SUITABLE).addTags(
                ItemTags.TERRACOTTA,
                ItemTags.STONE_BRICKS,
                ItemTags.STONE_CRAFTING_MATERIALS
        );
        tag(ModTags.Items.GARDENING_POT_SUITABLE).add(
                Blocks.BRICKS.asItem(),
                Blocks.OAK_LOG.asItem(),
                Blocks.QUARTZ_BLOCK.asItem(),
                Blocks.QUARTZ_BRICKS.asItem(),
                Blocks.MELON.asItem(),
                Blocks.PUMPKIN.asItem(),
                Blocks.HONEYCOMB_BLOCK.asItem(),
                Blocks.PRISMARINE.asItem(),
                Blocks.DARK_PRISMARINE.asItem(),
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
