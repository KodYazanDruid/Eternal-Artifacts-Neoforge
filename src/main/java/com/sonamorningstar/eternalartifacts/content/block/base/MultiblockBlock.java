package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.TickableClient;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class MultiblockBlock extends Block implements EntityBlock {
	private final BlockEntityType.BlockEntitySupplier<? extends AbstractMultiblockBlockEntity> fun;
	private final DeferredHolder<Multiblock,? extends Multiblock> multiblock;
	public MultiblockBlock(Properties props, BlockEntityType.BlockEntitySupplier<? extends AbstractMultiblockBlockEntity> fun, DeferredHolder<Multiblock,? extends Multiblock> multiblock) {
		super(props);
		this.fun = fun;
		this.multiblock = multiblock;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof AbstractMultiblockBlockEntity part) {
			var provider = multiblock.get().getShapeProvider();
			if (provider != null) {
				VoxelShape shape = provider.getShape(part, context);
				if (shape != null) return shape;
			}
		}
		return super.getShape(state, level, pos, context);
	}
	
	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof AbstractMultiblockBlockEntity part) {
			var provider = multiblock.get().getVisualShapeProvider();
			if (provider != null) {
				VoxelShape shape = provider.getShape(part, context);
				if (shape != null) return shape;
			}
		}
		return super.getVisualShape(state, level, pos, context);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof AbstractMultiblockBlockEntity part) {
			var provider = multiblock.get().getCollisionShapeProvider();
			if (provider != null) {
				VoxelShape shape = provider.getShape(part, context);
				if (shape != null) return shape;
			}
		}
		return super.getCollisionShape(state, level, pos, context);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof Machine<?> machine) return machine.use(state, level, pos, player, hand, hit);
		return super.use(state, level, pos, player, hand, hit);
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState pNewState, boolean pMovedByPiston) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof AbstractMultiblockBlockEntity ambe) ambe.deformMultiblock();
		super.onRemove(state, level, pos, pNewState, pMovedByPiston);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return fun.create(pPos, pState);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (lvl, pos, st, be) -> {
			if (be instanceof AbstractMultiblockBlockEntity mbBe) {
				if (lvl instanceof ClientLevel clientLevel && mbBe instanceof TickableClient tickableClient) tickableClient.tickClient(clientLevel, pos, st);
				else if (lvl instanceof ServerLevel serverLevel) mbBe.tickServer(serverLevel, pos, st);
			}
		};
	}
}
