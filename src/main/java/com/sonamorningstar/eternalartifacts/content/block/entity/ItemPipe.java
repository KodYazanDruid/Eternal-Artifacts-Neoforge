package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.FilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemFilterEntry;
import com.sonamorningstar.eternalartifacts.container.PipeFilterMenu;
import com.sonamorningstar.eternalartifacts.content.block.ItemPipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.AttachmentablePipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.FilterablePipeBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty.PipeConnection;

public class ItemPipe extends FilterablePipeBlockEntity<IItemHandler> {
	private final ItemPipeBlock.PipeTier tier;
	public ItemPipe(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ITEM_PIPE.get(), IItemHandler.class, pos, state);
		this.tier = ((ItemPipeBlock) state.getBlock()).getTier();
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		BlockHitResult hit = RayTraceHelper.retrace(pPlayer);
		Direction dir = ((AttachmentablePipeBlock<?>) getBlockState().getBlock()).getClickedRelativePos(hit.getDirection(), getBlockPos(), hit.getLocation(), 8);
		PipeConnectionProperty.PipeConnection conn = getBlockState().getValue(AttachmentablePipeBlock.CONNECTION_BY_DIRECTION.get(dir));
		filterEntries.putIfAbsent(dir, NonNullList.withSize(9, ItemFilterEntry.Empty.create(true)));
		return new PipeFilterMenu(pContainerId, pPlayerInventory, getBlockPos(),
			dir, conn == PipeConnectionProperty.PipeConnection.EXTRACT ? 0 : 1,
			whitelists.get(dir) != null && whitelists.get(dir),
			nbtIgnores.get(dir) != null && nbtIgnores.get(dir), filterEntries.get(dir)
		);
	}
	
	@Override
	public void loadFilterEntries(CompoundTag data) {
		CompoundTag filterData = data.getCompound("FilterData");
		ListTag filterList = filterData.getList("FilterEntries", 10);
		
		filterList.stream().map(t -> (CompoundTag) t).forEach(dirTag -> {
			for (Direction dir : Direction.values()) {
				NonNullList<FilterEntry> list = NonNullList.withSize(9, ItemFilterEntry.Empty.create(true));
				ListTag dirEntries = dirTag.getList(dir.getName(), 10);
				if (dirEntries.isEmpty()) continue;
				for (int i = 0; i < dirEntries.size(); i++) {
					list.set(i, ItemFilterEntry.fromNBT(dirEntries.getCompound(i)));
				}
				filterEntries.put(dir, list);
			}
		});
		for (Direction dir : Direction.values()) {
			whitelists.put(dir, filterData.getBoolean(dir.getName() + "_whitelist"));
			nbtIgnores.put(dir, filterData.getBoolean(dir.getName() + "_ignore_nbt"));
		}
	}
	
	@Override
	public void loadFromItemFilter(ItemStack stack, Direction direction) {
		if (stack.hasTag() && stack.getTag().contains("FilterData")) {
			CompoundTag filterData = stack.getTag().getCompound("FilterData");
			ListTag entryList = filterData.getList("ItemFilters", 10);
			NonNullList<FilterEntry> filters = NonNullList.withSize(9, ItemFilterEntry.Empty.create(true));
			for (int i = 0; i < entryList.size(); i++) {
				CompoundTag entryTag = entryList.getCompound(i);
				filters.set(i, ItemFilterEntry.fromNBT(entryTag));
			}
			filterEntries.put(direction, filters);
			whitelists.put(direction, filterData.getBoolean("whitelist"));
			nbtIgnores.put(direction, filterData.getBoolean("ignore_nbt"));
			sendUpdate();
		}
	}
	
	@Override
	protected boolean shouldPipesConnect(BlockState neighborState, Direction direction) {
		return super.shouldPipesConnect(neighborState, direction) && neighborState.getBlock() instanceof ItemPipeBlock cable &&
			tier == cable.getTier();
	}
	
	@Override
	protected void updatePipeConnections(Level lvl, BlockState state, Direction dir, boolean canConnect) {
		if (lvl.isAreaLoaded(getBlockPos(), 1) && state.getBlock() instanceof ItemPipeBlock) {
			PipeConnection current = state.getValue(ItemPipeBlock.CONNECTION_BY_DIRECTION.get(dir));
			if (current == PipeConnection.NONE || current == PipeConnection.FREE) {
				lvl.setBlockAndUpdate(getBlockPos(), state.setValue(ItemPipeBlock.CONNECTION_BY_DIRECTION.get(dir),
					canConnect ? PipeConnection.FREE : PipeConnection.NONE));
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
	protected boolean fillSourcesAndTargets(Map<BlockPos, BlockCapabilityCache<IItemHandler, Direction>> sources,
											Map<BlockPos, BlockCapabilityCache<IItemHandler, Direction>> targets,
											BlockCapabilityCache<IItemHandler, Direction> cache,
											BlockPos pos, Direction dir) {
		IItemHandler cap = cache.getCapability();
		PipeConnection connection = getBlockState().getValue(ItemPipeBlock.CONNECTION_BY_DIRECTION.get(dir));
		boolean isManuallyDisabled = manuallyDisabled.getOrDefault(dir, false);
		if (isManuallyDisabled && !(connection == PipeConnection.EXTRACT || connection == PipeConnection.FILTERED)) return false;
		boolean ret = false;
		if (cap != null) {
			if (connection == PipeConnection.EXTRACT) {
				sources.put(pos, cache);
			} else {
				targets.put(pos, cache);
			}
			ret = true;
		}
		return ret;
	}
	
	@Override
	protected int getMaxRange() {
		return tier.getMaxConnections();
	}
	
	@Override
	protected void doTransfer(Map<BlockPos, BlockCapabilityCache<IItemHandler, Direction>> sources,
							  Map<BlockPos, BlockCapabilityCache<IItemHandler, Direction>> targets) {
		int maxTransferRate = tier.getTransferRate();
		
		for (var sourceEntry : sources.entrySet()) {
			BlockPos sourcePos = sourceEntry.getKey();
			BlockCapabilityCache<IItemHandler, Direction> sourceCache = sourceEntry.getValue();
			IItemHandler source = sourceCache.getCapability();
			if (source == null) continue;
			
			int itemsTransferredFromThisSource = 0;
			
			Map<BlockPos, Integer> distances = sourceToTargetDistances.getOrDefault(sourcePos, Collections.emptyMap());
			if (distances.isEmpty()) continue;
			
			List<BlockPos> sortedTargets = distances.entrySet().stream()
				.filter(entry -> targets.containsKey(entry.getKey()))
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.toList();
			
			if (sortedTargets.isEmpty()) continue;
			
			Direction sourceDir = sourceCache.context();
			BlockEntity sourceBe = level.getBlockEntity(sourcePos.relative(sourceDir));
			NonNullList<FilterEntry> sourceFilters = null;
			
			if (sourceBe instanceof ItemPipe sourcePipe) {
				sourceFilters = sourcePipe.filterEntries.get(sourceDir.getOpposite());
			}
			
			sourceLoop: for (int sourceSlot = 0; sourceSlot < source.getSlots(); sourceSlot++) {
				if (itemsTransferredFromThisSource >= maxTransferRate) break;
				
				ItemStack stack = source.extractItem(sourceSlot, maxTransferRate - itemsTransferredFromThisSource, true);
				if (stack.isEmpty()) continue;
				
				if (sourceFilters != null && checkFilters((ItemPipe)sourceBe, sourceFilters, stack, sourceDir)) {
					continue;
				}
				
				int remainingAmount = stack.getCount();
				
				for (BlockPos targetPos : sortedTargets) {
					if (remainingAmount <= 0) break;
					
					BlockCapabilityCache<IItemHandler, Direction> targetCache = targets.get(targetPos);
					IItemHandler target = targetCache.getCapability();
					if (target == null) continue;
					
					Direction targetDir = targetCache.context();
					BlockEntity targetBe = level.getBlockEntity(targetPos.relative(targetDir));
					
					if (targetBe instanceof ItemPipe targetPipe) {
						var targetFilters = targetPipe.filterEntries.get(targetDir.getOpposite());
						if (targetFilters != null && checkFilters(targetPipe, targetFilters, stack, targetDir)) {
							continue;
						}
					}
					
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
								itemsTransferredFromThisSource += actuallyInserted;
								
								if (itemsTransferredFromThisSource >= maxTransferRate) {
									break sourceLoop;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private boolean checkFilters(ItemPipe sourcePipe, NonNullList<FilterEntry> sourceFilters,
								 ItemStack stack, Direction sourceDir) {
		boolean isWhitelist = sourcePipe.whitelists.getOrDefault(sourceDir.getOpposite(), false);
		boolean shouldSkip = false;
		
		for (FilterEntry entry : sourceFilters) {
			if (entry instanceof ItemFilterEntry itemFilter) {
				boolean matches = itemFilter.matches(stack);
				if (!isWhitelist && matches) {
					shouldSkip = true;
					break;
				}
				if (isWhitelist && matches) {
					shouldSkip = false;
					break;
				} else if (isWhitelist) {
					shouldSkip = true;
				}
			}
		}
		return shouldSkip;
	}
}
