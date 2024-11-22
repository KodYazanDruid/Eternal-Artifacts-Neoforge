package com.sonamorningstar.eternalartifacts.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.core.structure.ModStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class SurvivalistsIglooStructure extends Structure {
    public static final Codec<SurvivalistsIglooStructure> CODEC = RecordCodecBuilder.create(inst -> inst.group(settingsCodec(inst)).apply(inst, SurvivalistsIglooStructure::new));

    public SurvivalistsIglooStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        return onTopOfChunkCenter(ctx, Heightmap.Types.WORLD_SURFACE_WG, builder -> generatePieces(builder, ctx));
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext ctx) {
        ChunkPos chunkPos = ctx.chunkPos();
        WorldgenRandom worldgenrandom = ctx.random();
        int x = Mth.randomBetweenInclusive(worldgenrandom, chunkPos.getMinBlockX(), chunkPos.getMaxBlockX());
        int z = Mth.randomBetweenInclusive(worldgenrandom, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ());
        int y = adjustForTerrain(ctx, x, z);
        Rotation rotation = Rotation.getRandom(worldgenrandom);
        builder.addPiece(new SurvivalistsIglooPiece(ctx.structureTemplateManager(), new BlockPos(x, y, z), rotation));
    }

    private int adjustForTerrain(Structure.GenerationContext context, int x, int z) {
        return Mth.clamp(
                context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()),
                0, 256);
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.SURVIVALISTS_IGLOO.get();
    }
}
