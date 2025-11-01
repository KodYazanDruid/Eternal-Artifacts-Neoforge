package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.registrar.FluidDeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.ORANGE.get());
        basicItem(ModItems.BATTERY.get());
        basicItem(ModItems.CAPACITOR.get());
        basicItem(ModItems.LENS.get());
        basicItem(ModItems.PLANT_MATTER.get());
        basicItem(ModItems.HOLY_DAGGER.get());
        basicItem(ModItems.MEDKIT.get());
        basicItem(ModItems.FROG_LEGS.get());
        basicItem(ModItems.MAGIC_FEATHER.get());
        basicItem(ModItems.GOLD_RING.get());
        basicItem(ModItems.ANCIENT_SEED.get());
        basicItem(ModItems.ANCIENT_FRUIT.get());
        handheld(ModItems.AXE_OF_REGROWTH);
        basicItem(ModItems.RAW_MEAT_INGOT.get());
        basicItem(ModItems.COOKED_MEAT_INGOT.get());
        basicItem(ModItems.PINK_SLIME.get());
        basicItem(ModItems.ENDER_KNAPSACK.get());
        basicItem(ModItems.PORTABLE_CRAFTER.get());
        basicItem(ModItems.GOLDEN_ANCIENT_FRUIT.get());
        basicItem(ModItems.COMFY_SHOES.get());
        basicItem(ModItems.SUGAR_CHARCOAL.get());
        basicItem(ModItems.BANANA.get());
        basicItem(ModItems.ENDER_NOTEBOOK.get());
        basicItem(ModItems.APPLE_PIE.get());
        basicItem(ModItems.BANANA_CREAM_PIE.get());
        handheld(ModItems.CHLOROVEIN_PICKAXE);
        basicItem(ModItems.ENDER_TABLET.get());
        basicItem(ModItems.STONE_TABLET.get());
        handheld(ModItems.COPPER_SWORD);
        handheld(ModItems.COPPER_PICKAXE);
        handheld(ModItems.COPPER_AXE);
        handheld(ModItems.COPPER_SHOVEL);
        handheld(ModItems.COPPER_HOE);
        basicItem(ModItems.CHLOROPHYTE_TABLET.get());
        basicItem(ModItems.CHLOROPHYTE_INGOT.get());
        basicItem(ModItems.CHLOROPHYTE_UPGRADE_SMITHING_TEMPLATE.get());
        basicItem(ModItems.DUCK_MEAT.get());
        basicItem(ModItems.COOKED_DUCK_MEAT.get());
        basicItem(ModItems.DUCK_FEATHER.get());
        handheld(ModItems.SWORD_OF_THE_GREEN_EARTH);
        handheld(ModItems.NATURAL_SPADE);
        handheld(ModItems.LUSH_GRUBBER);
        basicItem(ModItems.COPPER_TABLET.get());
        basicItem(ModItems.COPPER_NUGGET.get());
        basicItem(ModItems.EXPERIENCE_BERRY.get());
        basicItem(ModItems.MANGANESE_INGOT.get());
        basicItem(ModItems.MANGANESE_NUGGET.get());
        basicItem(ModItems.STEEL_INGOT.get());
        basicItem(ModItems.STEEL_NUGGET.get());
        basicItem(ModItems.PLASTIC_SHEET.get());
        handheld(ModItems.WRENCH);
        basicItem(ModItems.ENDER_PAPER.get());
        basicItem(ModItems.RAW_MANGANESE.get());
        basicItem(ModItems.KNAPSACK.get());
        spawnEggItem(ModItems.DEMON_EYE_SPAWN_EGG);
        spawnEggItem(ModItems.PINKY_SPAWN_EGG);
        spawnEggItem(ModItems.DUCK_SPAWN_EGG);
        spawnEggItem(ModItems.MAGICAL_BOOK_SPAWN_EGG);
        basicItem(ModItems.COAL_DUST.get());
        basicItem(ModItems.CHARCOAL_DUST.get());
        basicItem(ModItems.SUGAR_CHARCOAL_DUST.get());
        handheld(ModItems.WOODEN_HAMMER);
        handheld(ModItems.STONE_HAMMER);
        handheld(ModItems.COPPER_HAMMER);
        handheld(ModItems.IRON_HAMMER);
        handheld(ModItems.GOLDEN_HAMMER);
        handheld(ModItems.DIAMOND_HAMMER);
        handheld(ModItems.NETHERITE_HAMMER);
        handheld(ModItems.HAMMAXE);
        basicItem(ModItems.CLAY_DUST.get());
        basicItem(ModItems.RAW_MARIN.get());
        basicItem(ModItems.MARIN_INGOT.get());
        basicItem(ModItems.TAR_BALL.get());
        basicItem(ModItems.BITUMEN.get());
        basicItem(ModItems.PINK_SLIME_STEEL_INGOT.get());
        handheld(ModItems.GLASSCUTTER);
        handheld(ModItems.WITHERING_SWORD);
        basicItem(ModItems.FEEDING_CANISTER.get());
        basicItem(ModItems.DEMON_INGOT.get());
        basicItem(ModItems.DEMONIC_TABLET.get());
        handheld(ModItems.GRAFTER);
        basicItem(ModItems.TANK_KNAPSACK.get());
        basicItem(ModItems.SLOT_LOCK.get());
        handheld(ModItems.WOODEN_CUTLASS);
        handheld(ModItems.STONE_CUTLASS);
        handheld(ModItems.COPPER_CUTLASS);
        handheld(ModItems.IRON_CUTLASS);
        handheld(ModItems.GOLDEN_CUTLASS);
        handheld(ModItems.DIAMOND_CUTLASS);
        handheld(ModItems.NETHERITE_CUTLASS);
        handheld(ModItems.CHLOROPHYTE_CUTLASS);
        handheld(ModItems.WOODEN_SICKLE);
        handheld(ModItems.STONE_SICKLE);
        handheld(ModItems.COPPER_SICKLE);
        handheld(ModItems.IRON_SICKLE);
        handheld(ModItems.GOLDEN_SICKLE);
        handheld(ModItems.DIAMOND_SICKLE);
        handheld(ModItems.NETHERITE_SICKLE);
        handheld(ModItems.CHLOROPHYTE_SICKLE);
        basicItem(ModItems.FLOUR.get());
        basicItem(ModItems.DOUGH.get());
        basicItem(ModItems.BANANA_BREAD.get());
        handheld(ModItems.CHISEL);
        spawnEggItem(ModItems.CHARGED_SHEEP_SPAWN_EGG);
        generateTwoStateItem(ModItems.ENCUMBATOR, "active");
        generateTwoStateItem(ModItems.BLUEPRINT, "filled");
        basicItem(ModItems.WHITE_SHULKER_SHELL.get());
        basicItem(ModItems.ORANGE_SHULKER_SHELL.get());
        basicItem(ModItems.MAGENTA_SHULKER_SHELL.get());
        basicItem(ModItems.LIGHT_BLUE_SHULKER_SHELL.get());
        basicItem(ModItems.YELLOW_SHULKER_SHELL.get());
        basicItem(ModItems.LIME_SHULKER_SHELL.get());
        basicItem(ModItems.PINK_SHULKER_SHELL.get());
        basicItem(ModItems.GRAY_SHULKER_SHELL.get());
        basicItem(ModItems.LIGHT_GRAY_SHULKER_SHELL.get());
        basicItem(ModItems.CYAN_SHULKER_SHELL.get());
        basicItem(ModItems.PURPLE_SHULKER_SHELL.get());
        basicItem(ModItems.BLUE_SHULKER_SHELL.get());
        basicItem(ModItems.BROWN_SHULKER_SHELL.get());
        basicItem(ModItems.GREEN_SHULKER_SHELL.get());
        basicItem(ModItems.RED_SHULKER_SHELL.get());
        basicItem(ModItems.BLACK_SHULKER_SHELL.get());
        basicItem(ModItems.SHULKER_HELMET.get());
        basicItem(ModItems.SHULKER_CHESTPLATE.get());
        basicItem(ModItems.SHULKER_LEGGINGS.get());
        basicItem(ModItems.SHULKER_BOOTS.get());
        basicItem(ModItems.SHULKER_BULLETS_TOME.get());
        basicItem(ModItems.METEORITE_TOME.get());
        basicItem(ModItems.POWER_GAUNTLET.get());
        basicItem(ModItems.PORTABLE_BATTERY.get());
        basicItem(ModItems.CONFIGURATION_DRIVE.get());
        basicItem(ModItems.HEART_NECKLACE.get());
        basicItem(ModItems.ANGELIC_HEART.get());
        basicItem(ModItems.SAGES_TALISMAN.get());
        basicItem(ModItems.BAND_OF_ARCANE.get());
        basicItem(ModItems.EMERALD_SIGNET.get());
        basicItem(ModItems.SKYBOUND_TREADS.get());
        handheld(ModItems.STEEL_SWORD);
        handheld(ModItems.STEEL_PICKAXE);
        handheld(ModItems.STEEL_AXE);
        handheld(ModItems.STEEL_SHOVEL);
        handheld(ModItems.STEEL_HOE);
        handheld(ModItems.STEEL_HAMMER);
        handheld(ModItems.STEEL_CUTLASS);
        handheld(ModItems.STEEL_SICKLE);
        basicItem(ModItems.STEEL_HELMET.get());
        basicItem(ModItems.STEEL_CHESTPLATE.get());
        basicItem(ModItems.STEEL_LEGGINGS.get());
        basicItem(ModItems.STEEL_BOOTS.get());
        basicItem(ModItems.GALE_SASH.get());
        basicItem(ModItems.GREEN_APPLE.get());
        basicItem(ModItems.YELLOW_APPLE.get());
        handheld(ModItems.SPAWNER_EXTRACTOR);
        handheld(ModItems.BONE_SWORD);
        basicItem(ModItems.MANGANESE_DUST.get());
        basicItem(ModItems.PIPE_EXTRACTOR.get());
        basicItem(ModItems.PIPE_FILTER.get());
        handheld(ModItems.SWORD_OF_THE_TWILIGHT);
        handheld(ModItems.SWORD_OF_THE_DAWN);
        basicItem(ModItems.SOLAR_PANEL_HELMET.get());
        basicItem(ModItems.MAGIC_QUIVER.get());
        basicItem(ModItems.IRON_LEATHER_GLOVES.get());
        basicItem(ModItems.MAGNET.get());
        withParentItemPath(ModItems.CHLOROPHYTE_REPEATER, "crossbow");
        basicItem(ModItems.PORTABLE_FURNACE.get());
        basicItem(ModItems.TIN_INGOT.get());
        basicItem(ModItems.ALUMINUM_INGOT.get());
        basicItem(ModItems.BRONZE_INGOT.get());
        basicItem(ModItems.RAW_TIN.get());
        basicItem(ModItems.RAW_ALUMINUM.get());
        handheld(ModItems.DASHING_SWORD);
        basicItem(ModItems.CARBON_PAPER.get());
        basicItem(ModItems.TIN_DUST.get());
        basicItem(ModItems.ALUMINUM_DUST.get());
        basicItem(ModItems.BRONZE_DUST.get());
        basicItem(ModItems.MACHINE_ITEM_FILTER.get());
        basicItem(ModItems.MACHINE_FLUID_FILTER.get());
        basicItem(ModItems.COPPER_DUST.get());
        basicItem(ModItems.AMETHYST_ARROW.get());
        basicItem(ModItems.FINAL_CUT.get());
        basicItem(ModItems.ODDLY_SHAPED_OPAL.get());
        basicItem(ModItems.RAINCOAT.get());
        basicItem(ModItems.MAGIC_BANE.get());
        basicItem(ModItems.DEATH_CAP.get());
        basicItem(ModItems.MOONGLASS_PENDANT.get());
        basicItem(ModItems.ENTITY_CATALOGUE.get());
        basicItem(ModItems.OBLIVIUM_INGOT.get());
        spawnEggItem(ModItems.HONEY_SLIME_SPAWN_EGG);
        basicItem(ModItems.GLOW_INK_DUST.get());
        basicItem(ModItems.INTERFACE_REMOTE.get());
        basicItem(ModItems.PRISMARINE_ARROW.get());
        
        withParentItem(ModItems.ENCHANTED_GOLDEN_ANCIENT_FRUIT, ModItems.GOLDEN_ANCIENT_FRUIT);
        itemGeneratedWithTexture(ModItems.GLASS_SPLASH_BOTTLE, new ResourceLocation("splash_potion"));
        itemGeneratedWithTexture(ModItems.GLASS_LINGERING_BOTTLE, new ResourceLocation("lingering_potion"));

        withParentBlock(ModBlocks.RESONATOR);
        withParentBlock(ModBlocks.GARDENING_POT);
        withParentBlock(ModBlocks.FANCY_CHEST);
        withParentBlock(ModBlocks.PINK_SLIME_BLOCK);
        withParentBlock(ModBlocks.ROSY_FROGLIGHT);
        withParentBlock(ModBlocks.MACHINE_BLOCK);
        withParentBlock(ModBlocks.CITRUS_LOG);
        withParentBlock(ModBlocks.STRIPPED_CITRUS_LOG);
        withParentBlock(ModBlocks.CITRUS_WOOD);
        withParentBlock(ModBlocks.STRIPPED_CITRUS_WOOD);
        withParentBlock(ModBlocks.COPPER_DRUM);
        withParentBlock(ModBlocks.IRON_DRUM);
        withParentBlock(ModBlocks.GOLD_DRUM);
        withParentBlock(ModBlocks.STEEL_DRUM);
        withParentBlock(ModBlocks.DIAMOND_DRUM);
        withParentBlock(ModBlocks.NETHERITE_DRUM);
        withExistingParent(ModBlocks.COPPER_ORE_BERRY.getId().getPath(), modLoc("block/copper_oreberry_stage1"));
        withExistingParent(ModBlocks.IRON_ORE_BERRY.getId().getPath(), modLoc("block/iron_oreberry_stage1"));
        withExistingParent(ModBlocks.GOLD_ORE_BERRY.getId().getPath(), modLoc("block/gold_oreberry_stage1"));
        withExistingParent(ModBlocks.EXPERIENCE_ORE_BERRY.getId().getPath(), modLoc("block/experience_oreberry_stage1"));
        withExistingParent(ModBlocks.MANGANESE_ORE_BERRY.getId().getPath(), modLoc("block/manganese_oreberry_stage1"));
        withParentBlock(ModBlocks.BATTERY_BOX);
        itemGenerated(ModBlocks.FORSYTHIA, "upper");
        itemGenerated(ModBlocks.FOUR_LEAF_CLOVER);
        itemGenerated(ModBlocks.TIGRIS_FLOWER);
        withParentBlock(ModBlocks.ICE_BRICK_SLAB);
        withParentBlock(ModBlocks.ICE_BRICK_STAIRS);
        withExistingParent(ModBlocks.ICE_BRICK_WALL.getId().getPath(), modLoc("block/ice_brick_wall_inventory"));
        withParentBlock(ModBlocks.TRASH_CAN);
        withParentBlock(ModBlocks.MACHINE_WORKBENCH);

        ModFluids.FLUIDS.getEntries().stream().filter(p -> p.getBucketItem() != null).forEach(this::bucketItem);
        ModMachines.MACHINES.getMachines().forEach(holder -> {
            if(!holder.isHasCustomRender()) withParentBlock(holder.getBlockHolder());
        });
    }

    private void handheld(DeferredItem<Item> deferred) {
        String path = deferred.getId().getPath();
        singleTexture(path, mcLoc("item/handheld"), "layer0", modLoc("item/"+path));
    }

    private void bucketItem(DeferredHolder<Item, BucketItem> bucket, DeferredHolder<Fluid, BaseFlowingFluid.Source> source) {
        withExistingParent(bucket.getId().getPath(),
            new ResourceLocation("neoforge", "item/bucket_drip"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(source.get())
                .applyTint(true);
    }
    private void bucketItem(FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, ? extends LiquidBlock> holder) {
        withExistingParent(holder.getBucketItemHolder().getId().getPath(),
            new ResourceLocation("neoforge", "item/bucket_drip"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(holder.getFluid())
                .applyTint(true);
    }

    private void itemGenerated(DeferredBlock<?> holder) {
        itemGenerated(holder, "");
    }
    private void itemGenerated(DeferredBlock<?> holder, String suffix) {
        String path = holder.getId().getPath();
        String loc = suffix.isEmpty() ? "block/"+path : "block/"+path+"_"+suffix;
        getBuilder(path).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc(loc));
    }
    private void itemGeneratedWithTexture(DeferredItem<?> deferred, ResourceLocation texture) {
        String path = deferred.getId().getPath();
        singleTexture(path, mcLoc("item/handheld"), "layer0", new ResourceLocation(texture.getNamespace(), "item/"+texture.getPath()));
    }

    private void withParentBlock(DeferredHolder<Block, ? extends Block> holder) {
        String path = holder.getId().getPath();
        withExistingParent(path, modLoc("block/"+path));
    }

    private void withParentItem(DeferredItem<?> holder, DeferredItem<?> parent) {
        withExistingParent(holder.getId().getPath(), new ResourceLocation(parent.getId().getNamespace(),"item/"+parent.getId().getPath()));
    }
    private void withParentItem(DeferredItem<?> holder, Item parent) {
        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(parent);
        withExistingParent(holder.getId().getPath(), new ResourceLocation(loc.getNamespace(),"item/"+loc.getPath()));
    }
    private void withParentItemPath(DeferredItem<?> holder, String path) {
        withExistingParent(holder.getId().getPath(), mcLoc("item/"+path));
    }
    private void spawnEggItem(DeferredItem<?> holder) {
        withParentItemPath(holder, "template_spawn_egg");
    }

    private void generateTwoStateItem(DeferredItem<?> holder, String activePrefix) {
        String path = holder.getId().getPath();
        basicItem(modLoc(path+"_"+activePrefix));
        ModelFile active = withExistingParent(path+"_"+activePrefix, "item/generated");
        basicItem(modLoc(path)).override().model(active).predicate(modLoc(activePrefix), 1.0F);
    }
}
