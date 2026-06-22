package com.sonamorningstar.eternalartifacts.content.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record Coolant(int coolantValue, int consumedPerTick) {
	public static final Codec<Coolant> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		ExtraCodecs.POSITIVE_INT.fieldOf("coolant_value").forGetter(Coolant::coolantValue),
		ExtraCodecs.POSITIVE_INT.fieldOf("consumed_per_tick").forGetter(Coolant::consumedPerTick)
	).apply(inst, Coolant::new));
}
