package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MODID);

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> NOUS_SOURCE = FLUIDS.register("nous", ()-> new BaseFlowingFluid.Source(ModFluids.NOUS_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> NOUS_FLOWING = FLUIDS.register("flowing_nous", ()-> new BaseFlowingFluid.Flowing(ModFluids.NOUS_PROPERTIES));

    private static final BaseFlowingFluid.Properties NOUS_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.NOUS::value, NOUS_SOURCE::value, NOUS_FLOWING::value).bucket(ModItems.NOUS_BUCKET::value);
}
