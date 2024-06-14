package com.sonamorningstar.eternalartifacts.registrar;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class FluidRegistry extends DeferredHolder<Fluid, BaseFlowingFluid> {
    protected FluidRegistry(ResourceKey<Fluid> key) {
        super(key);
    }

    /*public BaseFlowingFluid.Source createSource() {
        return
    }*/




}
