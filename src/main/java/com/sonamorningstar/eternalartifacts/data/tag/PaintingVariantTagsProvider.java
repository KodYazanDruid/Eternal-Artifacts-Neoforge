package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModPaintings;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.PaintingVariantTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class PaintingVariantTagsProvider extends net.minecraft.data.tags.PaintingVariantTagsProvider {
    public PaintingVariantTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(PaintingVariantTags.PLACEABLE).add(ModPaintings.THE_LAST_SUPPER.getKey());
    }
}
