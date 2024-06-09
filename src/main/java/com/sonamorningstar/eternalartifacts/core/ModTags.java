package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModTags {
    public static class Blocks {
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
        public static final TagKey<Item> SLIMEBALLS_PINK = forgeTag("slimeballs/pink");
        public static final TagKey<Item> TABLETS = modTag("tablets");
        public static final TagKey<Item> NUGGETS_COPPER = forgeTag("nuggets/copper");
        public static final TagKey<Item> NUGGETS_EXPERIENCE = forgeTag("nuggets/experience");
        public static final TagKey<Item> NUGGETS_MANGANESE= forgeTag("nuggets/manganese");
        public static final TagKey<Item> NUGGETS_STEEL = forgeTag("nuggets/steel");
        public static final TagKey<Item> PLASTIC = forgeTag("plastic_sheets");

        private static TagKey<Item> forgeTag(String name) { return ItemTags.create(new ResourceLocation("forge", name)); }
        private static TagKey<Item> modTag(String name) { return ItemTags.create(new ResourceLocation(MODID, name)); }
    }

    public static class Fluids {
        public static final TagKey<Fluid> EXPERIENCE = forgeTag("experience");
        public static final TagKey<Fluid> MEAT = forgeTag("meat");
        public static final TagKey<Fluid> PINK_SLIME = forgeTag("pink_slime");
        public static final TagKey<Fluid> BLOOD = forgeTag("blood");

        private static TagKey<Fluid> forgeTag(String name) { return FluidTags.create(new ResourceLocation("forge", name)); }
        private static TagKey<Fluid> modTag(String name) { return FluidTags.create(new ResourceLocation(MODID, name)); }
    }

}
