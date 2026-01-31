package com.sonamorningstar.eternalartifacts.core;

import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.HashSet;
import java.util.Set;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModLootTables {

    @Getter
    private static final Set<ResourceLocation> MOD_LOOTTABLES = new HashSet<>();

    public static final ResourceLocation COPPER_OREBERRY_HARVEST = registerOreBerries("copper");
    public static final ResourceLocation IRON_OREBERRY_HARVEST = registerOreBerries("iron");
    public static final ResourceLocation GOLD_OREBERRY_HARVEST = registerOreBerries("gold");
    public static final ResourceLocation EXPERIENCE_OREBERRY_HARVEST = registerOreBerries("experience");
    public static final ResourceLocation MANGANESE_OREBERRY_HARVEST = registerOreBerries("manganese");

    public static final ResourceLocation HAMMERING_COAL = registerHammering(Tags.Blocks.STORAGE_BLOCKS_COAL);
    public static final ResourceLocation HAMMERING_CHARCOAL = registerHammering(ModTags.Blocks.STORAGE_BLOCKS_CHARCOAL);
    public static final ResourceLocation HAMMERING_CLAY = registerHammering(Blocks.CLAY);
    public static final ResourceLocation HAMMERING_SUGAR_CHARCOAL = registerHammering(ModBlocks.SUGAR_CHARCOAL_BLOCK.get());
    public static final ResourceLocation HAMMERING_COPPER_ORE = registerHammering(Tags.Blocks.ORES_COPPER);
    public static final ResourceLocation HAMMERING_TIN_ORE = registerHammering(ModTags.Blocks.ORES_TIN);
    
    public static final ResourceLocation SLUDGE_REFINING_DIRT = registerSludgeRefining(Blocks.DIRT.asItem());
    public static final ResourceLocation SLUDGE_REFINING_COARSE_DIRT = registerSludgeRefining(Blocks.COARSE_DIRT.asItem());
    public static final ResourceLocation SLUDGE_REFINING_ROOTED_DIRT = registerSludgeRefining(Blocks.ROOTED_DIRT.asItem());
    public static final ResourceLocation SLUDGE_REFINING_PODZOL = registerSludgeRefining(Blocks.PODZOL.asItem());
    public static final ResourceLocation SLUDGE_REFINING_MOSS_BLOCK = registerSludgeRefining(Blocks.MOSS_BLOCK.asItem());
    public static final ResourceLocation SLUDGE_REFINING_MOSS_CARPET = registerSludgeRefining(Blocks.MOSS_CARPET.asItem());
    public static final ResourceLocation SLUDGE_REFINING_GRAVEL = registerSludgeRefining(Blocks.GRAVEL.asItem());
    public static final ResourceLocation SLUDGE_REFINING_SAND = registerSludgeRefining(Blocks.SAND.asItem());
    public static final ResourceLocation SLUDGE_REFINING_RED_SAND = registerSludgeRefining(Blocks.RED_SAND.asItem());
    public static final ResourceLocation SLUDGE_REFINING_CLAY_BLOCK = registerSludgeRefining(Blocks.CLAY.asItem());
    public static final ResourceLocation SLUDGE_REFINING_SOUL_SAND = registerSludgeRefining(Blocks.SOUL_SAND.asItem());
    public static final ResourceLocation SLUDGE_REFINING_SOUL_SOIL = registerSludgeRefining(Blocks.SOUL_SOIL.asItem());
    public static final ResourceLocation SLUDGE_REFINING_MUD = registerSludgeRefining(Blocks.MUD.asItem());
    public static final ResourceLocation SLUDGE_REFINING_BONE_MEAL = registerSludgeRefining(Items.BONE_MEAL);

    public static final ResourceLocation CHARGED_SHEEP_WHITE = register("entities/charged_sheep/white");
    public static final ResourceLocation CHARGED_SHEEP_ORANGE = register("entities/charged_sheep/orange");
    public static final ResourceLocation CHARGED_SHEEP_MAGENTA = register("entities/charged_sheep/magenta");
    public static final ResourceLocation CHARGED_SHEEP_LIGHT_BLUE = register("entities/charged_sheep/light_blue");
    public static final ResourceLocation CHARGED_SHEEP_YELLOW = register("entities/charged_sheep/yellow");
    public static final ResourceLocation CHARGED_SHEEP_LIME = register("entities/charged_sheep/lime");
    public static final ResourceLocation CHARGED_SHEEP_PINK = register("entities/charged_sheep/pink");
    public static final ResourceLocation CHARGED_SHEEP_GRAY = register("entities/charged_sheep/gray");
    public static final ResourceLocation CHARGED_SHEEP_LIGHT_GRAY = register("entities/charged_sheep/light_gray");
    public static final ResourceLocation CHARGED_SHEEP_CYAN = register("entities/charged_sheep/cyan");
    public static final ResourceLocation CHARGED_SHEEP_PURPLE = register("entities/charged_sheep/purple");
    public static final ResourceLocation CHARGED_SHEEP_BLUE = register("entities/charged_sheep/blue");
    public static final ResourceLocation CHARGED_SHEEP_BROWN = register("entities/charged_sheep/brown");
    public static final ResourceLocation CHARGED_SHEEP_GREEN = register("entities/charged_sheep/green");
    public static final ResourceLocation CHARGED_SHEEP_RED = register("entities/charged_sheep/red");
    public static final ResourceLocation CHARGED_SHEEP_BLACK = register("entities/charged_sheep/black");

    public static final ResourceLocation SURVIVALISTS_IGLOO = register("chests/survivalists_igloo");
    public static final ResourceLocation PLAINS_HOUSE_ENTRANCE = register("chests/plains_house_entrance");
    public static final ResourceLocation PLAINS_HOUSE_DESK = register("chests/plains_house_desk");

    private static ResourceLocation registerOreBerries(String material) {
        return register("oreberries/"+material);
    }
    private static ResourceLocation registerHammering(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return register("hammering/blocks/"+id.getNamespace()+"/"+id.getPath());
    }
    private static ResourceLocation registerHammering(TagKey<Block> block) {
        ResourceLocation id = block.location();
        return register("hammering/tags/"+id.getNamespace()+"/"+id.getPath());
    }
    private static ResourceLocation registerSludgeRefining(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return register("sludge_refiner/"+id.getNamespace()+"/"+id.getPath());
    }

    private static ResourceLocation register(String id) {
        return register(new ResourceLocation(MODID, id));
    }
    private static ResourceLocation register(ResourceLocation id) {
        if (MOD_LOOTTABLES.add(id)) {
            return id;
        } else {
            throw new IllegalArgumentException(id + " is already a registered built-in loot table");
        }
    }

}
