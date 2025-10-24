package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class BottleFluidWrapper implements IFluidHandlerItem {
	private ItemStack bottle;
	
	public BottleFluidWrapper(ItemStack bottle) {
		this.bottle = bottle;
	}
	
	public FluidStack getFluid() {
		if (bottle.is(Items.GLASS_BOTTLE)) return FluidStack.EMPTY;
		if (bottle.is(Items.EXPERIENCE_BOTTLE)) return new FluidStack(ModFluids.NOUS.getFluid(), 250);
		if (bottle.getItem() instanceof PotionItem) {
			Potion potion = PotionUtils.getPotion(bottle);
			if (potion == Potions.WATER) return new FluidStack(Fluids.WATER, 250);
			FluidStack potionFluid = new FluidStack(ModFluids.POTION.getFluid(), 250);
			if (bottle.hasTag()){
				CompoundTag bottleTag = bottle.getTag();
				CompoundTag nbt = new CompoundTag();
				if (bottleTag.contains("Potion")) nbt.put("Potion", bottleTag.get("Potion"));
				if (bottleTag.contains("custom_potion_effects")) nbt.put("custom_potion_effects", bottleTag.get("custom_potion_effects"));
				potionFluid.setTag(nbt);
				return potionFluid;
			}
		}
		return FluidStack.EMPTY;
	}
	
	public void setFluid(FluidStack fluid) {
		if (fluid.isEmpty()) {
			if (bottle.is(Items.SPLASH_POTION)) bottle = new ItemStack(ModItems.GLASS_SPLASH_BOTTLE.get());
			else if (bottle.is(Items.LINGERING_POTION)) bottle = new ItemStack(ModItems.GLASS_LINGERING_BOTTLE.get());
			else bottle = new ItemStack(Items.GLASS_BOTTLE);
			return;
		}
		if (fluid.is(Fluids.WATER)) {
			if (bottle.is(ModItems.GLASS_SPLASH_BOTTLE)) bottle = new ItemStack(Items.SPLASH_POTION);
			else if (bottle.is(ModItems.GLASS_LINGERING_BOTTLE)) bottle = new ItemStack(Items.LINGERING_POTION);
			else bottle = new ItemStack(Items.POTION);
			PotionUtils.setPotion(bottle, Potions.WATER);
			return;
		}
		if (fluid.is(ModTags.Fluids.EXPERIENCE)) {
			bottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
			return;
		}
		if (fluid.is(ModTags.Fluids.POTION)) {
			if (bottle.is(ModItems.GLASS_SPLASH_BOTTLE)) bottle = new ItemStack(Items.SPLASH_POTION);
			else if (bottle.is(ModItems.GLASS_LINGERING_BOTTLE)) bottle = new ItemStack(Items.LINGERING_POTION);
			else bottle = new ItemStack(Items.POTION);
			PotionUtils.setPotion(bottle, Potions.EMPTY);
			if (fluid.hasTag()) {
				CompoundTag bottleTag = new CompoundTag();
				CompoundTag fluidTag = fluid.getTag();
				if (fluidTag.contains("Potion")) bottleTag.put("Potion", fluidTag.get("Potion"));
				if (fluidTag.contains("custom_potion_effects")) bottleTag.put("custom_potion_effects", fluidTag.get("custom_potion_effects"));
				bottle.setTag(bottleTag);
			}
		}
	}
	
	@Override
	public ItemStack getContainer() {
		return bottle;
	}
	
	@Override
	public int getTanks() {
		return 1;
	}
	
	@Override
	public FluidStack getFluidInTank(int tank) {
		return getFluid();
	}
	
	@Override
	public int getTankCapacity(int tank) {
		return 250;
	}
	
	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return stack.is(ModTags.Fluids.POTION) || stack.is(Fluids.WATER) || stack.is(ModTags.Fluids.EXPERIENCE);
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (bottle.getCount() != 1 || resource.getAmount() < 250 ||
			!getFluid().isEmpty()|| !isFluidValid(0, resource)) return 0;
		
		if (action.execute()) {
			setFluid(resource);
		}
		
		return 250;
	}
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (bottle.getCount() != 1 || resource.getAmount() < 250) return FluidStack.EMPTY;
		
		FluidStack fluid = getFluid();
		if (!fluid.isEmpty() && fluid.isFluidEqual(resource)) {
			if (action.execute()) {
				setFluid(FluidStack.EMPTY);
			}
			return fluid;
		};
		
		return FluidStack.EMPTY;
	}
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (bottle.getCount() != 1 || maxDrain < 250) return FluidStack.EMPTY;
		
		FluidStack fluid = getFluid();
		if (!fluid.isEmpty()) {
			if (action.execute()) {
				setFluid(FluidStack.EMPTY);
			}
			return fluid;
		};
		return FluidStack.EMPTY;
	}
}
