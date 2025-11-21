package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.base.ItemStorageEntityBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.DeepItemStorageUnit;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.IDeepStorage;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@Getter
public class DeepItemStorageUnitBlock extends ItemStorageEntityBlock {
	private final boolean isInfinite;
	public DeepItemStorageUnitBlock(Properties pProperties, boolean isInfinite) {
		super(pProperties);
		this.isInfinite = isInfinite;
	}
	
	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(p -> new DeepItemStorageUnitBlock(p, isInfinite));
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new DeepItemStorageUnit(pPos, pState);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof IDeepStorage deepStorage) {
			return deepStorage.useStorageBlock(state, level, pos, player, hand, hit);
		}
		return InteractionResult.PASS;
	}
}
