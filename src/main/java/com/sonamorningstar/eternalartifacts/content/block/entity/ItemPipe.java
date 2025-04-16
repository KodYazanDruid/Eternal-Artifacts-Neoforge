package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.container.BasicAttachmentMenu;
import com.sonamorningstar.eternalartifacts.content.block.FluidPipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.ItemPipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.AttachmentablePipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ItemPipe extends AbstractPipeBlockEntity<IItemHandler> implements MenuProvider {
	private final ItemPipeBlock.PipeTier tier;
	public ItemPipe(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ITEM_PIPE.get(), IItemHandler.class, pos, state);
		this.tier = ((ItemPipeBlock) state.getBlock()).getTier();
	}
	
	@Override
	public void openMenu(ServerPlayer player, Direction dir) {
		player.openMenu(this, wr -> {
			wr.writeBlockPos(getBlockPos());
			wr.writeEnum(dir);
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
		return new BasicAttachmentMenu(pContainerId, pPlayerInventory, getBlockPos(),
			((AttachmentablePipeBlock<?>) getBlockState().getBlock()).getClickedRelativePos(hit.getDirection(), getBlockPos(), hit.getLocation(), 8)
		);
	}
	
	@Override
	protected boolean shouldPipesConnect(BlockState neighborState) {
		return neighborState.getBlock() instanceof ItemPipeBlock cable && tier == cable.getTier();
	}
	
	@Override
	protected void updatePipeConnections(Level lvl, BlockState state, Direction dir, boolean canConnect) {
		if (lvl.isAreaLoaded(getBlockPos(), 1) && state.getBlock() instanceof ItemPipeBlock) {
			PipeConnectionProperty.PipeConnection current = state.getValue(ItemPipeBlock.CONNECTION_BY_DIRECTION.get(dir));
			if (current == PipeConnectionProperty.PipeConnection.NONE || current == PipeConnectionProperty.PipeConnection.FREE) {
				lvl.setBlockAndUpdate(getBlockPos(), state.setValue(ItemPipeBlock.CONNECTION_BY_DIRECTION.get(dir),
					canConnect ? PipeConnectionProperty.PipeConnection.FREE : PipeConnectionProperty.PipeConnection.NONE));
			}
		}
	}
	
	@Override
	protected BlockCapabilityCache<IItemHandler, Direction> createCache(BlockPos pos, Direction dir) {
		return BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) level, pos, dir.getOpposite(),
			() -> !this.isRemoved(),
			() -> this.isDirty = true
		);
	}
	
	@Override
	protected boolean fillSourcesAndTargets(Map<BlockPos, BlockCapabilityCache<IItemHandler, Direction>> sources, Map<BlockPos, BlockCapabilityCache<IItemHandler, Direction>> targets, BlockCapabilityCache<IItemHandler, Direction> cache, BlockPos pos, Direction dir) {
		IItemHandler cap = cache.getCapability();
		PipeConnectionProperty.PipeConnection connection = getBlockState().getValue(FluidPipeBlock.CONNECTION_BY_DIRECTION.get(dir));
		boolean ret = false;
		if (cap != null) {
			if (connection == PipeConnectionProperty.PipeConnection.EXTRACT) {
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
	protected void doTransfer(Map<BlockPos, BlockCapabilityCache<IItemHandler, Direction>> sources,
							  Map<BlockPos, BlockCapabilityCache<IItemHandler, Direction>> targets) {
		int maxTransferRate = tier.getTransferRate();
		int itemsTransferredThisTick = 0;
		
		for (var sourceEntry : sources.entrySet()) {
			BlockPos sourcePos = sourceEntry.getKey();
			IItemHandler source = sourceEntry.getValue().getCapability();
			if (source == null) continue;
			
			var sortedTargets = targets.entrySet().stream()
				.sorted((a, b) -> {
					double distA = sourcePos.distSqr(a.getKey());
					double distB = sourcePos.distSqr(b.getKey());
					return Double.compare(distA, distB);
				}).toList();
			
			for (int sourceSlot = 0; sourceSlot < source.getSlots(); sourceSlot++) {
				ItemStack stack = source.extractItem(sourceSlot, maxTransferRate - itemsTransferredThisTick, true);
				if (stack.isEmpty()) continue;
				
				int remainingAmount = stack.getCount();
				
				for (var targetEntry : sortedTargets) {
					if (remainingAmount <= 0) break;
					
					IItemHandler target = targetEntry.getValue().getCapability();
					if (target == null) continue;
					
					for (int targetSlot = 0; targetSlot < target.getSlots() && remainingAmount > 0; targetSlot++) {
						ItemStack copyStack = stack.copy();
						copyStack.setCount(remainingAmount);
						
						ItemStack simulated = target.insertItem(targetSlot, copyStack, true);
						int insertable = copyStack.getCount() - simulated.getCount();
						
						if (insertable > 0) {
							ItemStack extracted = source.extractItem(sourceSlot, insertable, false);
							if (!extracted.isEmpty()) {
								int actuallyInserted = extracted.getCount() -
									target.insertItem(targetSlot, extracted, false).getCount();
								
								remainingAmount -= actuallyInserted;
								itemsTransferredThisTick += actuallyInserted;
								
								if (itemsTransferredThisTick >= maxTransferRate) {
									return;
								}
							}
						}
					}
				}
			}
		}
	}
}
