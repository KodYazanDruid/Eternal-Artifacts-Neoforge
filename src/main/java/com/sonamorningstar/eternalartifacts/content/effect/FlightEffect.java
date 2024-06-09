package com.sonamorningstar.eternalartifacts.content.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.Objects;

public class FlightEffect extends MobEffect {
    public FlightEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }
}
