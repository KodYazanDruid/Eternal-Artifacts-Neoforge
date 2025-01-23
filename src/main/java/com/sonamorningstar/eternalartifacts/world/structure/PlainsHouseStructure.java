package com.sonamorningstar.eternalartifacts.world.structure;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class PlainsHouseStructure extends Structure {
	public static final Codec<PlainsHouseStructure> CODEC = simpleCodec(PlainsHouseStructure::new);
	
	public PlainsHouseStructure(StructureSettings settings) {
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
		StructureTemplate template = ctx.structureTemplateManager().getOrCreate(PlainsHousePiece.STRUCTURE);
		BlockPos pivotPos = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
		builder.addPiece(new PlainsHousePiece(ctx.structureTemplateManager(), new BlockPos(x, y, z), rotation, pivotPos));
	}
	
	private int adjustForTerrain(Structure.GenerationContext context, int x, int z) {
		return Mth.clamp(
			context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()),
			0, 256);
	}
	
	@Override
	public StructureType<?> type() {
		return ModStructureTypes.PLAINS_HOUSE.get();
	}
}
