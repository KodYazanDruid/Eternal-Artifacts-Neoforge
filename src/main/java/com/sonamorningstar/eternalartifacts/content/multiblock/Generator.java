package com.sonamorningstar.eternalartifacts.content.multiblock;

import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public class Generator extends Multiblock {

	public Generator(BlockPattern pattern, int masterPalmOffset, int masterThumbOffset, int masterFingerOffset, MultiblockCapabilityManager capabilityManager) {
		super(pattern, ModMultiblocks.GENERATOR::getBlock, masterPalmOffset, masterThumbOffset, masterFingerOffset, capabilityManager, false);
	}
}
