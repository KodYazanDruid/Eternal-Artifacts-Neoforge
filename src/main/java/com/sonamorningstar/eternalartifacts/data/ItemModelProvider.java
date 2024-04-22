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

        basicItem(modLoc("encumbator_active"));

        ModelFile encumbator = withExistingParent(ModItems.ENCUMBATOR.getId().getPath()+"_active", "item/generated");
        basicItem(modLoc("encumbator")).override().model(encumbator).predicate(modLoc("active"), 1.0F);

        withExistingParent(ModItems.DEMON_EYE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

        withExistingParent(ModBlocks.ANVILINATOR.getId().getPath(), modLoc("block/anvilinator"));
        withExistingParent(ModBlocks.RESONATOR.getId().getPath(), modLoc("block/resonator"));
        withExistingParent(ModBlocks.GARDENING_POT.getId().getPath(), modLoc("block/gardening_pot"));

        withExistingParent(ModItems.NOUS_BUCKET.getId().getPath(), new ResourceLocation("neoforge", "item/bucket_drip"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(ModFluids.NOUS_SOURCE.get())
                .applyTint(true);
    }

    private void handheld(DeferredItem<Item> deferred) {
        singleTexture(deferred.getId().getPath(), mcLoc("item/handheld"), "layer0", modLoc("item/" + deferred.getId().getPath()));
    }
}
