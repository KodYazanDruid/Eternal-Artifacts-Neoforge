package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import java.util.function.BiConsumer;

public class FertilizedSoilBlock extends Block {
	public FertilizedSoilBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BlockPos above = pos.above();
		var bonemealed = BoneMealItem.applyBonemeal(Items.BONE_MEAL.getDefaultInstance(), level, above, FakePlayerHelper.getFakePlayer(level));
		if (bonemealed) {
			level.levelEvent(1505, above, 0);
		}
	}
}
