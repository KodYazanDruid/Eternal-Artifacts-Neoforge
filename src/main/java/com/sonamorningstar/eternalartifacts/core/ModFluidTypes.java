package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation NOUS_OVERLAY_RL = new ResourceLocation("misc/nous_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);

    public static final DeferredHolder<FluidType, FluidType> NOUS = FLUID_TYPES.register("nous", () -> new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL,
            NOUS_OVERLAY_RL, 0xA18EFF0E, new Vector3f(142 / 255f, 255 / 255f, 14 / 255f),
            FluidType.Properties.create().lightLevel(7).density(300).viscosity(450).rarity(Rarity.EPIC)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)));

}
