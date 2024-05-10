package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockTagsProvider extends net.neoforged.neoforge.common.data.BlockTagsProvider {

    public BlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        //tierAndTool(ModBlocks.LUTFI.get(), "diamond", "axe");
        tierAndTool(ModBlocks.ANVILINATOR.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.BOOK_DUPLICATOR.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.BIOFURNACE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.RESONATOR.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.GARDENING_POT.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.MACHINE_BLOCK.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.SUGAR_CHARCOAL_BLOCK.get(), "wood", "pickaxe");
        tierAndTool(ModBlocks.GRAVEL_COAL_ORE.get(), "wood", "shovel");
        tierAndTool(ModBlocks.GRAVEL_COPPER_ORE.get(), "stone", "shovel");
        tierAndTool(ModBlocks.GRAVEL_IRON_ORE.get(), "stone", "shovel");
        tierAndTool(ModBlocks.GRAVEL_GOLD_ORE.get(), "iron", "shovel");

        tag(BlockTags.BAMBOO_PLANTABLE_ON).add(ModBlocks.GARDENING_POT.get());
        tag(Tags.Blocks.STORAGE_BLOCKS_COAL).add(ModBlocks.SUGAR_CHARCOAL_BLOCK.get());
        tag(BlockTags.SMALL_FLOWERS).add(ModBlocks.FOUR_LEAF_CLOVER.get());
        tag(BlockTags.COAL_ORES).add(ModBlocks.GRAVEL_COAL_ORE.get());
        tag(BlockTags.COPPER_ORES).add(ModBlocks.GRAVEL_COPPER_ORE.get());
        tag(BlockTags.IRON_ORES).add(ModBlocks.GRAVEL_IRON_ORE.get());
        tag(BlockTags.GOLD_ORES).add(ModBlocks.GRAVEL_GOLD_ORE.get());

    }

    private void tierAndTool(Block block, String tier, String tool) {
        switch (tier) {
            case "wood" -> tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(block);
            case "gold" -> tag(Tags.Blocks.NEEDS_GOLD_TOOL).add(block);
            case "stone" -> tag(BlockTags.NEEDS_STONE_TOOL).add(block);
            case "iron" -> tag(BlockTags.NEEDS_IRON_TOOL).add(block);
            case "diamond" -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
            case "netherite" -> tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(block);
        }
        switch (tool) {
            case "sword" -> tag(BlockTags.SWORD_EFFICIENT).add(block);
            case "pickaxe" -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            case "axe" -> tag(BlockTags.MINEABLE_WITH_AXE).add(block);
            case "shovel" -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block);
            case "hoe" -> tag(BlockTags.MINEABLE_WITH_HOE).add(block);
        }
    }

}
