package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class FluidTagsProvider extends net.minecraft.data.tags.FluidTagsProvider {
    public FluidTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Fluids.EXPERIENCE).add(ModFluids.NOUS.getFluid());
        tag(ModTags.Fluids.MEAT).add(ModFluids.LIQUID_MEAT.getFluid());
        tag(ModTags.Fluids.PINK_SLIME).add(ModFluids.PINK_SLIME.getFluid());
        tag(ModTags.Fluids.BLOOD).add(ModFluids.BLOOD.getFluid());
        tag(ModTags.Fluids.PLASTIC).add(ModFluids.LIQUID_PLASTIC.getFluid());
        tag(ModTags.Fluids.CRUDE_OIL).add(ModFluids.CRUDE_OIL.getFluid());
        tag(ModTags.Fluids.GASOLINE).add(ModFluids.GASOLINE.getFluid());
        tag(ModTags.Fluids.DIESEL).add(ModFluids.DIESEL.getFluid());
        tag(ModTags.Fluids.NAPHTHA).add(ModFluids.NAPHTHA.getFluid());
        tag(FluidTags.WATER).add(ModFluids.HOT_SPRING_WATER.getFluid(), ModFluids.HOT_SPRING_WATER.getFlowingFluid());
        tag(ModTags.Fluids.POTION).add(ModFluids.POTION.getFluid());
    }
}
