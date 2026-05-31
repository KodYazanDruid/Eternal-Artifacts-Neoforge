package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class ElectricBeehive extends GenericMachine {
	public ElectricBeehive(BlockPos pos, BlockState blockState) {
		super(ModMachines.ELECTRIC_BEEHIVE, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, true, false));
		outputSlots.add(1);
		setInventory(() -> createBasicInventory(2, outputSlots,
			(slot, stack) -> slot == 0 ? stack.canPerformAction(ToolActions.SHEARS_HARVEST) : stack.is(Items.HONEYCOMB)));
		this.isChargeProgress = true;
	}
	
	@Override
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		
		BlockState upperState = lvl.getBlockState(pos.above());
		progressCharge(() -> !(upperState.getBlock() instanceof BeehiveBlock), () -> handleBeehive(lvl, pos), energy);
	}
	
	private boolean handleBeehive(Level lvl, BlockPos pos) {
		BlockPos hivePos = pos.above();
		BlockState upperState = lvl.getBlockState(hivePos);
		if (upperState.getBlock() instanceof BeehiveBlock) {
			int honeyLevel = upperState.getValue(BeehiveBlock.HONEY_LEVEL);
			if (honeyLevel >= BeehiveBlock.MAX_HONEY_LEVELS) {
				ItemStack tool = inventory.getStackInSlot(0);
				if (isVersatile()) {
					boolean ret = harvestHoney();
					if (tool.canPerformAction(ToolActions.SHEARS_HARVEST)) ret = ret || harvestHoneyComb();
					if (ret) resetHoneyLevel(lvl, hivePos);
					return ret;
				} else {
					if (tool.canPerformAction(ToolActions.SHEARS_HARVEST)) {
						boolean ret = harvestHoneyComb();
						if (ret) resetHoneyLevel(lvl, hivePos);
						return ret;
					} else {
						boolean ret = harvestHoney();
						if (ret) resetHoneyLevel(lvl, hivePos);
						return ret;
					}
				}
			}
		}
		return false;
	}
	
	private boolean harvestHoneyComb() {
		ItemStack remainder = ItemHelper.insertItemStackedForced(inventory, new ItemStack(Items.HONEYCOMB, 3), true, outputSlots).getFirst();
		if (remainder.isEmpty()) {
			ItemStack copy = inventory.getStackInSlot(0).copy();
			copy.hurt(1, level.getRandom(), null);
			inventory.setStackInSlot(0, copy);
			ItemHelper.insertItemStackedForced(inventory, new ItemStack(Items.HONEYCOMB, 3), false, outputSlots);
			return true;
		}
		return false;
	}
	
	private boolean harvestHoney() {
		int filled = tank.fillForced(new FluidStack(ModFluids.HONEY.getFluid(), 250), IFluidHandler.FluidAction.SIMULATE);
		if (filled == 250) {
			tank.fillForced(new FluidStack(ModFluids.HONEY.getFluid(), 250), IFluidHandler.FluidAction.EXECUTE);
			return true;
		}
		return false;
	}
	
	private void resetHoneyLevel(Level level, BlockPos hivePos) {
		level.setBlockAndUpdate(hivePos, level.getBlockState(hivePos).setValue(BeehiveBlock.HONEY_LEVEL, 0));
	}
	
}
