package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class FluidTagsProvider extends net.minecraft.data.tags.FluidTagsProvider {
    public FluidTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Fluids.EXPERIENCE).add(ModFluids.NOUS.get());
        tag(ModTags.Fluids.MEAT).add(ModFluids.LIQUID_MEAT.get());
        tag(ModTags.Fluids.PINK_SLIME).add(ModFluids.PINK_SLIME.get());
        tag(ModTags.Fluids.BLOOD).add(ModFluids.BLOOD.get());
        tag(ModTags.Fluids.PLASTIC).add(ModFluids.LIQUID_PLASTIC.get());
    }
}
