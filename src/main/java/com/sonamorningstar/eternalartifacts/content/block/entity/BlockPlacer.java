package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockPlacer extends GenericMachine {
	public boolean isContinuous = false;
	public boolean blockMode = true;
	public boolean fluidMode = false;
	public BlockPlacer(BlockPos pos, BlockState blockState) {
		super(ModMachines.BLOCK_PLACER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(this::createDefaultTank);
		setInventory(() -> createBasicInventory(4, (slot, stack) -> stack.getItem() instanceof BlockItem));
		setEnergyPerTick(250);
		screenInfo.setShouldDrawArrow(false);
		screenInfo.setSlotPosition(80, 35, 0);
		screenInfo.setSlotPosition(98, 35, 1);
		screenInfo.setSlotPosition(80, 53, 2);
		screenInfo.setSlotPosition(98, 53, 3);
		
		screenInfo.addButton(MODID, "textures/gui/sprites/blank_red.png", 120, 8, 16, 16, () -> {
			isContinuous = !isContinuous;
			if (level != null && !level.isClientSide()) sendUpdate();
		});
		screenInfo.addButton(MODID, "textures/gui/sprites/blank_red.png", 120, 28, 16, 16, () -> {
			blockMode = !blockMode;
			if (level != null && !level.isClientSide()) sendUpdate();
		});
		screenInfo.addButton(MODID, "textures/gui/sprites/blank_red.png", 120, 48, 16, 16, () -> {
			fluidMode = !fluidMode;
			if (level != null && !level.isClientSide()) sendUpdate();
		});
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("IsContinuous", isContinuous);
		tag.putBoolean("BlockMode", blockMode);
		tag.putBoolean("FluidMode", fluidMode);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		isContinuous = tag.getBoolean("IsContinuous");
		blockMode = tag.getBoolean("BlockMode");
		fluidMode = tag.getBoolean("FluidMode");
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputFluids(lvl, pos);
		if (!redstoneChecks(redstoneConfigs.get(0), lvl)) return;
		
		getFakePlayer();
		setupFakePlayer(st);
		BlockPos targetPos = getBlockPos().relative(st.getValue(BlockStateProperties.FACING));
		BlockState targetState = lvl.getBlockState(targetPos);
		ItemStack toPlace = getPlaceableItem();
		FluidStack toPlaceFluid = tank.getFluid(0);
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			fakePlayer.getInventory().items.set(i, stack);
			if (stack == toPlace) fakePlayer.getInventory().selected = i;
		}
		if (blockMode && shouldPlace(lvl, targetPos) && !toPlace.isEmpty() && canWork(energy)) {
			Direction facing = st.getValue(BlockStateProperties.FACING);
			InteractionResult result = fakePlayer.gameMode.useItemOn(fakePlayer, level, toPlace, InteractionHand.MAIN_HAND,
				new BlockHitResult(fakePlayer.position().relative(facing, 1), facing.getOpposite(), targetPos, true));
			if (result.consumesAction()) spendEnergy(energy);
		}
		if (fluidMode && shouldPlaceFluid(fakePlayer, lvl, targetPos, toPlaceFluid) && !toPlaceFluid.isEmpty() && toPlaceFluid.getAmount() >= 1000 &&
				canWork(energy)) {
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
	
	private boolean shouldPlace(Level level, BlockPos target) {
		BlockState state = level.getBlockState(target);
		FluidState fluidState = level.getFluidState(target);
		return isContinuous || (state.isEmpty() || fluidState.isEmpty());
	}
	private boolean shouldPlaceFluid(FakePlayer fakePlayer, Level level, BlockPos target, FluidStack toPlace) {
		BlockState state = level.getBlockState(target);
		FluidState fluidState = level.getFluidState(target);
		if (!fluidState.isEmpty()) return false;
		if (toPlace.getFluid().getFluidType().isVaporizedOnPlacement(level, target, toPlace)) return false;
		return state.isAir() || (state.getBlock() instanceof LiquidBlockContainer con && con.canPlaceLiquid(fakePlayer, level, target, state, toPlace.getFluid()));
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
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
}
