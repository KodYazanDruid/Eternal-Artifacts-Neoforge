package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.ReverseToggleConfig;
import com.sonamorningstar.eternalartifacts.api.machine.config.ToggleConfig;
import com.sonamorningstar.eternalartifacts.container.BlockInteractorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class BlockBreaker extends SidedTransferMachine<BlockInteractorMenu> implements Filterable {
	private final NonNullList<FluidFilterEntry> fluidFilters = NonNullList.withSize(9, FluidStackEntry.EMPTY);
	private final NonNullList<BlockFilterEntry> blockFilters = NonNullList.withSize(9, BlockStateEntry.EMPTY);
	
	private boolean fluidFilterWhitelist = false;
	private boolean blockFilterWhitelist = false;
	private boolean fluidFilterIgnoreNBT = true;
	private boolean blockFilterIgnoreProperties = true;
	
	public BlockBreaker(BlockPos pos, BlockState blockState) {
		super(ModMachines.BLOCK_BREAKER.getBlockEntity(), pos, blockState, (a, b, c, d) -> new BlockInteractorMenu(ModMachines.BLOCK_BREAKER.getMenu(), a, b, c, d));
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, fs -> !fs.getFluid().defaultFluidState().createLegacyBlock().isEmpty(), true, true));
		outputSlots.addAll(List.of(1, 2, 3, 4, 5, 6, 7, 8));
		setInventory(() -> createBasicInventory(9, outputSlots, (slot, stack) -> slot == 0 && canStackDig(stack)));
		setEnergyPerTick(250);
	}
	
	private boolean canStackDig(ItemStack stack) {
		Item item = stack.getItem();
		return item.canPerformAction(stack, ToolActions.SHOVEL_DIG) ||
			item.canPerformAction(stack, ToolActions.AXE_DIG) ||
			item.canPerformAction(stack, ToolActions.PICKAXE_DIG) ||
			item.canPerformAction(stack, ToolActions.HOE_DIG) ||
			item.canPerformAction(stack, ToolActions.SHEARS_DIG) ||
			item.canPerformAction(stack, ToolActions.SWORD_DIG);
	}
	
	@Override
	public void registerConfigs() {
		super.registerConfigs();
		getConfiguration().add(new ReverseToggleConfig("block_mode"));
		getConfiguration().add(new ReverseToggleConfig("fluid_mode"));
		getConfiguration().add(new ToggleConfig("always_mine"));
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		destroyTickStart = -1;
		if (level != null && !level.isClientSide()) {
			level.destroyBlockProgress(FakePlayerHelper.getFakePlayer(this, level).getId(), getBlockPos().relative(getBlockState().getValue(BlockStateProperties.FACING)), -1);
		}
	}
	
	@Override
	public void setFluidFilterWhitelistSilent(boolean whitelist) {
		this.fluidFilterWhitelist = whitelist;
	}
	@Override
	public void setBlockFilterWhitelistSilent(boolean whitelist) {this.blockFilterWhitelist = whitelist;}
	@Override
	public void setFluidFilterIgnoreNBTSilent(boolean ignoreNBT) {
		this.fluidFilterIgnoreNBT = ignoreNBT;
	}
	@Override
	public void setBlockFilterIgnorePropertiesSilent(boolean ignoreProperties) {this.blockFilterIgnoreProperties = ignoreProperties;}
	
	public int destroyTickStart = -1;
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputItems(lvl, pos);
		performAutoOutputItems(lvl, pos);
		performAutoOutputFluids(lvl, pos);
		if (!redstoneChecks(lvl)) return;
		
		getFakePlayer();
		setupFakePlayer(st);
		setupFakePlayerInventory(inventory.getStackInSlot(0));
		BlockPos targetPos = getBlockPos().relative(st.getValue(BlockStateProperties.FACING));
		BlockState minedState = lvl.getBlockState(targetPos);
		ServerPlayerGameMode gameMode = fakePlayer.gameMode;
		MachineConfiguration configs = getConfiguration();
		
		if (!configs.get(ReverseToggleConfig.class, "block_mode").isDisabled() && !lvl.getBlockState(targetPos).isAir() && canWork(energy)
			&& minedState.getBlock().canHarvestBlock(minedState, lvl, targetPos, fakePlayer) && matchesBlockFilter(minedState)
			&& shouldMineBlock((ServerLevel) lvl, targetPos)) {
			
			spendEnergy(energy);
			if (destroyTickStart == -1) {
				gameMode.delayedTickStart = gameMode.gameTicks;
				destroyTickStart = gameMode.gameTicks;
			}
			gameMode.delayedDestroyPos = targetPos;
			gameMode.hasDelayedDestroy = true;
			gameMode.tick();
		}
		FluidState fluidState = lvl.getFluidState(targetPos);
		FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
		
		if (!configs.get(ReverseToggleConfig.class, "fluid_mode").isDisabled() && !fluidState.isEmpty() && canWork(energy)
			&& minedState.getBlock() instanceof BucketPickup bp && fluidState.isSource() &&
				(tank.getFluid(0).isEmpty() || tank.getFluid(0).is(fluidState.getType())) && matchesFluidFilter(fluidStack)) {
			
			ItemStack bucketStack = bp.pickupBlock(fakePlayer, lvl, targetPos, minedState);
			if (!bucketStack.isEmpty()) {
				IFluidHandlerItem itemH = bucketStack.getCapability(Capabilities.FluidHandler.ITEM);
				if (itemH != null) {
					int filled = tank.fillForced(itemH.getFluidInTank(0).copyWithAmount(1000), IFluidHandler.FluidAction.SIMULATE);
					if (filled == 1000) {
						tank.fillForced(itemH.getFluidInTank(0).copyWithAmount(1000), IFluidHandler.FluidAction.EXECUTE);
						spendEnergy(energy);
						bp.getPickupSound(minedState).ifPresent(se -> fakePlayer.playSound(se, 1.0F, 1.0F));
						lvl.gameEvent(fakePlayer, GameEvent.FLUID_PICKUP, targetPos);
					}
				}
			}
		}
	}
	
	public static final Map<Block, List<ItemStack>> cachedLootTables = new HashMap<>();
	private boolean shouldMineBlock(ServerLevel sLevel, BlockPos targetPos) {
		ToggleConfig alwaysMine = getConfiguration().get(ToggleConfig.class, "always_mine");
		if (alwaysMine.isEnabled()) return true;
		BlockState state = sLevel.getBlockState(targetPos);
		List<ItemStack> table = cachedLootTables.computeIfAbsent(state.getBlock(), block ->
			LootTableHelper.getItemsWithCounts(sLevel, state.getBlock().getLootTable())
				.entrySet().stream()
				.map(entry -> new ItemStack(entry.getKey(), entry.getValue().getSecond().intValue()))
				.toList()
		);
		for (ItemStack stack : table) {
			if (!stack.isEmpty()) {
				var lastPair = ItemHelper.insertItemStackedForced(inventory, stack, true, outputSlots);
				if (!lastPair.getFirst().isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
}
