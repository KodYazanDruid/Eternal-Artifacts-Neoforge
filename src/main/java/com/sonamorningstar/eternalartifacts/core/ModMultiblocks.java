package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.base.MultiblockBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.GeneratorBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.PumpjackBlockEntity;
import com.sonamorningstar.eternalartifacts.content.multiblock.Generator;
import com.sonamorningstar.eternalartifacts.content.multiblock.Pumpjack;
import com.sonamorningstar.eternalartifacts.content.multiblock.ChunkEater;
import com.sonamorningstar.eternalartifacts.content.block.entity.ChunkEaterBlockEntity;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import com.sonamorningstar.eternalartifacts.registrar.MultiblockDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.MultiblockDeferredRegister;
import net.minecraft.core.Direction;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMultiblocks {
	public static final MultiblockDeferredRegister MULTIBLOCKS = new MultiblockDeferredRegister(MODID);
	
	public static final MultiblockDeferredHolder<Pumpjack, MultiblockBlock, PumpjackBlockEntity> PUMPJACK = MULTIBLOCKS.register("pumpjack",
		PumpjackBlockEntity::new, Pumpjack::new, 1, 3, 2,
		1, 3, 2,
		new MultiblockCapabilityManager()
			.addCapabilityAllDirections(1, 3, 4, MultiblockCapabilityManager.CapabilityType.ENERGY)
			.addCapabilityAllDirections(1, 3, 0, MultiblockCapabilityManager.CapabilityType.FLUID)
	);
	
	public static final MultiblockDeferredHolder<Generator, MultiblockBlock, GeneratorBlockEntity> GENERATOR = MULTIBLOCKS.register("generator",
		GeneratorBlockEntity::new, Generator::new, 1, 1, 0,
		1, 1, 0,
		new MultiblockCapabilityManager()
			.addCapability(1, 1, 2, MultiblockCapabilityManager.CapabilityType.ENERGY, Direction.NORTH)
			.addCapability(2, 1, 1, MultiblockCapabilityManager.CapabilityType.FLUID, Direction.WEST)
			.addCapability(0, 1, 1, MultiblockCapabilityManager.CapabilityType.ITEM, Direction.EAST)
	);

	public static final MultiblockDeferredHolder<ChunkEater, MultiblockBlock, ChunkEaterBlockEntity> CHUNK_EATER = MULTIBLOCKS.register("chunk_eater",
		ChunkEaterBlockEntity::new, ChunkEater::new, 2, 1, 0,
		2, 1, 0,
		new MultiblockCapabilityManager()
			.addCapabilityAllDirections(2, 1, 4, MultiblockCapabilityManager.CapabilityType.ENERGY)
			.addCapabilityAllDirections(0, 1, 2, MultiblockCapabilityManager.CapabilityType.ITEM)
			.addCapabilityAllDirections(2, 1, 0, MultiblockCapabilityManager.CapabilityType.ITEM)
			.addCapabilityAllDirections(4, 1, 2, MultiblockCapabilityManager.CapabilityType.FLUID)
	);
	
}
