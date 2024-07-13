package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MODID);

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> NOUS = FLUIDS.register("nous", ()-> new BaseFlowingFluid.Source(ModFluids.NOUS_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> NOUS_FLOWING = FLUIDS.register("nous_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.NOUS_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_MEAT = FLUIDS.register("liquid_meat", ()-> new BaseFlowingFluid.Source(ModFluids.LIQUID_MEAT_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> LIQUID_MEAT_FLOWING = FLUIDS.register("liquid_meat_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.LIQUID_MEAT_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> PINK_SLIME = FLUIDS.register("pink_slime", ()-> new BaseFlowingFluid.Source(ModFluids.PINK_SLIME_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> PINK_SLIME_FLOWING = FLUIDS.register("pink_slime_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.PINK_SLIME_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> BLOOD = FLUIDS.register("blood", ()-> new BaseFlowingFluid.Source(ModFluids.BLOOD_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> BLOOD_FLOWING = FLUIDS.register("blood_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.BLOOD_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_PLASTIC = FLUIDS.register("liquid_plastic", ()-> new BaseFlowingFluid.Source(ModFluids.LIQUID_PLASTIC_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> LIQUID_PLASTIC_FLOWING = FLUIDS.register("liquid_plastic_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.LIQUID_PLASTIC_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> BEER = FLUIDS.register("beer", ()-> new BaseFlowingFluid.Source(ModFluids.BEER_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> BEER_FLOWING = FLUIDS.register("beer_flow", ()-> new BaseFlowingFluid.Flowing(ModFluids.BEER_PROPERTIES));


    private static final BaseFlowingFluid.Properties NOUS_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.NOUS::value, NOUS::value, NOUS_FLOWING::value)
            .bucket(ModItems.NOUS_BUCKET::value)
            .block(ModBlocks.NOUS_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);
    private static final BaseFlowingFluid.Properties LIQUID_MEAT_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.LIQUID_MEAT::value, LIQUID_MEAT::value, LIQUID_MEAT_FLOWING::value)
            .bucket(ModItems.LIQUID_MEAT_BUCKET::value)
            .block(ModBlocks.LIQUID_MEAT_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);
    private static final BaseFlowingFluid.Properties PINK_SLIME_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.PINK_SLIME::value, PINK_SLIME::value, PINK_SLIME_FLOWING::value)
            .bucket(ModItems.PINK_SLIME_BUCKET::value)
            .block(ModBlocks.PINK_SLIME_FLUID_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);
    private static final BaseFlowingFluid.Properties BLOOD_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.BLOOD::value, BLOOD::value, BLOOD_FLOWING::value)
            .bucket(ModItems.BLOOD_BUCKET::value)
            .block(ModBlocks.BLOOD_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);
    private static final BaseFlowingFluid.Properties LIQUID_PLASTIC_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.LIQUID_PLASTIC::value, LIQUID_PLASTIC::value, LIQUID_PLASTIC_FLOWING::value)
            .bucket(ModItems.LIQUID_PLASTIC_BUCKET::value)
            .block(ModBlocks.LIQUID_PLASTIC_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);
    private static final BaseFlowingFluid.Properties BEER_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.BEER::value, BEER::value, BEER_FLOWING::value)
            .bucket(ModItems.BEER_BUCKET::value)
            .block(ModBlocks.BEER_BLOCK)
            .tickRate(10).levelDecreasePerBlock(2);

    //Hmmm
    private static BaseFlowingFluid.Source createSource(BaseFlowingFluid.Properties props) {
        return new BaseFlowingFluid.Source(props);
    }

    private static BaseFlowingFluid.Flowing createFlowing(BaseFlowingFluid.Properties props) {
        return new BaseFlowingFluid.Flowing(props);
    }

    private static BaseFlowingFluid.Properties createProps(
            DeferredHolder<FluidType, FluidType> fluidType,
            DeferredHolder<Fluid, BaseFlowingFluid.Source> source,
            DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing,
            DeferredItem<BucketItem> bucket,
            DeferredBlock<LiquidBlock> block, int tickRate, int decrease) {
        return new BaseFlowingFluid.Properties(fluidType::value, source::value, flowing::value)
                .bucket(bucket::value)
                .block(block)
                .tickRate(tickRate).levelDecreasePerBlock(decrease);
    }
}
