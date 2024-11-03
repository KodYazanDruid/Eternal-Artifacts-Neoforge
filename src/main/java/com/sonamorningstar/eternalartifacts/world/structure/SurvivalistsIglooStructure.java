package com.sonamorningstar.eternalartifacts.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.core.ModStructureTypes;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

/*public class SurvivalistsIglooStructure extends SinglePieceStructure {
    public static final Codec<SurvivalistsIglooStructure> CODEC = RecordCodecBuilder.create(inst -> inst.group(settingsCodec(inst)).apply(inst, SurvivalistsIglooStructure::new));

    *//*public SurvivalistsIglooStructure(StructureSettings pSettings) {
        super(pSettings);
    }*//*

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext pContext) {
        return Optional.empty();
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.SURVIVALISTS_IGLOO.get();
    }
}*/
