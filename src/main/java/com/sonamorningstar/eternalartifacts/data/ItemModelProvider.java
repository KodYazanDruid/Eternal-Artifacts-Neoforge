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
        handheld(ModItems.WRENCH);
        basicItem(ModItems.ENDER_PAPER.get());
        basicItem(ModItems.RAW_MANGANESE.get());
        basicItem(ModItems.KNAPSACK.get());
        spawnEggItem(ModItems.DEMON_EYE_SPAWN_EGG);
        spawnEggItem(ModItems.PINKY_SPAWN_EGG);
        spawnEggItem(ModItems.DUCK_SPAWN_EGG);
        spawnEggItem(ModItems.MAGICAL_BOOK_SPAWN_EGG);

        basicItem(modLoc("encumbator_active"));
        ModelFile encumbator = withExistingParent(ModItems.ENCUMBATOR.getId().getPath()+"_active", "item/generated");
        basicItem(modLoc("encumbator")).override().model(encumbator).predicate(modLoc("active"), 1.0F);

        withParentItem(ModItems.ENCHANTED_GOLDEN_ANCIENT_FRUIT, ModItems.ANCIENT_FRUIT);

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
        withExistingParent(ModBlocks.COPPER_ORE_BERRY.getId().getPath(), modLoc("block/copper_oreberry_stage1"));
        withExistingParent(ModBlocks.IRON_ORE_BERRY.getId().getPath(), modLoc("block/iron_oreberry_stage1"));
        withExistingParent(ModBlocks.GOLD_ORE_BERRY.getId().getPath(), modLoc("block/gold_oreberry_stage1"));
        withExistingParent(ModBlocks.EXPERIENCE_ORE_BERRY.getId().getPath(), modLoc("block/experience_oreberry_stage1"));
        withExistingParent(ModBlocks.MANGANESE_ORE_BERRY.getId().getPath(), modLoc("block/manganese_oreberry_stage1"));
        withParentBlock(ModBlocks.BATTERY_BOX);
        getBuilder(ModBlocks.FORSYTHIA.getId().getPath())
            .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("block/forsythia_upper"));
        getBuilder(ModBlocks.FOUR_LEAF_CLOVER.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("block/four_leaf_clover"));

        bucketItem(ModItems.NOUS_BUCKET, ModFluids.NOUS);
        bucketItem(ModItems.LIQUID_MEAT_BUCKET, ModFluids.LIQUID_MEAT);
        bucketItem(ModItems.PINK_SLIME_BUCKET, ModFluids.PINK_SLIME);
        bucketItem(ModItems.BLOOD_BUCKET, ModFluids.BLOOD);
        bucketItem(ModItems.LIQUID_PLASTIC_BUCKET, ModFluids.LIQUID_PLASTIC);
        bucketItem(ModItems.BEER_BUCKET, ModFluids.BEER);
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

    private void withParentBlock(DeferredBlock<?> holder) {
        String path = holder.getId().getPath();
        withExistingParent(path, modLoc("block/"+path));
    }

    private void withParentItem(DeferredItem<?> holder, DeferredItem<?> parent) {
        withExistingParent(holder.getId().getPath(), new ResourceLocation(parent.getId().getNamespace(),"item/"+parent.getId().getPath()));
    }
    private void withParentItemPath(DeferredItem<?> holder, String path) {
        withExistingParent(holder.getId().getPath(), mcLoc("item/"+path));
    }
    private void spawnEggItem(DeferredItem<?> holder) {
        withParentItemPath(holder, "template_spawn_egg");
    }
}
