package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Consumer;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModFluidTypes {
    public static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");
    public static final ResourceLocation GENERIC_LIQUID_STILL = new ResourceLocation(MODID, "block/generic_liquid_still");
    public static final ResourceLocation GENERIC_LIQUID_FLOW = new ResourceLocation(MODID, "block/generic_liquid_flow");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);

    public static final DeferredHolder<FluidType, FluidType> NOUS = register("nous", 7, 3000, 4500, Rarity.EPIC, 38, 178, 82);
    public static final DeferredHolder<FluidType, FluidType> LIQUID_MEAT = register("liquid_meat", 0, 6000, 5000, Rarity.RARE, 23, 61, 49);
    public static final DeferredHolder<FluidType, FluidType> PINK_SLIME = register("pink_slime", 0, 5000, 4500, Rarity.RARE, 201, 87, 185);
    public static final DeferredHolder<FluidType, FluidType> BLOOD = register("blood", 0, 4000, 3500, Rarity.RARE, 186, 26, 16);
    public static final DeferredHolder<FluidType, FluidType> LIQUID_PLASTIC = register("liquid_plastic", 0, 4500, 3000, Rarity.RARE, 232, 225, 213);
    public static final DeferredHolder<FluidType, FluidType> BEER = register("beer", 0, 1000, 1000, Rarity.COMMON, 153, 131, 36);
    public static final DeferredHolder<FluidType, FluidType> CRUDE_OIL = register("crude_oil", 0, 3500, 3500, Rarity.UNCOMMON, 23, 21, 22);

    private static DeferredHolder<FluidType, FluidType> register(String name, int light, int density, int viscosity, Rarity rarity, int fogX, int fogY, int fogZ) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(
                new ResourceLocation(MODID,"block/"+name+"_still"), new ResourceLocation(MODID,"block/"+name+"_flow"),
                WATER_OVERLAY, 0xFFFFFFFF, new Vector3f(fogX / 255f, fogY / 255f, fogZ / 255f),
                FluidType.Properties.create().lightLevel(light).density(density).viscosity(viscosity).rarity(rarity)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)
        ));
    }



}
