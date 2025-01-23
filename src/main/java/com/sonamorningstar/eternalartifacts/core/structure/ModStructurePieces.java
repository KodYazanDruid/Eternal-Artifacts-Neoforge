package com.sonamorningstar.eternalartifacts.core.structure;

import com.sonamorningstar.eternalartifacts.world.structure.PlainsHousePiece;
import com.sonamorningstar.eternalartifacts.world.structure.SurvivalistsIglooPiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModStructurePieces {
    public static final DeferredRegister<StructurePieceType> PIECE_TYPE = DeferredRegister.create(Registries.STRUCTURE_PIECE, MODID);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> SURVIVALISTS_IGLOO_PIECE = register("EASI", SurvivalistsIglooPiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> PLAINS_HOUSE_PIECE = register("EAPH", PlainsHousePiece::new);


    private static <S extends StructurePieceType> DeferredHolder<StructurePieceType, S> register(String pieceId, S pieceType) {
        return PIECE_TYPE.register(pieceId.toLowerCase(Locale.ROOT), () -> pieceType);
    }

}
