package com.sonamorningstar.eternalartifacts.content.multiblock;

import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public class Generator extends Multiblock {
	
	public Generator(int masterPalmOffset, int masterThumbOffset, int masterFingerOffset,
					 int clickablePalmOffset, int clickableThumbOffset, int clickableFingerOffset,
					 MultiblockCapabilityManager capabilityManager) {
		super(ModMultiblocks.GENERATOR::getBlock, masterPalmOffset, masterThumbOffset, masterFingerOffset,
				clickablePalmOffset, clickableThumbOffset, clickableFingerOffset, capabilityManager, false);
	}
}
