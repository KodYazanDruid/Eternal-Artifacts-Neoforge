package com.sonamorningstar.eternalartifacts.content.block;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.content.block.entity.ModSkullBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModSkullType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;

public class ModSkullBlock extends SkullBlock {
	public static final Map<Type, Pair<ResourceLocation, ResourceLocation>> LAYERED_SKIN_BY_TYPE = Util.make(Maps.newHashMap(), map -> {
		map.put(ModSkullType.DROWNED,
			new Pair<>(
				new ResourceLocation("textures/entity/zombie/drowned.png"),
				new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png")
			));
		map.put(ModSkullType.STRAY,
			new Pair<>(
				new ResourceLocation("textures/entity/skeleton/stray.png"),
				new ResourceLocation("textures/entity/skeleton/stray_overlay.png")
			));
	});
	public ModSkullBlock(Type p_56318_, Properties p_56319_) {
		super(p_56318_, p_56319_);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ModSkullBlockEntity(pPos, pState);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pLevel.isClientSide) {
			boolean flag = pState.is(Blocks.DRAGON_HEAD)
				|| pState.is(Blocks.DRAGON_WALL_HEAD)
				|| pState.is(Blocks.PIGLIN_HEAD)
				|| pState.is(Blocks.PIGLIN_WALL_HEAD);
			if (flag) {
				return createTickerHelper(pBlockEntityType, ModBlockEntities.SKULL.get(), SkullBlockEntity::animation);
			}
		}
		
		return null;
	}
}
