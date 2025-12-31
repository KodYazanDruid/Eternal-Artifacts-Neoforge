package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.FluidFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.FluidStackEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemStackEntry;
import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.ConfigLocations;
import com.sonamorningstar.eternalartifacts.api.machine.config.ReverseToggleConfig;
import com.sonamorningstar.eternalartifacts.container.BlockInteractorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

@Getter
public class BlockPlacer extends SidedTransferMachine<BlockInteractorMenu> implements Filterable {
	private final NonNullList<ItemFilterEntry> itemFilters = NonNullList.withSize(9, ItemStackEntry.EMPTY);
	private final NonNullList<FluidFilterEntry> fluidFilters = NonNullList.withSize(9, FluidStackEntry.EMPTY);
	
	private boolean itemFilterWhitelist = false;
	private boolean fluidFilterWhitelist = false;
	private boolean itemFilterIgnoreNBT = true;
	private boolean fluidFilterIgnoreNBT = true;
	
	public BlockPlacer(BlockPos pos, BlockState blockState) {
		super(ModMachines.BLOCK_PLACER.getBlockEntity(), pos, blockState, (a, b, c, d) -> new BlockInteractorMenu(ModMachines.BLOCK_PLACER.getMenu(), a, b, c, d));
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, fs -> !fs.getFluid().defaultFluidState().createLegacyBlock().isEmpty(), true, true));
		setInventory(() -> createBasicInventory(4, (slot, stack) -> stack.getItem() instanceof BlockItem));
		setEnergyPerTick(250);
	}
	
	@Override
	public void setItemFilterWhitelist(boolean whitelist) {
		this.itemFilterWhitelist = whitelist;
		setChanged();
	}
	
	@Override
	public void setFluidFilterWhitelist(boolean whitelist) {
		this.fluidFilterWhitelist = whitelist;
		setChanged();
	}
	
	@Override
	public void setItemFilterIgnoreNBT(boolean ignoreNBT) {
		this.itemFilterIgnoreNBT = ignoreNBT;
		setChanged();
	}
	
	@Override
	public void setFluidFilterIgnoreNBT(boolean ignoreNBT) {
		this.fluidFilterIgnoreNBT = ignoreNBT;
		setChanged();
	}
	
	// Silent setters for loading from NBT
	@Override
	public void setItemFilterWhitelistSilent(boolean whitelist) {
		this.itemFilterWhitelist = whitelist;
	}
	
	@Override
	public void setFluidFilterWhitelistSilent(boolean whitelist) {
		this.fluidFilterWhitelist = whitelist;
	}
	
	@Override
	public void setItemFilterIgnoreNBTSilent(boolean ignoreNBT) {
		this.itemFilterIgnoreNBT = ignoreNBT;
	}
	
	@Override
	public void setFluidFilterIgnoreNBTSilent(boolean ignoreNBT) {
		this.fluidFilterIgnoreNBT = ignoreNBT;
	}
	
	@Override
	public void registerConfigs() {
		super.registerConfigs();
		getConfiguration().add(new ReverseToggleConfig("block_mode"));
		getConfiguration().add(new ReverseToggleConfig("fluid_mode"));
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputItems(lvl, pos);
		performAutoOutputItems(lvl, pos);
		performAutoInputFluids(lvl, pos);
		if (!redstoneChecks(lvl)) return;
		
		getFakePlayer();
		setupFakePlayer(st);
		Direction facing = st.getValue(BlockStateProperties.FACING);
		BlockPos targetPos = getBlockPos().relative(facing);
		BlockState targetState = lvl.getBlockState(targetPos);
		ItemStack toPlace = getPlaceableItem();
		FluidStack toPlaceFluid = tank.getFluid(0);
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			fakePlayer.getInventory().items.set(i, stack);
			if (stack == toPlace) fakePlayer.getInventory().selected = i;
		}
		MachineConfiguration configs = getConfiguration();
		if (!((ReverseToggleConfig) configs.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "block_mode"))).isDisabled()
			&& !toPlace.isEmpty() && canWork(energy)) {
			BlockHitResult hitResult = new BlockHitResult(Vec3.atBottomCenterOf(targetPos), facing.getOpposite(), targetPos, true);
			BlockPlaceContext ctx = new BlockPlaceContext(fakePlayer, InteractionHand.MAIN_HAND, toPlace, hitResult);
			InteractionResult result = ((BlockItem) toPlace.getItem()).place(ctx);
			if (result.consumesAction()) spendEnergy(energy);
		}
		if (!((ReverseToggleConfig) configs.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "fluid_mode"))).isDisabled()
			&& shouldPlaceFluid(fakePlayer, lvl, targetPos, toPlaceFluid) && !toPlaceFluid.isEmpty() && toPlaceFluid.getAmount() >= 1000 && canWork(energy)) {
			boolean isPlaced = false;
			if (targetState.getBlock() instanceof LiquidBlockContainer con) {
				con.placeLiquid(lvl, targetPos, targetState, toPlaceFluid.getFluidType().getStateForPlacement(lvl, targetPos, toPlaceFluid));
				isPlaced = true;
			} else if (targetState.isAir()) {
				lvl.setBlock(targetPos, toPlaceFluid.getFluid().defaultFluidState().createLegacyBlock(), 11);
				isPlaced = true;
			}
			if (isPlaced) {
				playEmptySound(fakePlayer, lvl, targetPos, toPlaceFluid.getFluid());
				tank.drainForced(1000, IFluidHandler.FluidAction.EXECUTE);
				spendEnergy(energy);
			}
		}
	}
	
	private boolean shouldPlaceFluid(FakePlayer fakePlayer, Level level, BlockPos target, FluidStack toPlace) {
		if (!matchesFluidFilter(toPlace)) return false;
		if (toPlace.getFluid().defaultFluidState().createLegacyBlock().isEmpty()) return false;
		FluidState fluidState = level.getFluidState(target);
		if (!fluidState.isEmpty()) return false;
		if (toPlace.getFluid().getFluidType().isVaporizedOnPlacement(level, target, toPlace)) return false;
		BlockState blockState = level.getBlockState(target);
		return blockState.isAir() || (blockState.getBlock() instanceof LiquidBlockContainer con && con.canPlaceLiquid(fakePlayer, level, target, blockState, toPlace.getFluid()));
	}
	
	private void playEmptySound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, Fluid toPlace) {
		SoundEvent soundevent = toPlace.getFluidType().getSound(pPlayer, pLevel, pPos, net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY);
		if(soundevent == null) soundevent = toPlace.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
		pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);
	}
	
	private ItemStack getPlaceableItem() {
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof BlockItem) {
				if (matchesItemFilter(stack)) return stack;
			}
		}
		return ItemStack.EMPTY;
	}
}
