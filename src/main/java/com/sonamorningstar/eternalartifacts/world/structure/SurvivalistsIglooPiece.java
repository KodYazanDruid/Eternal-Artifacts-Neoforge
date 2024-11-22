package com.sonamorningstar.eternalartifacts.world.structure;

import com.sonamorningstar.eternalartifacts.core.ModLootTables;
import com.sonamorningstar.eternalartifacts.core.structure.ModStructurePieces;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SurvivalistsIglooPiece extends TemplateStructurePiece {
    private boolean placedMainChest;
    private static final ResourceLocation STRUCTURE = new ResourceLocation(MODID, "survivalists_igloo");

    public SurvivalistsIglooPiece(StructureTemplateManager manager, BlockPos pos,  Rotation rot) {
        super(ModStructurePieces.SURVIVALISTS_IGLOO_PIECE.get(),
                0,
                manager,
                STRUCTURE, STRUCTURE.toString(),
                makeSettings(rot),
                pos
        );
    }

    public SurvivalistsIglooPiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(ModStructurePieces.SURVIVALISTS_IGLOO_PIECE.get(), tag, ctx.structureTemplateManager(), rl -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
        placedMainChest = tag.getBoolean("placedMainChest");
    }

    private static StructurePlaceSettings makeSettings(Rotation rot) {
        return new StructurePlaceSettings()
                .setRotation(rot)
                .setMirror(Mirror.NONE)
                .setRotationPivot(new BlockPos(4, 2, 3))
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super.addAdditionalSaveData(ctx, tag);
        tag.putBoolean("placedMainChest", placedMainChest);
        tag.putString("Rot", this.placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(String pName, BlockPos pos, ServerLevelAccessor level, RandomSource rand, BoundingBox box) {
/*        System.out.println("0");
        if ("chest".equals(pName)) {
            System.out.println("1");
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            BlockEntity blockentity = level.getBlockEntity(pos.below());
            if (blockentity instanceof ChestBlockEntity) {
                System.out.println("2");
                ((ChestBlockEntity)blockentity).setLootTable(ModLootTables.SURVIVALISTS_IGLOO, rand.nextLong());
            }
        }*/
    }


}
