package com.sonamorningstar.eternalartifacts.content.multiblock;

import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public class Pumpjack extends Multiblock {

	public Pumpjack(BlockPattern pattern) {
		super(pattern, ModBlocks.PUMPJACK::get, 1, 3, 2);
	}
}
