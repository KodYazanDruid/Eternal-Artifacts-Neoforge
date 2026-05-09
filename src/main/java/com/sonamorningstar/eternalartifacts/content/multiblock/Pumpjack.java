package com.sonamorningstar.eternalartifacts.content.multiblock;

import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public class Pumpjack extends Multiblock {
	
	public Pumpjack(int masterPalmOffset, int masterThumbOffset, int masterFingerOffset,
					int clickablePalmOffset, int clickableThumbOffset, int clickableFingerOffset,
					MultiblockCapabilityManager capabilityManager) {
		super(ModMultiblocks.PUMPJACK::getBlock, masterPalmOffset, masterThumbOffset, masterFingerOffset,
				clickablePalmOffset, clickableThumbOffset, clickableFingerOffset, capabilityManager, false);
	}
	
}
