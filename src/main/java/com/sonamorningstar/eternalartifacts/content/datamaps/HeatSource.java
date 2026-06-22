package com.sonamorningstar.eternalartifacts.content.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record HeatSource(int heatValue) {
	public static final Codec<HeatSource> HEAT_VALUE_CODEC = ExtraCodecs.POSITIVE_INT
		.xmap(HeatSource::new, HeatSource::heatValue);
	public static final Codec<HeatSource> CODEC = ExtraCodecs.withAlternative(
		RecordCodecBuilder.create(in -> in.group(
			ExtraCodecs.POSITIVE_INT.fieldOf("burn_time").forGetter(HeatSource::heatValue)).apply(in, HeatSource::new)),
		HEAT_VALUE_CODEC);
}
