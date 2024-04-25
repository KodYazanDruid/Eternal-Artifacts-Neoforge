package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.effect.FlightEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);

    public static final DeferredHolder<MobEffect, MobEffect> FLIGHT = EFFECTS.register("flight",
            () -> new FlightEffect(MobEffectCategory.BENEFICIAL,0x001e52).addAttributeModifier(
                    NeoForgeMod.CREATIVE_FLIGHT.value(), "b51da91b-d856-4ab6-9ac7-43ceda5e2cc4", 1, AttributeModifier.Operation.ADDITION));

}
