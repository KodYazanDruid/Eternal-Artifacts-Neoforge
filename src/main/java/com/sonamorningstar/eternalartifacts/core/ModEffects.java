package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.effect.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);

    public static final DeferredHolder<MobEffect, MobEffect> FLIGHT = EFFECTS.register("flight",
            () -> new FlightEffect(MobEffectCategory.BENEFICIAL,0x001e52)
                    .addAttributeModifier(NeoForgeMod.CREATIVE_FLIGHT.value(), "b51da91b-d856-4ab6-9ac7-43ceda5e2cc4", 1, AttributeModifier.Operation.ADDITION));
    public static final DeferredHolder<MobEffect, MobEffect> DIVINE_PROTECTION = EFFECTS.register("divine_protection",
            ()-> new DivineProtectionEffect(MobEffectCategory.BENEFICIAL, 0xe5ff7f)
                    .addAttributeModifier(Attributes.MAX_ABSORPTION, "e1b4a353-c7a2-4d4d-b696-09948785c84d", 20, AttributeModifier.Operation.ADDITION)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "af11528e-ace4-4f6d-83f6-fcc76d279b7f", 0.3, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> MALADY = EFFECTS.register("malady", ()-> new MaladyEffect(MobEffectCategory.HARMFUL, 0x525c49));
    public static final DeferredHolder<MobEffect, MobEffect> ANGLERS_LUCK = EFFECTS.register("anglers_luck", ()-> new AnglersLuckEffect(MobEffectCategory.BENEFICIAL, 0x22293b));
    public static final DeferredHolder<MobEffect, MobEffect> LURING = EFFECTS.register("luring", ()-> new LuringEffect(MobEffectCategory.BENEFICIAL, 0x226e8c));

}
