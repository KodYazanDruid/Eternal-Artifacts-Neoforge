package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.FilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.FluidFilterEntry;
import com.sonamorningstar.eternalartifacts.container.PipeFilterMenu;
import com.sonamorningstar.eternalartifacts.content.block.FluidPipeBlock;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty.PipeConnection;

public class FluidPipe extends FilterablePipeBlockEntity<IFluidHandler> {
	private final FluidPipeBlock.PipeTier tier;
	public FluidPipe(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FLUID_PIPE.get(), IFluidHandler.class, pos, state);
		this.tier = ((FluidPipeBlock) state.getBlock()).getTier();
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		BlockHitResult hit = RayTraceHelper.retrace(pPlayer);
		Direction dir = ((AttachmentablePipeBlock<?>) getBlockState().getBlock()).getClickedRelativePos(hit.getDirection(), getBlockPos(), hit.getLocation(), 8);
		PipeConnectionProperty.PipeConnection conn = getBlockState().getValue(AttachmentablePipeBlock.CONNECTION_BY_DIRECTION.get(dir));
		filterEntries.putIfAbsent(dir, NonNullList.withSize(9, FluidFilterEntry.Empty.create(true)));
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
				NonNullList<FilterEntry> list = NonNullList.withSize(9, FluidFilterEntry.Empty.create(true));
				ListTag dirEntries = dirTag.getList(dir.getName(), 10);
				if (dirEntries.isEmpty()) continue;
				for (int i = 0; i < dirEntries.size(); i++) {
					list.set(i, FluidFilterEntry.fromNBT(dirEntries.getCompound(i)));
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
			ListTag entryList = filterData.getList("FluidFilters", 10);
			NonNullList<FilterEntry> filters = NonNullList.withSize(9, FluidFilterEntry.Empty.create(true));
			for (int i = 0; i < entryList.size(); i++) {
				CompoundTag entryTag = entryList.getCompound(i);
				filters.set(i, FluidFilterEntry.fromNBT(entryTag));
			}
			filterEntries.put(direction, filters);
			whitelists.put(direction, filterData.getBoolean("whitelist"));
			nbtIgnores.put(direction, filterData.getBoolean("ignore_nbt"));
			sendUpdate();
		}
	}
	
	@Override
	protected boolean shouldPipesConnect(BlockState neighborState, Direction direction) {
		return super.shouldPipesConnect(neighborState, direction) && neighborState.getBlock() instanceof FluidPipeBlock cable &&
			tier == cable.getTier();
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
	protected int getMaxConnections() {
		return tier.getMaxConnections();
	}
	
	@Override
	protected void doTransfer(Map<BlockPos, BlockCapabilityCache<IFluidHandler, Direction>> sources,
							  Map<BlockPos, BlockCapabilityCache<IFluidHandler, Direction>> targets) {
		
		int maxTransferRate = tier.getTransferRate();
		
		for (var sourceEntry : sources.entrySet()) {
			BlockPos sourcePos = sourceEntry.getKey();
			BlockCapabilityCache<IFluidHandler, Direction> sourceCache = sourceEntry.getValue();
			IFluidHandler source = sourceCache.getCapability();
			if (source == null) continue;
			
			var sortedTargets = targets.entrySet().stream()
				.sorted((a, b) -> {
					double distA = sourcePos.distSqr(a.getKey());
					double distB = sourcePos.distSqr(b.getKey());
					return Double.compare(distA, distB);
				}).toList();
			
			FluidStack extracted = source.drain(maxTransferRate, IFluidHandler.FluidAction.SIMULATE);
			if (extracted.isEmpty()) continue;
			
			Direction sourceDir = sourceCache.context();
			BlockEntity sourceBe = level.getBlockEntity(sourcePos.relative(sourceDir));
			if (sourceBe instanceof FluidPipe sourcePipe) {
				var sourceFilters = sourcePipe.filterEntries.get(sourceDir.getOpposite());
				if (sourceFilters != null) {
					if (checkFilters(sourcePipe, sourceFilters, extracted, sourceDir)) {
						continue;
					}
				}
			}
			
			int remainingAmount = Math.min(extracted.getAmount(), maxTransferRate);
			
			for (var targetEntry : sortedTargets) {
				if (remainingAmount <= 0) break;
				
				BlockPos targetPos = targetEntry.getKey();
				BlockCapabilityCache<IFluidHandler, Direction> targetCache = targetEntry.getValue();
				IFluidHandler target = targetCache.getCapability();
				if (target == null) continue;
				
				FluidStack transferStack = new FluidStack(extracted.getFluid(), remainingAmount);
				int fillable = target.fill(transferStack, IFluidHandler.FluidAction.SIMULATE);
				if (fillable <= 0) continue;
				
				Direction targetDir = targetCache.context();
				BlockEntity targetBe = level.getBlockEntity(targetPos.relative(targetDir));
				if (targetBe instanceof FluidPipe targetPipe) {
					var targetFilters = targetPipe.filterEntries.get(targetDir.getOpposite());
					if (targetFilters != null) {
						if (checkFilters(targetPipe, targetFilters, extracted, targetDir)) {
							continue;
						}
					}
				}
				
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
	
	private boolean checkFilters(FluidPipe sourcePipe, NonNullList<FilterEntry> sourceFilters,
								 FluidStack stack, Direction sourceDir) {
		boolean isWhitelist = sourcePipe.whitelists.getOrDefault(sourceDir.getOpposite(), false);
		boolean shouldSkip = false;
		
		for (FilterEntry entry : sourceFilters) {
			if (entry instanceof FluidFilterEntry itemFilter) {
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
