package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.fluid.HotSpringWaterLiquidBlock;
import com.sonamorningstar.eternalartifacts.content.fluid.PinkSlimeLiquidBlock;
import com.sonamorningstar.eternalartifacts.content.fluid.SludgeLiquidBlock;
import com.sonamorningstar.eternalartifacts.registrar.FluidHolder;
import com.sonamorningstar.eternalartifacts.registrar.FluidRegistration;
import com.sonamorningstar.eternalartifacts.registrar.FluidRegistry;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModFluids {
    public static final FluidRegistry FLUIDS = new FluidRegistry(MODID);

    public static final FluidHolder<LiquidBlock> NOUS = FLUIDS.register(
            FluidRegistration.create("nous")
                    .light(7)
                    .density(3000)
                    .viscosity(4500)
                    .rarity(Rarity.EPIC)
                    .color(38, 178, 82)
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> LIQUID_MEAT = FLUIDS.register(
            FluidRegistration.create("liquid_meat")
                    .density(6000)
                    .viscosity(5000)
                    .rarity(Rarity.RARE)
                    .color(23, 61, 49)
                    .mapColor(MapColor.COLOR_BROWN)
                    .build()
    );

    public static final FluidHolder<PinkSlimeLiquidBlock> PINK_SLIME = FLUIDS.register(
            FluidRegistration.create("pink_slime")
                    .block(PinkSlimeLiquidBlock::new)
                    .density(5000)
                    .viscosity(4500)
                    .rarity(Rarity.RARE)
                    .color(201, 87, 185)
                    .mapColor(MapColor.COLOR_PINK)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> BLOOD = FLUIDS.register(
            FluidRegistration.create("blood")
                    .density(4000)
                    .viscosity(3500)
                    .rarity(Rarity.RARE)
                    .color(186, 26, 16)
                    .mapColor(MapColor.COLOR_RED)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> LIQUID_PLASTIC = FLUIDS.register(
            FluidRegistration.create("liquid_plastic")
                    .density(4500)
                    .viscosity(3000)
                    .rarity(Rarity.RARE)
                    .color(232, 225, 213)
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> BEER = FLUIDS.register(
            FluidRegistration.create("beer")
                    .density(1000)
                    .viscosity(1000)
                    .rarity(Rarity.COMMON)
                    .color(153, 131, 36)
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> CRUDE_OIL = FLUIDS.register(
            FluidRegistration.create("crude_oil")
                    .density(1100)
                    .viscosity(2500)
                    .rarity(Rarity.UNCOMMON)
                    .color(43, 25, 7)
                    .mapColor(MapColor.COLOR_BLACK)
                    .genericTexture()
                    .tint(0xFF2B1907)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> GASOLINE = FLUIDS.register(
            FluidRegistration.create("gasoline")
                    .density(750)
                    .viscosity(400)
                    .rarity(Rarity.RARE)
                    .color(227, 242, 163)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
                    .genericTexture()
                    .tint(0xF0E3F2A3)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> DIESEL = FLUIDS.register(
            FluidRegistration.create("diesel")
                    .density(830)
                    .viscosity(900)
                    .rarity(Rarity.RARE)
                    .color(217, 164, 65)
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .genericTexture()
                    .tint(0xF0D9A441)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> NAPHTHA = FLUIDS.register(
            FluidRegistration.create("naphtha")
                    .density(850)
                    .viscosity(600)
                    .rarity(Rarity.RARE)
                    .color(245, 213, 128)
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .genericTexture()
                    .tint(0xF9F5D580)
                    .build()
    );

    public static final FluidHolder<HotSpringWaterLiquidBlock> HOT_SPRING_WATER = FLUIDS.register(
            FluidRegistration.create("hot_spring_water")
                    .block(HotSpringWaterLiquidBlock::new)
                    .density(1000)
                    .viscosity(1000)
                    .rarity(Rarity.RARE)
                    .color(22, 54, 58)
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .genericTexture()
                    .tint(0xC500A4B3)
                    .build()
    );

    public static final FluidHolder<SludgeLiquidBlock> SLUDGE = FLUIDS.register(
            FluidRegistration.create("sludge")
                    .block(SludgeLiquidBlock::new)
                    .density(4000)
                    .viscosity(2000)
                    .rarity(Rarity.UNCOMMON)
                    .color(44, 11, 1)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .genericTexture()
                    .tint(0xFF2C0B0B)
                    .build()
    );

    public static final FluidHolder<LiquidBlock> POTION = FLUIDS.registerPotion("potion", 0, 1000, 1000);
}
