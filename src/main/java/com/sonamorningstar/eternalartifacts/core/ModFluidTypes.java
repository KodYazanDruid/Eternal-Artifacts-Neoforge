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

    public static final DeferredHolder<FluidType, FluidType> NOUS = FLUID_TYPES.register("nous", () -> new BaseFluidType(
            new ResourceLocation(MODID,"block/nous_still"), new ResourceLocation(MODID,"block/nous_flow"),
            //GENERIC_LIQUID_STILL, GENERIC_LIQUID_FLOW,
            WATER_OVERLAY, 0xFFFFFFFF/*0xff27ba5a*/, new Vector3f(38 / 255f, 178 / 255f, 82 / 255f),
            FluidType.Properties.create().lightLevel(7).density(3000).viscosity(4500).rarity(Rarity.EPIC)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)));
    public static final DeferredHolder<FluidType, FluidType> LIQUID_MEAT = FLUID_TYPES.register("liquid_meat", () -> new BaseFluidType(
            new ResourceLocation(MODID,"block/liquid_meat_still"), new ResourceLocation(MODID,"block/liquid_meat_flow"),
            //GENERIC_LIQUID_STILL, GENERIC_LIQUID_FLOW,
            WATER_OVERLAY, 0xFFFFFFFF/*0xffd3aa8b*/, new Vector3f(23 / 255f, 61 / 255f, 49 / 255f),
            FluidType.Properties.create().lightLevel(0).density(6000).viscosity(5000).rarity(Rarity.RARE)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)));
    public static final DeferredHolder<FluidType, FluidType> PINK_SLIME = FLUID_TYPES.register("pink_slime", () -> new BaseFluidType(
            new ResourceLocation(MODID,"block/pink_slime_still"), new ResourceLocation(MODID,"block/pink_slime_flow"),
            //GENERIC_LIQUID_STILL, GENERIC_LIQUID_FLOW,
            WATER_OVERLAY, 0xFFFFFFFF/*0xffc643bb*/, new Vector3f(201 / 255f, 87 / 255f, 185 / 255f),
            FluidType.Properties.create().lightLevel(0).density(5000).viscosity(4500).rarity(Rarity.RARE)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)));
    public static final DeferredHolder<FluidType, FluidType> BLOOD = FLUID_TYPES.register("blood", () -> new BaseFluidType(
            new ResourceLocation(MODID,"block/blood_still"), new ResourceLocation(MODID,"block/blood_flow"),
            //GENERIC_LIQUID_STILL, GENERIC_LIQUID_FLOW,
            WATER_OVERLAY, 0xFFFFFFFF/*0xffb80d08*/, new Vector3f(186 / 255f, 26 / 255f, 16 / 255f),
            FluidType.Properties.create().lightLevel(0).density(4000).viscosity(3500).rarity(Rarity.RARE)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)));



}
