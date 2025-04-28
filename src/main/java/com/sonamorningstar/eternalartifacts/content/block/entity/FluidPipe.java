package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.container.BasicAttachmentMenu;
import com.sonamorningstar.eternalartifacts.content.block.FluidPipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.AttachmentablePipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty.PipeConnection;

public class FluidPipe extends AbstractPipeBlockEntity<IFluidHandler> implements MenuProvider {
	private final FluidPipeBlock.PipeTier tier;
	public FluidPipe(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FLUID_PIPE.get(), IFluidHandler.class, pos, state);
		this.tier = ((FluidPipeBlock) state.getBlock()).getTier();
	}
	
	@Override
	public void openMenu(ServerPlayer player, Direction dir) {
		PipeConnection conn = getBlockState().getValue(AttachmentablePipeBlock.CONNECTION_BY_DIRECTION.get(dir));
		player.openMenu(this, wr -> {
			wr.writeBlockPos(getBlockPos());
			wr.writeEnum(dir);
			wr.writeVarInt(conn == PipeConnection.EXTRACT ? 0 : 1);
		});
	}
	
	@Override
	public Component getDisplayName() {
		return Component.translatable("filter");
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		BlockHitResult hit = RayTraceHelper.retrace(pPlayer);
		Direction dir = ((AttachmentablePipeBlock<?>) getBlockState().getBlock()).getClickedRelativePos(hit.getDirection(), getBlockPos(), hit.getLocation(), 8);
		PipeConnection conn = getBlockState().getValue(AttachmentablePipeBlock.CONNECTION_BY_DIRECTION.get(dir));
		return new BasicAttachmentMenu(pContainerId, pPlayerInventory, getBlockPos(),
			dir, conn == PipeConnection.EXTRACT ? 0 : 1
		);
	}
	
	@Override
	protected boolean shouldPipesConnect(BlockState neighborState) {
		return neighborState.getBlock() instanceof FluidPipeBlock cable && tier == cable.getTier();
	}
	
	@Override
	protected BlockCapabilityCache<IFluidHandler, Direction> createCache(BlockPos pos, Direction dir) {
		return BlockCapabilityCache.create(Capabilities.FluidHandler.BLOCK, (ServerLevel) level, pos, dir.getOpposite(),
			() -> !this.isRemoved(),
			() -> this.isDirty = true
		);
	}
	
	@Override
	protected void updatePipeConnections(Level lvl, BlockState state, Direction dir, boolean canConnect) {
		if (lvl.isAreaLoaded(getBlockPos(), 1) && state.getBlock() instanceof FluidPipeBlock) {
			PipeConnection current = state.getValue(FluidPipeBlock.CONNECTION_BY_DIRECTION.get(dir));
			if (current == PipeConnection.NONE || current == PipeConnection.FREE) {
				lvl.setBlockAndUpdate(getBlockPos(), state.setValue(FluidPipeBlock.CONNECTION_BY_DIRECTION.get(dir),
					canConnect ? PipeConnection.FREE : PipeConnection.NONE));
			}
		}
	}
	
	@Override
	protected boolean fillSourcesAndTargets(Map<BlockPos, BlockCapabilityCache<IFluidHandler, Direction>> sources,
											Map<BlockPos, BlockCapabilityCache<IFluidHandler, Direction>> targets,
											BlockCapabilityCache<IFluidHandler, Direction> cache,
											BlockPos pos, Direction dir) {
		IFluidHandler cap = cache.getCapability();
		PipeConnection connection = getBlockState().getValue(FluidPipeBlock.CONNECTION_BY_DIRECTION.get(dir));
		boolean ret = false;
		if (cap != null) {
			if (connection == PipeConnection.EXTRACT) {
				sources.put(pos, cache);
				ret = true;
			} else /*if (connection != PipeConnection.NONE) */{
				targets.put(pos, cache);
				ret = true;
			}
		}
		return ret;
	}
	
	@Override
	protected int getMaxConnections() {
		return tier.getMaxConnections();
	}
	
	@Override
	protected void doTransfer(Map<BlockPos, BlockCapabilityCache<IFluidHandler, Direction>> sources,
							  Map<BlockPos, BlockCapabilityCache<IFluidHandler, Direction>> targets) {
		
		int maxTransferRate = tier.getTransferRate();
		
		for (var sourceEntry : sources.entrySet()) {
			BlockPos sourcePos = sourceEntry.getKey();
			IFluidHandler source = sourceEntry.getValue().getCapability();
			if (source == null) continue;
			
			var sortedTargets = targets.entrySet().stream()
				.sorted((a, b) -> {
					double distA = sourcePos.distSqr(a.getKey());
					double distB = sourcePos.distSqr(b.getKey());
					return Double.compare(distA, distB);
				}).toList();
			
			FluidStack extracted = source.drain(maxTransferRate, IFluidHandler.FluidAction.SIMULATE);
			if (extracted.isEmpty()) continue;
			
			int remainingAmount = Math.min(extracted.getAmount(), maxTransferRate);
			
			for (var targetEntry : sortedTargets) {
				if (remainingAmount <= 0) break;
				
				IFluidHandler target = targetEntry.getValue().getCapability();
				if (target == null) continue;
				
				FluidStack transferStack = new FluidStack(extracted.getFluid(), remainingAmount);
				
				int fillable = target.fill(transferStack, IFluidHandler.FluidAction.SIMULATE);
				if (fillable <= 0) continue;
				
				FluidStack actualExtract = source.drain(
					new FluidStack(extracted.getFluid(), fillable),
					IFluidHandler.FluidAction.EXECUTE
				);
				
				if (!actualExtract.isEmpty()) {
					int actuallyFilled = target.fill(actualExtract, IFluidHandler.FluidAction.EXECUTE);
					remainingAmount -= actuallyFilled;
				}
			}
		}
	}
}
