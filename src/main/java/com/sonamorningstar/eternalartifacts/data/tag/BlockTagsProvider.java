package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModBlockFamilies;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockTagsProvider extends net.neoforged.neoforge.common.data.BlockTagsProvider {

    public BlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MODID, existingFileHelper);
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tierAndTool(ModBlocks.ANVILINATOR.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.BOOK_DUPLICATOR.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.BIOFURNACE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.RESONATOR.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.MACHINE_BLOCK.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.SUGAR_CHARCOAL_BLOCK.get(), "", "pickaxe");
        tierAndTool(ModBlocks.GRAVEL_MANGANESE_ORE.get(), "stone", "shovel");
        tierAndTool(ModBlocks.GRAVEL_COAL_ORE.get(), "wooden", "shovel");
        tierAndTool(ModBlocks.GRAVEL_COPPER_ORE.get(), "stone", "shovel");
        tierAndTool(ModBlocks.GRAVEL_IRON_ORE.get(), "stone", "shovel");
        tierAndTool(ModBlocks.GRAVEL_GOLD_ORE.get(), "iron", "shovel");
        tierAndTool(ModBlocks.GRAVEL_DIAMOND_ORE.get(), "iron", "shovel");
        tierAndTool(ModBlocks.GRAVEL_EMERALD_ORE.get(), "iron", "shovel");
        tierAndTool(ModBlocks.GRAVEL_REDSTONE_ORE.get(), "iron", "shovel");
        tierAndTool(ModBlocks.GRAVEL_LAPIS_ORE.get(), "stone", "shovel");
        tierAndTool(ModBlocks.SANDY_TILED_STONE_BRICKS.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.SANDY_STONE_BRICKS.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.SANDY_PRISMARINE.get(), "", "pickaxe");
        tierAndTool(ModBlocks.VERY_SANDY_PRISMARINE.get(), "", "pickaxe");
        tierAndTool(ModBlocks.SANDY_DARK_PRISMARINE.get(), "", "pickaxe");
        tierAndTool(ModBlocks.VERY_SANDY_DARK_PRISMARINE.get(), "", "pickaxe");
        tierAndTool(ModBlocks.PAVED_PRISMARINE_BRICKS.get(), "", "pickaxe");
        tierAndTool(ModBlocks.SANDY_PAVED_PRISMARINE_BRICKS.get(), "", "pickaxe");
        tierAndTool(ModBlocks.SANDY_PRISMARINE_BRICKS.get(), "", "pickaxe");
        tierAndTool(ModBlocks.LAYERED_PRISMARINE.get(), "", "pickaxe");
        tierAndTool(ModBlocks.CITRUS_LOG.get(), "", "axe");
        tierAndTool(ModBlocks.STRIPPED_CITRUS_LOG.get(), "", "axe");
        tierAndTool(ModBlocks.CITRUS_WOOD.get(), "", "axe");
        tierAndTool(ModBlocks.STRIPPED_CITRUS_WOOD.get(), "", "axe");
        tierAndTool(ModBlocks.BATTERY_BOX.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.FLUID_COMBUSTION_DYNAMO.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.MANGANESE_ORE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.DEEPSLATE_MANGANESE_ORE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.RAW_MANGANESE_BLOCK.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.NOUS_TANK.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.CHARCOAL_BLOCK.get(), "", "pickaxe");
        tierAndTool(ModBlocks.MARIN_ORE.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.RAW_MARIN_BLOCK.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.MARIN_BLOCK.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.SNOW_BRICKS.get(), "", "pickaxe");
        tierAndTool(ModBlocks.ICE_BRICKS.get(), "", "pickaxe");
        tierAndTool(ModBlocks.COPPER_DRUM.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.IRON_DRUM.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.GOLD_DRUM.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.STEEL_DRUM.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.DIAMOND_DRUM.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.NETHERITE_DRUM.get(), "diamond", "pickaxe");
        tierAndTool(ModBlocks.ASPHALT_BLOCK.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.STEEL_BLOCK.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.TEMPERED_GLASS.get(), "diamond", "pickaxe");
        tierAndTool(ModBlocks.DEMON_BLOCK.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.OBSIDIAN_BRICKS.get(), "diamond", "pickaxe");
        ModBlockFamilies.OBSIDIAN_BRICKS.getVariants().values().forEach(block -> tierAndTool(block, "diamond", "pickaxe"));
        ModBlockFamilies.SNOW_BRICKS.getVariants().values().forEach(block -> tierAndTool(block, "", "pickaxe"));
        ModBlockFamilies.ICE_BRICKS.getVariants().values().forEach(block -> tierAndTool(block, "", "pickaxe"));
        tierAndTool(ModBlocks.COPPER_CABLE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.COVERED_COPPER_CABLE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.ENERGY_DOCK.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.SHOCK_ABSORBER.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.TESSERACT.get(), "diamond", "pickaxe");
        tierAndTool(ModBlocks.TRASH_CAN.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.MACHINE_WORKBENCH.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.GOLD_CABLE.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.COVERED_GOLD_CABLE.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.COPPER_FLUID_PIPE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.GOLD_FLUID_PIPE.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.COPPER_ITEM_PIPE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.GOLD_ITEM_PIPE.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.SOLID_COMBUSTION_DYNAMO.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.SOLAR_PANEL.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.MOSS_MANGANESE_ORE.get(), "stone", "hoe");
        tierAndTool(ModBlocks.MOSS_COPPER_ORE.get(), "stone", "hoe");
        tierAndTool(ModBlocks.MOSS_IRON_ORE.get(), "stone", "hoe");
        tierAndTool(ModBlocks.MOSS_GOLD_ORE.get(), "iron", "hoe");
        tierAndTool(ModBlocks.MOSS_REDSTONE_ORE.get(), "iron", "hoe");
        tierAndTool(ModBlocks.MOSS_LAPIS_ORE.get(), "stone", "hoe");
        tierAndTool(ModBlocks.MOSS_DIAMOND_ORE.get(), "iron", "hoe");
        tierAndTool(ModBlocks.MOSS_EMERALD_ORE.get(), "iron", "hoe");
        tierAndTool(ModBlocks.MOSS_COAL_ORE.get(), "wooden", "hoe");
        tierAndTool(ModBlocks.TIN_ORE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.DEEPSLATE_TIN_ORE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.GRAVEL_TIN_ORE.get(), "stone", "shovel");
        tierAndTool(ModBlocks.MOSS_TIN_ORE.get(), "stone", "hoe");
        tierAndTool(ModBlocks.ALUMINUM_ORE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.DEEPSLATE_ALUMINUM_ORE.get(), "stone", "pickaxe");
        tierAndTool(ModBlocks.GRAVEL_ALUMINUM_ORE.get(), "stone", "shovel");
        tierAndTool(ModBlocks.MOSS_ALUMINUM_ORE.get(), "stone", "hoe");
        tierAndTool(ModBlocks.STEEL_ITEM_PIPE.get(), "iron", "pickaxe");
        tierAndTool(ModBlocks.STEEL_FLUID_PIPE.get(), "iron", "pickaxe");

        tag(ModTags.Blocks.MINEABLE_WITH_WRENCH).add(
            ModBlocks.MACHINE_BLOCK.get(),
            ModBlocks.RESONATOR.get(),
            ModBlocks.ANVILINATOR.get(),
            ModBlocks.BOOK_DUPLICATOR.get(),
            ModBlocks.BATTERY_BOX.get(),
            ModBlocks.FLUID_COMBUSTION_DYNAMO.get(),
            ModBlocks.NOUS_TANK.get(),
            ModBlocks.SHOCK_ABSORBER.get(),
            ModBlocks.MACHINE_WORKBENCH.get(),
            ModBlocks.SOLID_COMBUSTION_DYNAMO.get()
        );
        tag(ModTags.Blocks.MINEABLE_WITH_WRENCH).addTag(Tags.Blocks.STORAGE_BLOCKS);
        tag(ModTags.Blocks.MINEABLE_WITH_HAMMAXE).addTags(BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_PICKAXE);
        tag(ModTags.Blocks.MINEABLE_WITH_GLASSCUTTER).addTags(Tags.Blocks.GLASS, Tags.Blocks.GLASS_PANES);
        tag(ModTags.Blocks.MINEABLE_WITH_GLASSCUTTER).add(
                Blocks.GLOWSTONE, Blocks.SEA_LANTERN
        );
        tag(ModTags.Blocks.MINEABLE_WITH_GRAFTER).addTags(BlockTags.LEAVES);
        tag(ModTags.Blocks.MINEABLE_WITH_SICKLE).addTags(
                BlockTags.MINEABLE_WITH_HOE,
                BlockTags.CROPS
        );
        tag(ModTags.Blocks.MINEABLE_WITH_SICKLE).add(
                Blocks.SHORT_GRASS,
                Blocks.FERN,
                Blocks.DEAD_BUSH,
                Blocks.VINE,
                Blocks.GLOW_LICHEN,
                Blocks.SUNFLOWER,
                Blocks.LILAC,
                Blocks.ROSE_BUSH,
                Blocks.PEONY,
                Blocks.TALL_GRASS,
                Blocks.LARGE_FERN,
                Blocks.HANGING_ROOTS,
                Blocks.PITCHER_PLANT,
                Blocks.WARPED_ROOTS,
                Blocks.WEEPING_VINES,
                Blocks.WEEPING_VINES_PLANT,
                Blocks.CRIMSON_ROOTS,
                Blocks.TWISTING_VINES,
                Blocks.TWISTING_VINES_PLANT
        );
        tag(ModTags.Blocks.VERSATILITY_MINEABLES).addTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_SHOVEL);

        tag(BlockTags.BAMBOO_PLANTABLE_ON).add(ModBlocks.GARDENING_POT.get());
        tag(BlockTags.SMALL_FLOWERS).add(ModBlocks.FOUR_LEAF_CLOVER.get(), ModBlocks.TIGRIS_FLOWER.get());
        tag(BlockTags.COAL_ORES).add(
            ModBlocks.GRAVEL_COAL_ORE.get(),
            ModBlocks.MOSS_COAL_ORE.get()
        );
        tag(BlockTags.COPPER_ORES).add(
            ModBlocks.GRAVEL_COPPER_ORE.get(),
            ModBlocks.MOSS_COPPER_ORE.get()
        );
        tag(BlockTags.IRON_ORES).add(
            ModBlocks.GRAVEL_IRON_ORE.get(),
            ModBlocks.MOSS_IRON_ORE.get()
        );
        tag(BlockTags.GOLD_ORES).add(
            ModBlocks.GRAVEL_GOLD_ORE.get(),
            ModBlocks.MOSS_GOLD_ORE.get()
        );
        tag(ModTags.Blocks.ORES_MANGANESE).add(
            ModBlocks.MANGANESE_ORE.get(),
            ModBlocks.DEEPSLATE_MANGANESE_ORE.get(),
            ModBlocks.MOSS_MANGANESE_ORE.get(),
            ModBlocks.GRAVEL_MANGANESE_ORE.get()
        );
        tag(ModTags.Blocks.ORES_TIN).add(
            ModBlocks.TIN_ORE.get(),
            ModBlocks.DEEPSLATE_TIN_ORE.get(),
            ModBlocks.GRAVEL_TIN_ORE.get(),
            ModBlocks.MOSS_TIN_ORE.get()
        );
        tag(ModTags.Blocks.ORES_ALUMINUM).add(
            ModBlocks.ALUMINUM_ORE.get(),
            ModBlocks.DEEPSLATE_ALUMINUM_ORE.get(),
            ModBlocks.GRAVEL_ALUMINUM_ORE.get(),
            ModBlocks.MOSS_ALUMINUM_ORE.get()
        );
        tag(BlockTags.DIAMOND_ORES).add(
            ModBlocks.MOSS_DIAMOND_ORE.get(),
            ModBlocks.GRAVEL_DIAMOND_ORE.get()
        );
        tag(BlockTags.EMERALD_ORES).add(
            ModBlocks.MOSS_EMERALD_ORE.get(),
            ModBlocks.GRAVEL_EMERALD_ORE.get()
        );
        tag(BlockTags.REDSTONE_ORES).add(
            ModBlocks.MOSS_REDSTONE_ORE.get(),
            ModBlocks.GRAVEL_REDSTONE_ORE.get()
        );
        tag(BlockTags.LAPIS_ORES).add(
            ModBlocks.MOSS_LAPIS_ORE.get(),
            ModBlocks.GRAVEL_LAPIS_ORE.get()
        );
        tag(Tags.Blocks.ORE_RATES_DENSE).add(
            ModBlocks.GRAVEL_COPPER_ORE.get(),
            ModBlocks.GRAVEL_REDSTONE_ORE.get(),
            ModBlocks.GRAVEL_LAPIS_ORE.get(),
            ModBlocks.MOSS_COPPER_ORE.get(),
            ModBlocks.MOSS_REDSTONE_ORE.get(),
            ModBlocks.MOSS_LAPIS_ORE.get()
        );
        tag(Tags.Blocks.ORE_RATES_SINGULAR).add(
            ModBlocks.GRAVEL_COAL_ORE.get(),
            ModBlocks.GRAVEL_IRON_ORE.get(),
            ModBlocks.GRAVEL_GOLD_ORE.get(),
            ModBlocks.GRAVEL_DIAMOND_ORE.get(),
            ModBlocks.GRAVEL_EMERALD_ORE.get(),
            ModBlocks.GRAVEL_MANGANESE_ORE.get(),
            ModBlocks.MANGANESE_ORE.get(),
            ModBlocks.DEEPSLATE_MANGANESE_ORE.get(),
            ModBlocks.MARIN_ORE.get(),
            ModBlocks.CHLOROPHYTE_DEBRIS.get(),
            ModBlocks.MOSS_COAL_ORE.get(),
            ModBlocks.MOSS_IRON_ORE.get(),
            ModBlocks.MOSS_GOLD_ORE.get(),
            ModBlocks.MOSS_DIAMOND_ORE.get(),
            ModBlocks.MOSS_EMERALD_ORE.get(),
            ModBlocks.MOSS_MANGANESE_ORE.get(),
            ModBlocks.TIN_ORE.get(),
            ModBlocks.DEEPSLATE_TIN_ORE.get(),
            ModBlocks.GRAVEL_TIN_ORE.get(),
            ModBlocks.MOSS_TIN_ORE.get(),
            ModBlocks.ALUMINUM_ORE.get(),
            ModBlocks.DEEPSLATE_ALUMINUM_ORE.get(),
            ModBlocks.GRAVEL_ALUMINUM_ORE.get(),
            ModBlocks.MOSS_ALUMINUM_ORE.get()
        );
        tag(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE).add(
                ModBlocks.DEEPSLATE_MANGANESE_ORE.get(),
                ModBlocks.DEEPSLATE_TIN_ORE.get(),
                ModBlocks.DEEPSLATE_ALUMINUM_ORE.get()
        );
        tag(Tags.Blocks.ORE_BEARING_GROUND_STONE).add(
                ModBlocks.MANGANESE_ORE.get(),
                ModBlocks.TIN_ORE.get(),
                ModBlocks.ALUMINUM_ORE.get()
        );
        tag(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK).add(
                ModBlocks.MARIN_ORE.get()
        );
        tag(ModTags.Blocks.ORE_BEARING_GROUND_GRAVEL).add(
            ModBlocks.GRAVEL_MANGANESE_ORE.get(),
            ModBlocks.GRAVEL_COAL_ORE.get(),
            ModBlocks.GRAVEL_COPPER_ORE.get(),
            ModBlocks.GRAVEL_IRON_ORE.get(),
            ModBlocks.GRAVEL_GOLD_ORE.get(),
            ModBlocks.GRAVEL_DIAMOND_ORE.get(),
            ModBlocks.GRAVEL_EMERALD_ORE.get(),
            ModBlocks.GRAVEL_REDSTONE_ORE.get(),
            ModBlocks.GRAVEL_LAPIS_ORE.get(),
            ModBlocks.GRAVEL_TIN_ORE.get(),
            ModBlocks.GRAVEL_ALUMINUM_ORE.get()
            
        );
        tag(ModTags.Blocks.ORE_BEARING_GROUND_MOSS).add(
            ModBlocks.CHLOROPHYTE_DEBRIS.get(),
            ModBlocks.MOSS_COPPER_ORE.get(),
            ModBlocks.MOSS_IRON_ORE.get(),
            ModBlocks.MOSS_GOLD_ORE.get(),
            ModBlocks.MOSS_REDSTONE_ORE.get(),
            ModBlocks.MOSS_LAPIS_ORE.get(),
            ModBlocks.MOSS_DIAMOND_ORE.get(),
            ModBlocks.MOSS_EMERALD_ORE.get(),
            ModBlocks.MOSS_COAL_ORE.get(),
            ModBlocks.MOSS_MANGANESE_ORE.get(),
            ModBlocks.MOSS_TIN_ORE.get(),
            ModBlocks.MOSS_ALUMINUM_ORE.get()
        );
        tag(Tags.Blocks.ORES).addTags(
            ModTags.Blocks.ORE_BEARING_GROUND_GRAVEL,
            ModTags.Blocks.ORE_BEARING_GROUND_MOSS
        );
        tag(Tags.Blocks.ORES).add(
                ModBlocks.MANGANESE_ORE.get(),
                ModBlocks.MARIN_ORE.get(),
                ModBlocks.TIN_ORE.get(),
                ModBlocks.ALUMINUM_ORE.get()
        );
        tag(ModTags.Blocks.MOSS_ORE_REPLACEABLES).add(Blocks.MOSS_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS).add(
                ModBlocks.SUGAR_CHARCOAL_BLOCK.get(),
                ModBlocks.MARIN_BLOCK.get(),
                ModBlocks.STEEL_BLOCK.get(),
                ModBlocks.DEMON_BLOCK.get()
        );
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(
                ModTags.Blocks.STORAGE_BLOCKS_CHARCOAL
        );
        tag(ModTags.Blocks.STORAGE_BLOCKS_CHARCOAL).add(
                ModBlocks.CHARCOAL_BLOCK.get()
        );
        tag(ModTags.Blocks.STORAGE_BLOCKS_STEEL).add(
                ModBlocks.STEEL_BLOCK.get()
        );
        tag(ModTags.Blocks.DRUM).add(
                ModBlocks.COPPER_DRUM.get(),
                ModBlocks.IRON_DRUM.get(),
                ModBlocks.GOLD_DRUM.get(),
                ModBlocks.STEEL_DRUM.get(),
                ModBlocks.DIAMOND_DRUM.get(),
                ModBlocks.NETHERITE_DRUM.get()
        );
        tag(BlockTags.LOGS_THAT_BURN).add(ModBlocks.CITRUS_LOG.get(), ModBlocks.STRIPPED_CITRUS_LOG.get(), ModBlocks.CITRUS_WOOD.get(), ModBlocks.STRIPPED_CITRUS_WOOD.get());
        tag(BlockTags.PLANKS).add(ModBlocks.CITRUS_PLANKS.get());
        tag(Tags.Blocks.GLASS).add(ModBlocks.TEMPERED_GLASS.get());
        tag(ModTags.Blocks.GLASS_HARDENED).add(ModBlocks.TEMPERED_GLASS.get());
        tag(ModTags.Blocks.HARDENED_GLASS).add(ModBlocks.TEMPERED_GLASS.get());
        tag(BlockTags.IMPERMEABLE).add(ModBlocks.TEMPERED_GLASS.get());
        tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_TIGRIS.get());
        tag(Tags.Blocks.OBSIDIAN).add(ModBlocks.OBSIDIAN_BRICKS.get());
        tag(BlockTags.WALLS).add(
            ModBlocks.OBSIDIAN_BRICK_WALL.get(),
            ModBlocks.ICE_BRICK_WALL.get(),
            ModBlocks.SNOW_BRICK_WALL.get()
        );

        ModMachines.MACHINES.getMachines().forEach(holder -> {
            tierAndTool(holder.getBlock(), "stone", "pickaxe");
            tag(ModTags.Blocks.MINEABLE_WITH_WRENCH).add(holder.getBlock());
        });

    }

    private void tierAndTool(Block block, String tier, String tool) {
        switch (tier) {
            case "wood" -> tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(block);
            case "gold" -> tag(Tags.Blocks.NEEDS_GOLD_TOOL).add(block);
            case "stone" -> tag(BlockTags.NEEDS_STONE_TOOL).add(block);
            case "copper" -> tag(ModTags.Blocks.NEEDS_COPPER_TOOL).add(block);
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
            case "wrench" -> tag(ModTags.Blocks.MINEABLE_WITH_WRENCH).add(block);
        }
    }

}
