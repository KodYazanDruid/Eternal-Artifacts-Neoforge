package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.content.block.entity.TesseractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class TesseractBlock extends BaseEntityBlock {
	public TesseractBlock(Properties props) {
		super(props);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(TesseractBlock::new);
	}
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TesseractBlockEntity(pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof TesseractBlockEntity tesseract) {
			player.openMenu(tesseract, wr -> {
				wr.writeBlockPos(pos);
				wr.writeCollection(TesseractNetworks.get(player.level()).getNetworksForPlayer(player), (wrt, network) -> {
					CompoundTag tag = new CompoundTag();
					network.writeToNBT(tag);
					wr.writeNbt(tag);
				});
			});
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return super.use(state, level, pos, player, hand, hit);
	}
}
