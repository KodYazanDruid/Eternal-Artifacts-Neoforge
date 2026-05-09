package com.sonamorningstar.eternalartifacts.content.multiblock;

import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;

public class ChunkEater extends Multiblock {

    public ChunkEater(int masterPalmOffset, int masterThumbOffset, int masterFingerOffset,
                      int clickablePalmOffset, int clickableThumbOffset, int clickableFingerOffset,
                      MultiblockCapabilityManager capabilityManager) {
        super(ModMultiblocks.CHUNK_EATER::getBlock, masterPalmOffset, masterThumbOffset, masterFingerOffset,
                clickablePalmOffset, clickableThumbOffset, clickableFingerOffset, capabilityManager, false);
    }

}
