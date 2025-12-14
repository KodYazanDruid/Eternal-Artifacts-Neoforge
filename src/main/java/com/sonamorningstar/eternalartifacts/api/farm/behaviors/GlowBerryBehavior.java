package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.CaveVines.BERRIES;

public class GlowBerryBehavior implements FarmBehavior {
	
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.is(Items.GLOW_BERRIES);
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(Blocks.CAVE_VINES) || state.is(Blocks.CAVE_VINES_PLANT);
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		return level.getBlockState(pos).getValue(BERRIES);
	}
	
	@Override
	public List<ItemStack> harvest(Level level, BlockPos pos, @Nullable ItemStack tool) {
		List<ItemStack> drops = new ArrayList<>();
		drops.add(new ItemStack(Items.GLOW_BERRIES, 1));
		float f = Mth.randomBetween(level.random, 0.8F, 1.2F);
		level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
		BlockState blockstate = level.getBlockState(pos).setValue(BERRIES, false);
		level.setBlock(pos, blockstate, 2);
		level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
		return drops;
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 10;
	}
	
	@Override
	public boolean supportsReplanting() {
		return false;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block instanceof GrowingPlantBlock growing) {
			var state = growing.getStateForPlacement(level);
			if (state.canSurvive(level, pos)) return state;
		}
		return Blocks.AIR.defaultBlockState();
	}
}
