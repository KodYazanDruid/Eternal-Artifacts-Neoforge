package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import com.sonamorningstar.eternalartifacts.registrar.FluidDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.FluidDeferredRegister;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModFluids {
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(MODID);

    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> NOUS = FLUIDS.register(
            "nous", 7, 3000, 4500, Rarity.EPIC, 38, 178, 82, MapColor.COLOR_LIGHT_GREEN, false
    );
    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> LIQUID_MEAT = FLUIDS.register(
            "liquid_meat", 0, 6000, 5000, Rarity.RARE, 23, 61, 49, MapColor.COLOR_BROWN, false
    );
    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> PINK_SLIME = FLUIDS.register(
            "pink_slime", 0, 5000, 4500, Rarity.RARE, 201, 87, 185, MapColor.COLOR_PINK, false
    );
    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> BLOOD = FLUIDS.register(
            "blood", 0, 4000, 3500, Rarity.RARE, 186, 26, 16, MapColor.COLOR_RED, false
    );
    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> LIQUID_PLASTIC = FLUIDS.register(
            "liquid_plastic", 0, 4500, 3000, Rarity.RARE, 232, 225, 213, MapColor.TERRACOTTA_WHITE, false
    );
    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> BEER = FLUIDS.register(
            "beer", 0, 1000, 1000, Rarity.COMMON, 153, 131, 36, MapColor.TERRACOTTA_ORANGE, false
    );
    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> CRUDE_OIL = FLUIDS.register(
            "crude_oil", 0, 3500, 3500, Rarity.UNCOMMON, 23, 21, 22, MapColor.COLOR_BLACK, false
    );
    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> GASOLINE = FLUIDS.register(
            "gasoline", 0, 2000, 4000, Rarity.RARE, 51, 88, 77, MapColor.TERRACOTTA_YELLOW, true, 0xF5C92D
    );
    public static final FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> DIESEL = FLUIDS.register(
            "diesel", 0, 2000, 4000, Rarity.RARE, 22, 54, 58, MapColor.TERRACOTTA_BROWN, true, 0xFDA50A
    );

}
