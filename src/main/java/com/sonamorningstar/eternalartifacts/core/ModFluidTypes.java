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
    public static final ResourceLocation NOUS_OVERLAY_RL = new ResourceLocation("misc/nous_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);

    public static final DeferredHolder<FluidType, FluidType> NOUS = FLUID_TYPES.register("nous", () -> new BaseFluidType(
            new ResourceLocation(MODID,"block/nous_still"), new ResourceLocation(MODID,"block/nous_flow"),
            NOUS_OVERLAY_RL, 0xFFFFFFFF, new Vector3f(142 / 255f, 255 / 255f, 14 / 255f),
            FluidType.Properties.create().lightLevel(7).density(300).viscosity(450).rarity(Rarity.EPIC)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)));

    /*public static final DeferredHolder<FluidType, FluidType> NOUS = FLUID_TYPES.register("nous", ()-> new FluidType(
            FluidType.Properties.create().density(300).viscosity(450).rarity(Rarity.EPIC).lightLevel(7)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)
            ) {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return IClientFluidTypeExtensions.super.getStillTexture();
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return IClientFluidTypeExtensions.super.getFlowingTexture();
                }

                @Override
                public int getTintColor() {
                    return IClientFluidTypeExtensions.super.getTintColor();
                }
            });
        }
    });*/

}
