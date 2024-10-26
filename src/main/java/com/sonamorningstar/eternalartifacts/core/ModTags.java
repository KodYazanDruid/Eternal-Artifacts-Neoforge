package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> MINEABLE_WITH_WRENCH = forgeTag("mineable/wrench");
        public static final TagKey<Block> NEEDS_COPPER_TOOL = forgeTag("needs_copper_tool");
        public static final TagKey<Block> ORES_MANGANESE = forgeTag("ores/manganese");
        public static final TagKey<Block> STORAGE_BLOCKS_CHARCOAL = forgeTag("storage_blocks/charcoal");
        public static final TagKey<Block> MINEABLE_WITH_HAMMAXE = modTag("mineable/hammaxe");
        public static final TagKey<Block> MINEABLE_WITH_GLASSCUTTER = modTag("mineable/glasscutter");
        public static final TagKey<Block> MINEABLE_WITH_GRAFTER = modTag("mineable/grafter");
        public static final TagKey<Block> STORAGE_BLOCKS_STEEL = forgeTag("storage_blocks/steel");
        public static final TagKey<Block> DRUM = modTag("drum");
        public static final TagKey<Block> MINEABLE_WITH_SICKLE = forgeTag("mineable/sickle");


        private static TagKey<Block> forgeTag(String name) { return BlockTags.create(new ResourceLocation("forge", name)); }
        private static TagKey<Block> modTag(String name) { return BlockTags.create(new ResourceLocation(MODID, name)); }
    }

    public static class Items {
        public static final TagKey<Item> FRUITS = forgeTag("fruits");
        public static final TagKey<Item> FRUITS_ORANGE = forgeTag("fruits/orange");
        public static final TagKey<Item> BANANA = forgeTag("fruits/banana");
        public static final TagKey<Item> FRUITS_ANCIENT = forgeTag("fruits/ancient");
        public static final TagKey<Item> GARDENING_POT_SUITABLE = modTag("gardening_pot_suitable");
        public static final TagKey<Item> INGOTS_MEAT = forgeTag("ingots/meat");
        public static final TagKey<Item> INGOTS_RAW_MEAT = forgeTag("ingots/raw_meat");
        public static final TagKey<Item> INGOTS_MANGANESE = forgeTag("ingots/manganese");
        public static final TagKey<Item> INGOTS_STEEL = forgeTag("ingots/steel");
        public static final TagKey<Item> INGOTS_ARDITE = forgeTag("ingots/ardite");
        public static final TagKey<Item> SLIMEBALLS_PINK = forgeTag("slimeballs/pink");
        public static final TagKey<Item> TABLETS = modTag("tablets");
        public static final TagKey<Item> NUGGETS_COPPER = forgeTag("nuggets/copper");
        public static final TagKey<Item> NUGGETS_EXPERIENCE = forgeTag("nuggets/experience");
        public static final TagKey<Item> NUGGETS_MANGANESE= forgeTag("nuggets/manganese");
        public static final TagKey<Item> NUGGETS_STEEL = forgeTag("nuggets/steel");
        public static final TagKey<Item> PLASTIC = forgeTag("plastic_sheets");
        public static final TagKey<Item> TOOLS_WRENCH = forgeTag("tools/wrench");
        public static final TagKey<Item> DUSTS_COAL = forgeTag("dusts/coal");
        public static final TagKey<Item> DUSTS_CHARCOAL = forgeTag("dusts/charcoal");
        public static final TagKey<Item> DUSTS_SUGAR_CHARCOAL = forgeTag("dusts/sugar_charcoal");
        public static final TagKey<Item> TOOLS_HAMMER = forgeTag("tools/hammer");
        public static final TagKey<Item> CHARCOAL = forgeTag("charcoal");
        public static final TagKey<Item> TOOLS_CUTLASS = modTag("tools/cutlass");
        public static final TagKey<Item> TOOLS_SICKLE = forgeTag("tools/sickle");

        private static TagKey<Item> forgeTag(String name) { return ItemTags.create(new ResourceLocation("forge", name)); }
        private static TagKey<Item> modTag(String name) { return ItemTags.create(new ResourceLocation(MODID, name)); }
    }

    public static class Fluids {
        public static final TagKey<Fluid> EXPERIENCE = forgeTag("experience");
        public static final TagKey<Fluid> MEAT = forgeTag("meat");
        public static final TagKey<Fluid> PINK_SLIME = forgeTag("pink_slime");
        public static final TagKey<Fluid> BLOOD = forgeTag("blood");
        public static final TagKey<Fluid> PLASTIC = forgeTag("plastic");
        public static final TagKey<Fluid> CRUDE_OIL = forgeTag("crude_oil");
        public static final TagKey<Fluid> GASOLINE = forgeTag("gasoline");
        public static final TagKey<Fluid> DIESEL = forgeTag("diesel");

        private static TagKey<Fluid> forgeTag(String name) { return FluidTags.create(new ResourceLocation("forge", name)); }
        private static TagKey<Fluid> modTag(String name) { return FluidTags.create(new ResourceLocation(MODID, name)); }
    }

}
