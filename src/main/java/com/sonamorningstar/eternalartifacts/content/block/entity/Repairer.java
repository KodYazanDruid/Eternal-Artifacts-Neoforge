package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class Repairer extends GenericMachine {
	public static final int XP_COST = 1;
	public static final int DEFAULT_REPAIR_AMOUNT = 1;
	
	public Repairer(BlockPos pos, BlockState blockState) {
		super(ModMachines.REPAIRER, pos, blockState);
		outputSlots.add(1);
		setEnergy(this::createDefaultEnergy);
		setInventory(() -> createBasicInventory(2,
			(slot, stack) -> slot == 0 ? stack.isDamageableItem() : slot == 1 && !outputSlots.contains(1))
		);
		setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.EXPERIENCE), false, true));
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputFluids(lvl, pos);
		
		ItemStack input = inventory.getStackInSlot(0);
		ItemStack output = inventory.getStackInSlot(1);
		int modifiedRepairAmount = DEFAULT_REPAIR_AMOUNT + getEnchantmentLevel(ModEnchantments.CELERITY.get());
		modifiedRepairAmount = Math.min(modifiedRepairAmount, input.getDamageValue());
		
		if (!input.isEmpty() && input.isDamaged() && canWork(energy) &&
				tank.getFluidAmount(0) >= XP_COST * modifiedRepairAmount) {
			tank.get(0).drainForced(XP_COST * modifiedRepairAmount, IFluidHandler.FluidAction.EXECUTE);
			energy.extractEnergy(getEnergyPerTick(), false);
			modifiedRepairAmount = modifiedRepairAmount + (modifiedRepairAmount * getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY) / 5);
			input.setDamageValue(input.getDamageValue() - modifiedRepairAmount);
			setChanged();
		}
		if (!input.isEmpty() && output.isEmpty() && !input.isDamaged()) {
			inventory.setStackInSlot(1, input.copy());
			inventory.setStackInSlot(0, ItemStack.EMPTY);
			setChanged();
		}
	}
	
}
