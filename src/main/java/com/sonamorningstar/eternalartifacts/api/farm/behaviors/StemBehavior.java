package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StemBehavior implements FarmBehavior {
	private final Item seed;
	private final Block stemBlock;
	private final Block fruitBlock;
	private final Block attachedStemBlock;
	
	public StemBehavior(Item seed, Block stemBlock, Block fruitBlock, Block attachedStemBlock) {
		this.seed = seed;
		this.stemBlock = stemBlock;
		this.fruitBlock = fruitBlock;
		this.attachedStemBlock = attachedStemBlock;
	}
	
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.is(seed);
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(fruitBlock) || state.is(attachedStemBlock);
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(fruitBlock) || state.is(attachedStemBlock);
	}
	
	@Override
	public List<ItemStack> harvest(Level level, BlockPos pos, @Nullable ItemStack tool, @Nullable Entity harvester) {
		BlockState state = level.getBlockState(pos);
		if (state.is(fruitBlock)) {
			return FarmBehavior.super.harvest(level, pos, tool, harvester);
		} else if (state.is(attachedStemBlock)) {
			BlockPos fruitPos = pos.relative(state.getValue(AttachedStemBlock.FACING));
			BlockState fruitState = level.getBlockState(fruitPos);
			if (fruitState.is(fruitBlock)) {
				return FarmBehavior.super.harvest(level, fruitPos, tool, harvester);
			}
		}
		return List.of();
	}
	
	@Override
	public boolean supportsReplanting() {
		return false;
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 60;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		BlockState state = stemBlock.defaultBlockState();
		return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
	}
}
