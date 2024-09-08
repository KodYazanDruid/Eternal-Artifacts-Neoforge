package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.registrar.FluidDeferredHolder;
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
        basicItem(ModItems.RAW_ARDITE.get());
        basicItem(ModItems.ARDITE_INGOT.get());
        basicItem(ModItems.TAR_BALL.get());
        basicItem(ModItems.BITUMEN.get());
        basicItem(ModItems.PINK_SLIME_INGOT.get());
        handheld(ModItems.GLASSCUTTER);
        handheld(ModItems.WITHERING_SWORD);
        basicItem(ModItems.FEEDING_CANISTER.get());
        basicItem(ModItems.DEMON_INGOT.get());
        basicItem(ModItems.DEMONIC_TABLET.get());
        handheld(ModItems.GRAFTER);

        basicItem(modLoc("encumbator_active"));
        ModelFile encumbator = withExistingParent(ModItems.ENCUMBATOR.getId().getPath()+"_active", "item/generated");
        basicItem(modLoc("encumbator")).override().model(encumbator).predicate(modLoc("active"), 1.0F);

        withParentItem(ModItems.ENCHANTED_GOLDEN_ANCIENT_FRUIT, ModItems.GOLDEN_ANCIENT_FRUIT);

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
        withParentBlock(ModBlocks.DIAMOND_DRUM);
        withParentBlock(ModBlocks.NETHERITE_DRUM);
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

        ModFluids.FLUIDS.getEntries().forEach(this::bucketItem);
        ModMachines.MACHINES.getMachines().forEach(holder -> {
            if(!holder.isHasCustomRender()) withParentBlock(holder.getBlockHolder());
        });
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
    private void bucketItem(FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> holder) {
        withExistingParent(holder.getBucketItemHolder().getId().getPath(), new ResourceLocation("neoforge", "item/bucket_drip"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(holder.getFluid())
                .applyTint(true);
    }

    private void withParentBlock(DeferredHolder<Block, ? extends Block> holder) {
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
