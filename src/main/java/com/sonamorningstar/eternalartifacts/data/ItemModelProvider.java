package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
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
        basicItem(ModItems.MEAT_INGOT.get());
        basicItem(ModItems.PINK_SLIME.get());
        basicItem(ModItems.ENDER_POUCH.get());
        basicItem(ModItems.PORTABLE_CRAFTER.get());
        basicItem(ModItems.GOLDEN_ANCIENT_FRUIT.get());
        basicItem(ModItems.COMFY_SHOES.get());

        basicItem(modLoc("encumbator_active"));

        ModelFile encumbator = withExistingParent(ModItems.ENCUMBATOR.getId().getPath()+"_active", "item/generated");
        basicItem(modLoc("encumbator")).override().model(encumbator).predicate(modLoc("active"), 1.0F);

        withExistingParent(ModItems.DEMON_EYE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PINKY_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ENCHANTED_GOLDEN_ANCIENT_FRUIT.getId().getPath(), modLoc("item/golden_ancient_fruit"));

        withExistingParent(ModBlocks.ANVILINATOR.getId().getPath(), modLoc("block/anvilinator"));
        withExistingParent(ModBlocks.RESONATOR.getId().getPath(), modLoc("block/resonator"));
        withExistingParent(ModBlocks.GARDENING_POT.getId().getPath(), modLoc("block/gardening_pot"));
        withExistingParent(ModBlocks.FANCY_CHEST.getId().getPath(), modLoc("block/fancy_chest"));
        withExistingParent(ModBlocks.PINK_SLIME_BLOCK.getId().getPath(), modLoc("block/pink_slime_block"));
        withExistingParent(ModBlocks.ROSY_FROGLIGHT.getId().getPath(), modLoc("block/rosy_froglight"));

        withExistingParent(ModItems.NOUS_BUCKET.getId().getPath(), new ResourceLocation("neoforge", "item/bucket_drip"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(ModFluids.NOUS_SOURCE.get())
                .applyTint(true);
    }

    private void handheld(DeferredItem<Item> deferred) {
        singleTexture(deferred.getId().getPath(), mcLoc("item/handheld"), "layer0", modLoc("item/" + deferred.getId().getPath()));
    }
}
