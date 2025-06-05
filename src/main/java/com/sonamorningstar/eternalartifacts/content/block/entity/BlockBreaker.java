package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockBreaker extends GenericMachine {
	public boolean blockMode = true;
	public boolean fluidMode = false;
	public BlockBreaker(BlockPos pos, BlockState blockState) {
		super(ModMachines.BLOCK_BREAKER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(this::createDefaultTank);
		outputSlots.addAll(List.of(1, 2, 3, 4, 5, 6, 7, 8));
		setInventory(() -> createBasicInventory(9, outputSlots, (slot, stack) -> slot == 0 && canStackDig(stack)));
		setEnergyPerTick(250);
		screenInfo.setShouldDrawArrow(false);
		screenInfo.setSlotPosition(46, 44, 0);
		screenInfo.setSlotPosition(64, 35, 1);
		screenInfo.setSlotPosition(82, 35, 2);
		screenInfo.setSlotPosition(100, 35, 3);
		screenInfo.setSlotPosition(118, 35, 4);
		screenInfo.setSlotPosition(64, 53, 5);
		screenInfo.setSlotPosition(82, 53, 6);
		screenInfo.setSlotPosition(100, 53, 7);
		screenInfo.setSlotPosition(118, 53, 8);
		
		screenInfo.addButton(MODID, "textures/gui/sprites/blank_red.png", 140, 28, 16, 16, () -> {
			blockMode = !blockMode;
			if (level != null && !level.isClientSide()) sendUpdate();
		});
		screenInfo.addButton(MODID, "textures/gui/sprites/blank_red.png", 140, 48, 16, 16, () -> {
			fluidMode = !fluidMode;
			if (level != null && !level.isClientSide()) sendUpdate();
		});
	}
	
	private boolean canStackDig(ItemStack stack) {
		Item item = stack.getItem();
		return item.canPerformAction(stack, ToolActions.SHOVEL_DIG) ||
			item.canPerformAction(stack, ToolActions.AXE_DIG) ||
			item.canPerformAction(stack, ToolActions.PICKAXE_DIG) ||
			item.canPerformAction(stack, ToolActions.HOE_DIG) ||
			item.canPerformAction(stack, ToolActions.SWORD_DIG);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("BlockMode", blockMode);
		tag.putBoolean("FluidMode", fluidMode);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		blockMode = tag.getBoolean("BlockMode");
		fluidMode = tag.getBoolean("FluidMode");
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		destroyTickStart = -1;
		if (level != null && !level.isClientSide()) {
			level.destroyBlockProgress(FakePlayerHelper.getFakePlayer(this, level).getId(), getBlockPos().relative(getBlockState().getValue(BlockStateProperties.FACING)), -1);
		}
	}
	
	public int destroyTickStart = -1;
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		if (!redstoneChecks(redstoneConfigs.get(0), lvl)) return;
		
		FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, level);
		fakePlayer.setYRot(st.getValue(BlockStateProperties.FACING).toYRot());
		fakePlayer.setPosRaw(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
		ItemStack tool = inventory.getStackInSlot(0);
		fakePlayer.getInventory().selected = 0;
		fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, tool);
		for (int i = 0; i < inventory.getSlots(); i++) {
			if (i == 0) continue;
			ItemStack stack = inventory.getStackInSlot(i);
			fakePlayer.getInventory().setItem(i, stack);
		}
		BlockPos targetPos = getBlockPos().relative(st.getValue(BlockStateProperties.FACING));
		BlockState minedState = lvl.getBlockState(targetPos);
		ServerPlayerGameMode gameMode = fakePlayer.gameMode;
		if (blockMode && !lvl.getBlockState(targetPos).isAir() && canWork(energy) &&
				minedState.getBlock().canHarvestBlock(minedState, lvl, targetPos, fakePlayer)){
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
		if (fluidMode && !fluidState.isEmpty() && canWork(energy) &&
				minedState.getBlock() instanceof BucketPickup bp && fluidState.isSource() &&
				(tank.getFluid(0).isEmpty() || tank.getFluid(0).is(fluidState.getType()))) {
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
}
