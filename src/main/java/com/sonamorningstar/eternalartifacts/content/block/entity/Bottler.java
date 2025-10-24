package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
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
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class Bottler extends SidedTransferMachine<BottlerMenu> {
	public boolean mode = false; // false: fill, true: empty the item tank
	public int transferRate = 1000;
	public Bottler(BlockPos pos, BlockState blockState) {
		super(ModMachines.BOTTLER.getBlockEntity(), pos, blockState, (a, b, c, d) ->
			new BottlerMenu(ModMachines.BOTTLER.getMenu(), a, b, c, d));
		setEnergy(this::createDefaultEnergy);
		setTank(() -> {
			int volume = getVolumeLevel();
			return new ModFluidStorage(16000 * (volume + 1)) {
				@Override
				protected void onContentsChanged() {
					sendUpdate();
				}
				@Override
				public FluidStack drain(int maxDrain, FluidAction action) {
					return mode ? super.drain(maxDrain, action) : FluidStack.EMPTY;
				}
				@Override
				public int fill(FluidStack resource, FluidAction action) {
					return mode ? 0 : super.fill(resource, action);
				}
			};
		});
		outputSlots.add(2);
		setInventory(() -> createBasicInventory(3, (slot, stack) ->
			!outputSlots.contains(slot) &&
			slot == 0 &&
			stack.getCapability(Capabilities.FluidHandler.ITEM) != null, slot -> slot == 1 ? 1 : 64));
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
		
		ItemStack container = inventory.getStackInSlot(1);
		
		if (container.isEmpty()) {
			ItemStack pendingTanks = inventory.getStackInSlot(0);
			inventory.setStackInSlot(1, pendingTanks.copyWithCount(1));
			inventory.setStackInSlot(0, pendingTanks.copyWithCount(Math.max(0, pendingTanks.getCount() - 1)));
		}
		if (!container.isEmpty() && canWork(energy)) {
			IFluidHandlerItem containerCap = container.getCapability(Capabilities.FluidHandler.ITEM);
			if (containerCap != null) {
				FluidActionResult result = mode ?
					FluidUtil.tryEmptyContainer(container, this.tank, transferRate, null, true) :
					FluidUtil.tryFillContainer(container, this.tank, transferRate, null, true);
				if (result.isSuccess()) {
					inventory.setStackInSlot(1, result.getResult());
					spendEnergy(energy);
				}
				containerCap = container.getCapability(Capabilities.FluidHandler.ITEM);
				if (containerCap == null || (mode ? FluidTankUtils.isFluidHandlerEmpty(containerCap) :
						FluidTankUtils.isFluidHandlerFull(containerCap))
				) inventory.setStackInSlot(1, inventory.insertItemForced(2, container, false));
			}
		}
	}
}
