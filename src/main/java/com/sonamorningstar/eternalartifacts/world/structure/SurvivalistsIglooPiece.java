package com.sonamorningstar.eternalartifacts.world.structure;

import com.sonamorningstar.eternalartifacts.core.structure.ModStructurePieces;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SurvivalistsIglooPiece extends TemplateStructurePiece {
    public static final ResourceLocation STRUCTURE = new ResourceLocation(MODID, "survivalists_igloo");

    public SurvivalistsIglooPiece(StructureTemplateManager manager, BlockPos pos,  Rotation rot, BlockPos pivotPos) {
        super(ModStructurePieces.SURVIVALISTS_IGLOO_PIECE.get(),
            0,
            manager,
            STRUCTURE, STRUCTURE.toString(),
            makeSettings(rot, pivotPos),
            pos
        );
    }

    public SurvivalistsIglooPiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(ModStructurePieces.SURVIVALISTS_IGLOO_PIECE.get(), tag, ctx.structureTemplateManager(), rl -> {
            var template = ctx.structureTemplateManager().getOrCreate(rl);
            return makeSettings(Rotation.valueOf(tag.getString("Rotation")),
                new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2)
            );
        });
    }

    private static StructurePlaceSettings makeSettings(Rotation rot, BlockPos pivotPos) {
        return new StructurePlaceSettings()
            .setRotation(rot)
            .setMirror(Mirror.NONE)
            .setRotationPivot(pivotPos)
            .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
    }
    
    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super.addAdditionalSaveData(ctx, tag);
        tag.putString("Rotation", this.placeSettings.getRotation().name());
        tag.putString("Mirror", this.placeSettings.getMirror().name());
    }

    @Override
    protected void handleDataMarker(String pName, BlockPos pos, ServerLevelAccessor level, RandomSource rand, BoundingBox box) {}
}