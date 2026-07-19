package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.PlantType;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class FertilizedSoilFarmland extends FarmBlock {
	public FertilizedSoilFarmland(Properties properties) {
		super(properties);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
			? ModBlocks.FERTILIZED_SOIL.get().defaultBlockState()
			: super.getStateForPlacement(context);
	}
	
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction direction, IPlantable plantable) {
		PlantType plantType = plantable.getPlantType(level, pos);
		return plantType == PlantType.CROP || plantType == PlantType.PLAINS;
	}
	
	@Override
	public boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		return true;
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!state.canSurvive(level, pos)) {
			turnToSoil(null, state, level, pos);
		}
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int moisture = state.getValue(MOISTURE);
		BlockPos above = pos.above();
		if (!isNearWater(level, pos) && !level.isRainingAt(above)) {
			if (moisture > 0) {
				level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
			} else if (!shouldMaintainFarmland(level, pos)) {
				turnToSoil(null, state, level, pos);
			}
		} else if (moisture < 7) {
			level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
		}
		var bonemealed = BoneMealItem.applyBonemeal(Items.BONE_MEAL.getDefaultInstance(), level, above, FakePlayerHelper.getFakePlayer(level));
		if (bonemealed) {
			level.levelEvent(1505, above, 0);
		}
	}
	
	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (!level.isClientSide && CommonHooks.onFarmlandTrample(level, pos, ModBlocks.FERTILIZED_SOIL.get().defaultBlockState(), fallDistance, entity)) {
			turnToSoil(entity, state, level, pos);
		}
		
		entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
	}
	
	public void turnToSoil(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
		BlockState blockstate = pushEntitiesUp(state, ModBlocks.FERTILIZED_SOIL.get().defaultBlockState(), level, pos);
		level.setBlockAndUpdate(pos, blockstate);
		level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
	}
	
	private boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
		BlockState plant = level.getBlockState(pos.above());
		BlockState state = level.getBlockState(pos);
		return plant.getBlock() instanceof IPlantable plantable && state.canSustainPlant(level, pos, Direction.UP, plantable);
	}
	
	private boolean isNearWater(LevelReader level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		for(BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
			if (state.canBeHydrated(level, pos, level.getFluidState(blockpos), blockpos)) {
				return true;
			}
		}
		
		return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
	}
}
