package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
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
        basicItem(ModItems.MEAT_INGOT.get());
        basicItem(ModItems.PINK_SLIME.get());
        basicItem(ModItems.ENDER_POUCH.get());
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
        basicItem(ModItems.COPPER_SWORD.get());
        basicItem(ModItems.COPPER_PICKAXE.get());
        basicItem(ModItems.COPPER_AXE.get());
        basicItem(ModItems.COPPER_SHOVEL.get());
        basicItem(ModItems.COPPER_HOE.get());
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

        basicItem(modLoc("encumbator_active"));
        ModelFile encumbator = withExistingParent(ModItems.ENCUMBATOR.getId().getPath()+"_active", "item/generated");
        basicItem(modLoc("encumbator")).override().model(encumbator).predicate(modLoc("active"), 1.0F);

        withExistingParent(ModItems.DEMON_EYE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PINKY_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.DUCK_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ENCHANTED_GOLDEN_ANCIENT_FRUIT.getId().getPath(), modLoc("item/golden_ancient_fruit"));

        //needs functions to write these
        withExistingParent(ModBlocks.RESONATOR.getId().getPath(), modLoc("block/resonator"));
        withExistingParent(ModBlocks.GARDENING_POT.getId().getPath(), modLoc("block/gardening_pot"));
        withExistingParent(ModBlocks.FANCY_CHEST.getId().getPath(), modLoc("block/fancy_chest"));
        withExistingParent(ModBlocks.PINK_SLIME_BLOCK.getId().getPath(), modLoc("block/pink_slime_block"));
        withExistingParent(ModBlocks.ROSY_FROGLIGHT.getId().getPath(), modLoc("block/rosy_froglight"));
        withExistingParent(ModBlocks.MACHINE_BLOCK.getId().getPath(), modLoc("block/machine_block"));
        withExistingParent(ModBlocks.CITRUS_LOG.getId().getPath(), modLoc("block/citrus_log"));
        withExistingParent(ModBlocks.STRIPPED_CITRUS_LOG.getId().getPath(), modLoc("block/stripped_citrus_log"));
        withExistingParent(ModBlocks.CITRUS_WOOD.getId().getPath(), modLoc("block/citrus_wood"));
        withExistingParent(ModBlocks.STRIPPED_CITRUS_WOOD.getId().getPath(), modLoc("block/stripped_citrus_wood"));
        withExistingParent(ModBlocks.COPPER_ORE_BERRY.getId().getPath(), modLoc("block/copper_oreberry_stage1"));
        withExistingParent(ModBlocks.IRON_ORE_BERRY.getId().getPath(), modLoc("block/iron_oreberry_stage1"));
        withExistingParent(ModBlocks.GOLD_ORE_BERRY.getId().getPath(), modLoc("block/gold_oreberry_stage1"));
        withExistingParent(ModBlocks.EXPERIENCE_ORE_BERRY.getId().getPath(), modLoc("block/experience_oreberry_stage1"));
        withExistingParent(ModBlocks.MANGANESE_ORE_BERRY.getId().getPath(), modLoc("block/manganese_oreberry_stage1"));
        withExistingParent(ModBlocks.BATTERY_BOX.getId().getPath(), modLoc("block/battery_box"));
        withExistingParent(ModBlocks.JAR.getId().getPath(), modLoc("block/jar"));
        //withExistingParent(ModBlocks.FOUR_LEAF_CLOVER.getId().getPath(), modLoc("block/four_leaf_clover"));
        getBuilder(ModBlocks.FORSYTHIA.getId().getPath())
            .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("block/forsythia_upper"));
        getBuilder(ModBlocks.FOUR_LEAF_CLOVER.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("block/four_leaf_clover"));

        bucketItem(ModItems.NOUS_BUCKET, ModFluids.NOUS_SOURCE);
        bucketItem(ModItems.LIQUID_MEAT_BUCKET, ModFluids.LIQUID_MEAT_SOURCE);
        bucketItem(ModItems.PINK_SLIME_BUCKET, ModFluids.PINK_SLIME_SOURCE);
        bucketItem(ModItems.BLOOD_BUCKET, ModFluids.BLOOD_SOURCE);
    }

    private void handheld(DeferredItem<Item> deferred) {
        singleTexture(deferred.getId().getPath(), mcLoc("item/handheld"), "layer0", modLoc("item/" + deferred.getId().getPath()));
    }

    private void bucketItem(DeferredHolder<Item, BucketItem> bucket, DeferredHolder<Fluid, BaseFlowingFluid.Source> source) {
        withExistingParent(bucket.getId().getPath(), new ResourceLocation("neoforge", "item/bucket_drip"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(source.get())
                .applyTint(true);
    }
}
