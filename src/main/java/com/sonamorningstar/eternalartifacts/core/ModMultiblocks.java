package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.multiblock.Pumpjack;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMultiblocks {
	public static final DeferredRegister<Multiblock> MULTIBLOCKS = DeferredRegister.create(ModRegistries.Keys.MULTIBLOCK, MODID);
	
	public static final DeferredHolder<Multiblock, Pumpjack> PUMPJACK = MULTIBLOCKS.register("pumpjack", () ->
		// Width = 3, Height = 4, Depth = 5
		new Pumpjack(BlockPatternBuilder.start()
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
		.build()));
	
}
