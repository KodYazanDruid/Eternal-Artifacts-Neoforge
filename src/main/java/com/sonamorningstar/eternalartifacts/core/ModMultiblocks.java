package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.base.MultiblockBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.GeneratorMB;
import com.sonamorningstar.eternalartifacts.content.block.entity.PumpjackMB;
import com.sonamorningstar.eternalartifacts.content.multiblock.Generator;
import com.sonamorningstar.eternalartifacts.content.multiblock.Pumpjack;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import com.sonamorningstar.eternalartifacts.registrar.MultiblockDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.MultiblockDeferredRegister;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.neoforged.neoforge.common.Tags;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMultiblocks {
	public static final MultiblockDeferredRegister MULTIBLOCKS = new MultiblockDeferredRegister(MODID);
	
	public static final MultiblockDeferredHolder<Pumpjack, MultiblockBlock, PumpjackMB> PUMPJACK = MULTIBLOCKS.register("pumpjack",
		PumpjackMB::new, Pumpjack::new, BlockPatternBuilder.start()
			.aisle("A#A", "A#A", "A#A", "SPS")
			.aisle("AAA", "A#A", "AAA", "SSS")
			.aisle("AAA", "F#F", "FAF", "FSF")
			.aisle("AAA", "A#A", "AAA", "SSS")
			.aisle("AAA", "A#A", "AAA", "SRS")
			.where('#', BlockInWorld.hasState(st -> st.is(ModTags.Blocks.STORAGE_BLOCKS_STEEL)))
			.where('F', BlockInWorld.hasState(st -> st.is(Tags.Blocks.FENCES_NETHER_BRICK)))
			.where('S', BlockInWorld.hasState(st -> st.is(Blocks.POLISHED_DEEPSLATE_SLAB)))
			.where('P', BlockInWorld.hasState(st -> st.is(ModBlocks.STEEL_FLUID_PIPE)))
			.where('R', BlockInWorld.hasState(st -> st.is(Blocks.REDSTONE_BLOCK)))
			.where('A', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
			.build(), 1, 3, 2,
		new MultiblockCapabilityManager()
			.addCapabilityAllDirections(1, 3, 4, MultiblockCapabilityManager.CapabilityType.ENERGY)
			.addCapabilityAllDirections(1, 3, 0, MultiblockCapabilityManager.CapabilityType.FLUID)
	);
	
	//Mirrored MB's asembled at east and it rotated around it. EAST = NORTH
	public static final MultiblockDeferredHolder<Generator, MultiblockBlock, GeneratorMB> GENERATOR = MULTIBLOCKS.register("generator",
		GeneratorMB::new, Generator::new, BlockPatternBuilder.start()
			.aisle("AAA", "A#A", "P#P")
			.aisle("A$A", "###", "#P#")
			.aisle("AAA", "A#A", "P#P")
			.where('#', BlockInWorld.hasState(st -> st.is(ModTags.Blocks.STORAGE_BLOCKS_STEEL)))
			.where('$', BlockInWorld.hasState(st -> st.is(Tags.Blocks.FENCES_NETHER_BRICK)))
			.where('P', BlockInWorld.hasState(st -> st.is(ModBlocks.CITRUS_LOG)))
			.where('A', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
			.build(), 1, 1, 1,
		new MultiblockCapabilityManager()
			.addCapability(1, 2, 1, MultiblockCapabilityManager.CapabilityType.ENERGY, Direction.DOWN)
			.addCapability(1, 1, 0, MultiblockCapabilityManager.CapabilityType.FLUID, Direction.SOUTH)
			.addCapability(1, 0, 1, MultiblockCapabilityManager.CapabilityType.ITEM, Direction.UP)
	);
	
}
