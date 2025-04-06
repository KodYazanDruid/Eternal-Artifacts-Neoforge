package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidMixingRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.ItemFluidContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidMixer extends GenericMachineBlockEntity {
	public FluidMixer(BlockPos pos, BlockState blockState) {
		super(ModMachines.FLUID_MIXER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> new MultiFluidTank<>(
			createRecipeFinderTank(16000, false, true),
			createRecipeFinderTank(16000, false, true),
			createBasicTank(16000, true, false)
		));
		setInventory(() -> createRecipeFinderInventory(1, (i, s) -> true));
		setRecipeTypeAndContainer(ModRecipes.FLUID_MIXING.getType(), () ->
			new ItemFluidContainer(List.of(inventory.getStackInSlot(0)), List.of(tank.getFluid(0), tank.getFluid(1)))
		);
		screenInfo.setShouldDrawInventoryTitle(false);
		screenInfo.setTankPosition(24, 20, 0);
		screenInfo.setTankPosition(64, 20, 1);
		screenInfo.setTankPosition(124, 20, 2);
		screenInfo.setArrowPos(92, 41);
		screenInfo.setSlotPosition(45, 40, 0);
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		if (recipe instanceof FluidMixingRecipe mixing) {
			condition.initOutputTank(tank.get(2))
				.queueImport(mixing.output()).commitQueuedFluidStackImports();
		}
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputFluids(lvl, pos);
		performAutoOutputFluids(lvl, pos);
		
		FluidMixingRecipe recipe = (FluidMixingRecipe) RecipeCache.getCachedRecipe(this);
		
		progress(() -> {
			tank.get(2).fillForced(recipe.output().copy(), IFluidHandler.FluidAction.EXECUTE);
			SizedIngredient itemIngredient = recipe.itemInput();
			
			if (!itemIngredient.isEmpty())
				inventory.extractItem(0, itemIngredient.getItems()[0].getCount(), false);
			
			FluidIngredient firstIngredient = recipe.fluidInput1();
			FluidIngredient secondIngredient = recipe.fluidInput2();
			
			if (!tank.getFluid(0).isEmpty()) {
				if (firstIngredient.test(tank.getFluid(0)) && !firstIngredient.isEmpty()) {
					tank.get(0).drainForced(firstIngredient.getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
				} else if (secondIngredient.test(tank.getFluid(0)) && !secondIngredient.isEmpty()) {
					tank.get(0).drainForced(secondIngredient.getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
				}
			}
			
			if (!tank.getFluid(1).isEmpty()) {
				if (firstIngredient.test(tank.getFluid(1)) && !firstIngredient.isEmpty()) {
					tank.get(1).drainForced(firstIngredient.getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
				} else if (secondIngredient.test(tank.getFluid(1)) && !secondIngredient.isEmpty()) {
					tank.get(1).drainForced(secondIngredient.getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
				}
			}
		});
	}
}
