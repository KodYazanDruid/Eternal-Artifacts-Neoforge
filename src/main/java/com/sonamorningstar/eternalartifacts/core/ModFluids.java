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
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> NOUS_FLOWING = FLUIDS.register("nous_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.NOUS_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_MEAT_SOURCE = FLUIDS.register("liquid_meat", ()-> new BaseFlowingFluid.Source(ModFluids.LIQUID_MEAT_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> LIQUID_MEAT_FLOWING = FLUIDS.register("liquid_meat_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.LIQUID_MEAT_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> PINK_SLIME_SOURCE = FLUIDS.register("pink_slime", ()-> new BaseFlowingFluid.Source(ModFluids.PINK_SLIME_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> PINK_SLIME_FLOWING = FLUIDS.register("pink_slime_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.PINK_SLIME_PROPERTIES));

    private static final BaseFlowingFluid.Properties NOUS_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.NOUS::value, NOUS_SOURCE::value, NOUS_FLOWING::value)
            .bucket(ModItems.NOUS_BUCKET::value)
            .block(ModBlocks.NOUS_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);
    private static final BaseFlowingFluid.Properties LIQUID_MEAT_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.LIQUID_MEAT::value, LIQUID_MEAT_SOURCE::value, LIQUID_MEAT_FLOWING::value)
            .bucket(ModItems.LIQUID_MEAT_BUCKET::value)
            .block(ModBlocks.LIQUID_MEAT_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);
    private static final BaseFlowingFluid.Properties PINK_SLIME_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.PINK_SLIME::value, PINK_SLIME_SOURCE::value, PINK_SLIME_FLOWING::value)
            .bucket(ModItems.PINK_SLIME_BUCKET::value)
            .block(ModBlocks.PINK_SLIME_FLUID_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);
}
