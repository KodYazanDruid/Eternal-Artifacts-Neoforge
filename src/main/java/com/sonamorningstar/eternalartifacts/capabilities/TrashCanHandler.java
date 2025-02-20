package com.sonamorningstar.eternalartifacts.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class TrashCanHandler implements IItemHandler, IFluidHandler, IEnergyStorage {
	private static final FluidStack EMPTY_FLUID = FluidStack.EMPTY;
	private static final ItemStack EMPTY_STACK = ItemStack.EMPTY;
	
	public static TrashCanHandler registerCapability(Level level, BlockPos pos, BlockState state, BlockEntity be, Direction dir) {
		return new TrashCanHandler();
	}
	
	//region Energy.
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return maxReceive;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}
	
	@Override
	public int getEnergyStored() {
		return 0;
	}
	
	@Override
	public int getMaxEnergyStored() {
		return 50000;
	}
	
	@Override
	public boolean canExtract() {
		return false;
	}
	
	@Override
	public boolean canReceive() {
		return true;
	}
	//endregion
	
	//region Fluid.
	@Override
	public int getTanks() {
		return 1;
	}
	
	@Override
	public FluidStack getFluidInTank(int tank) {
		return EMPTY_FLUID;
	}
	
	@Override
	public int getTankCapacity(int tank) {
		return 16000;
	}
	
	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return true;
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return resource.getAmount();
	}
	
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return EMPTY_FLUID;
	}
	
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return EMPTY_FLUID;
	}
	//endregion
	
	//region Item.
	@Override
	public int getSlots() {
		return 9;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return EMPTY_STACK;
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return EMPTY_STACK;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return EMPTY_STACK;
	}
	
	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}
	
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}
	//endregion
}
