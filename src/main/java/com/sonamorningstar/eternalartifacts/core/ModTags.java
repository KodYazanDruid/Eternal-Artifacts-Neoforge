package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
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
        public static final TagKey<Block> GLASS_HARDENED = forgeTag("glass/hardened");
        public static final TagKey<Block> HARDENED_GLASS = forgeTag("hardened_glass");
        public static final TagKey<Block> VERSATILITY_MINEABLES = modTag("mineable/versatility");

        private static TagKey<Block> forgeTag(String name) { return BlockTags.create(new ResourceLocation("forge", name)); }
        private static TagKey<Block> modTag(String name) { return BlockTags.create(new ResourceLocation(MODID, name)); }
        private static TagKey<Block> otherTag(String namespace, String name) { return BlockTags.create(new ResourceLocation(namespace, name)); }
    }

    public static class Items {
        public static final TagKey<Item> FRUITS = forgeTag("fruits");
        public static final TagKey<Item> FRUITS_ORANGE = forgeTag("fruits/orange");
        public static final TagKey<Item> FRUITS_BANANA = forgeTag("fruits/banana");
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
        public static final TagKey<Item> DUSTS_FLOUR = forgeTag("dusts/flour");
        public static final TagKey<Item> FLOUR_WHEAT = forgeTag("flour/wheat");
        public static final TagKey<Item> DOUGH = forgeTag("dough");
        public static final TagKey<Item> DOUGH_WHEAT = forgeTag("dough/wheat");
        public static final TagKey<Item> DUSTS_CLAY = forgeTag("dusts/clay");
        public static final TagKey<Item> CHARMS = modTag("charms");
        public static final TagKey<Item> CHARMS_HEAD = modTag("charms/head");
        public static final TagKey<Item> CHARMS_NECKLACE = modTag("charms/necklace");
        public static final TagKey<Item> CHARMS_HAND = modTag("charms/hand");
        public static final TagKey<Item> CHARMS_RING = modTag("charms/ring");
        public static final TagKey<Item> CHARMS_BACK = modTag("charms/back");
        public static final TagKey<Item> CHARMS_BOOTS = modTag("charms/boots");
        public static final TagKey<Item> CHARMS_CHARM = modTag("charms/charm");
        public static final TagKey<Item> CHARMS_BELT = modTag("charms/belt");
        public static final TagKey<Item> CHARMS_BRACELET = modTag("charms/bracelet");

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

    public static class Biomes {

        public static final TagKey<Biome> VALID_SURVIVALISTS_IGLOO_BIOMES = modTag("can_survivalists_igloo_spawn");

        private static TagKey<Biome> forgeTag(String name) { return TagKey.create(Registries.BIOME, new ResourceLocation("forge", name)); }
        private static TagKey<Biome> modTag(String name) { return TagKey.create(Registries.BIOME, new ResourceLocation(MODID, name)); }

    }
}
