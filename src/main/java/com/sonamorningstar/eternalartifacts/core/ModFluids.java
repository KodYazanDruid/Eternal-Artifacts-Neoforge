package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.fluid.HotSpringWaterLiquidBlock;
import com.sonamorningstar.eternalartifacts.content.fluid.PinkSlimeLiquidBlock;
import com.sonamorningstar.eternalartifacts.registrar.FluidDeferredRegister;
import com.sonamorningstar.eternalartifacts.registrar.GenericLiquidHolder;
import com.sonamorningstar.eternalartifacts.registrar.LiquidBlockFluidHolder;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MapColor;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModFluids {
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(MODID);

    public static final GenericLiquidHolder NOUS = FLUIDS.register(
            "nous", 7, 3000, 4500, Rarity.EPIC,
            38, 178, 82, MapColor.COLOR_LIGHT_GREEN, false
    );
    public static final GenericLiquidHolder LIQUID_MEAT = FLUIDS.register(
            "liquid_meat", 0, 6000, 5000, Rarity.RARE,
            23, 61, 49, MapColor.COLOR_BROWN, false
    );
    public static final LiquidBlockFluidHolder<PinkSlimeLiquidBlock> PINK_SLIME = FLUIDS.register(
            "pink_slime", PinkSlimeLiquidBlock::new, 0, 5000, 4500, Rarity.RARE,
            201, 87, 185, MapColor.COLOR_PINK, false
    );
    public static final GenericLiquidHolder BLOOD = FLUIDS.register(
            "blood", 0, 4000, 3500, Rarity.RARE,
            186, 26, 16, MapColor.COLOR_RED, false
    );
    public static final GenericLiquidHolder LIQUID_PLASTIC = FLUIDS.register(
            "liquid_plastic", 0, 4500, 3000, Rarity.RARE,
            232, 225, 213, MapColor.TERRACOTTA_WHITE, false
    );
    public static final GenericLiquidHolder BEER = FLUIDS.register(
            "beer", 0, 1000, 1000, Rarity.COMMON,
            153, 131, 36, MapColor.TERRACOTTA_ORANGE, false
    );
    public static final GenericLiquidHolder CRUDE_OIL = FLUIDS.register(
            "crude_oil", 0, 3500, 3500, Rarity.UNCOMMON,
            23, 21, 22, MapColor.COLOR_BLACK, false
    );
    public static final GenericLiquidHolder GASOLINE = FLUIDS.register(
            "gasoline", 0, 2000, 4000, Rarity.RARE,
            51, 88, 77, MapColor.TERRACOTTA_YELLOW, true, 0xFFF5C92D
    );
    public static final GenericLiquidHolder DIESEL = FLUIDS.register(
            "diesel", 0, 2000, 4000, Rarity.RARE,
            22, 54, 58, MapColor.TERRACOTTA_BROWN, true, 0xFFFDA50A
    );
    public static final LiquidBlockFluidHolder<HotSpringWaterLiquidBlock> HOT_SPRING_WATER = FLUIDS.register(
            "hot_spring_water", HotSpringWaterLiquidBlock::new, 0, 1000, 1000, Rarity.RARE,
            22, 54, 58, MapColor.COLOR_LIGHT_BLUE, true, 0xC500A4B3
    );

}
