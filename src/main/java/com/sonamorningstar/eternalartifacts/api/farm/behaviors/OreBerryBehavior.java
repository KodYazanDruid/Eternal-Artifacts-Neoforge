package com.sonamorningstar.eternalartifacts.api.farm.behaviors;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import com.sonamorningstar.eternalartifacts.content.block.OreBerryBlock;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OreBerryBehavior implements FarmBehavior {
	@Override
	public boolean isCorrectSeed(ItemStack seedStack) {
		return seedStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof OreBerryBlock;
	}
	
	@Override
	public boolean matches(Level level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof OreBerryBlock;
	}
	
	@Override
	public boolean canHarvest(Level level, BlockPos pos) {
		int attempts = 3;
		while (attempts-- > 0) {
			BlockState state = level.getBlockState(pos.offset(0, attempts, 0));
			if (!(state.getBlock() instanceof OreBerryBlock)) continue;
			if (state.getValue(OreBerryBlock.AGE) >= OreBerryBlock.MAX_AGE) return true;
		}
		return false;
	}
	
	@Override
	public List<ItemStack> harvest(Level level, BlockPos pos, @Nullable ItemStack tool, @Nullable Entity harvester) {
		List<ItemStack> drops = new ObjectArrayList<>();
		int attempts = 3;
		while (attempts-- > 0) {
			BlockPos targetPos = pos.offset(0, attempts, 0);
			BlockState state = level.getBlockState(targetPos);
			if (state.getBlock() instanceof OreBerryBlock && state.getValue(OreBerryBlock.AGE) >= OreBerryBlock.MAX_AGE) {
				LootTable lootTable = level.getServer().getLootData().getLootTable(((OreBerryBlock) state.getBlock()).getTable());
				LootParams ctx = new LootParams.Builder((ServerLevel) level).create(LootContextParamSets.EMPTY);
				drops.addAll(lootTable.getRandomItems(ctx));
				level.playSound(null, targetPos, SoundEvents.CHAIN_STEP, SoundSource.BLOCKS, 1, 0.8F + level.random.nextFloat() * 0.4F);
				BlockState newState = state.setValue(OreBerryBlock.AGE, 2);
				level.setBlock(targetPos, newState, 2);
				level.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(harvester, newState));
			}
		}
		return drops;
	}
	
	@Override
	public boolean supportsReplanting() {
		return false;
	}
	
	@Override
	public BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		Block block = Block.byItem(seed.getItem());
		if (block == Blocks.AIR) return Blocks.AIR.defaultBlockState();
		BlockState state = block.defaultBlockState();
		return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
	}
	
	@Override
	public int getSludgeAmount(Level level, BlockPos pos, @Nullable ItemStack tool) {
		return 15;
	}
}
