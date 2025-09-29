package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.helper.FluidTankUtils;
import com.sonamorningstar.eternalartifacts.container.BottlerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class Bottler extends SidedTransferMachine<BottlerMenu> {
	public boolean mode = false; // false: fill, true: empty the tank
	public int transferRate = 1000;
	public Bottler(BlockPos pos, BlockState blockState) {
		super(ModMachines.BOTTLER.getBlockEntity(), pos, blockState, (a, b, c, d) ->
			new BottlerMenu(ModMachines.BOTTLER.getMenu(), a, b, c, d));
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, true, true));
		outputSlots.add(2);
		setInventory(() -> createBasicInventory(3, (slot, stack) ->
			!outputSlots.contains(slot) &&
			slot == 0 &&
			stack.getCapability(Capabilities.FluidHandler.ITEM) != null, limit -> 1));
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("Mode", mode);
	}
	
	@Override
	public void saveContents(CompoundTag additionalTag) {
		super.saveContents(additionalTag);
		additionalTag.putBoolean("Mode", mode);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		mode = tag.getBoolean("Mode");
	}
	
	@Override
	public void loadContents(CompoundTag additionalTag) {
		super.loadContents(additionalTag);
		mode = additionalTag.getBoolean("Mode");
	}
	
	@Override
	protected void applyEfficiency(int level) {
		transferRate = 1000 * (getEnchantmentLevel(ModEnchantments.CELERITY.get()) + 1);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputItems(lvl, pos);
		performAutoOutputItems(lvl, pos);
		if (mode) performAutoOutputFluids(lvl, pos);
		else performAutoInputFluids(lvl, pos);
		
		ItemStack tank = inventory.getStackInSlot(1);
		
		if (tank.isEmpty()) {
			inventory.setStackInSlot(1, inventory.getStackInSlot(0).copy());
			inventory.setStackInSlot(0, ItemStack.EMPTY);
		}
		// false: fill, true: empty the tank
		if (!tank.isEmpty() && canWork(energy)) {
			IFluidHandlerItem itemTank = tank.getCapability(Capabilities.FluidHandler.ITEM);
			if (itemTank != null) {
				FluidStack transferred;
				if (mode) {
					transferred = FluidUtil.tryFluidTransfer(this.tank, itemTank, transferRate, true);
					if (transferred.isEmpty() && FluidTankUtils.isFluidHandlerEmpty(itemTank)) {
						inventory.setStackInSlot(2, tank.copy());
						inventory.setStackInSlot(1, ItemStack.EMPTY);
					}
				}
				else {
					transferred = FluidUtil.tryFluidTransfer(itemTank, this.tank, transferRate, true);
					if (transferred.isEmpty() && FluidTankUtils.isFluidHandlerFull(itemTank)) {
						inventory.setStackInSlot(2, tank.copy());
						inventory.setStackInSlot(1, ItemStack.EMPTY);
					}
				}
				if (!transferred.isEmpty()) spendEnergy(energy);
			}
		}
	}
}
