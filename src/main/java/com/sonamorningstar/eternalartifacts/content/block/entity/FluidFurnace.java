package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.FluidFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
public class FluidFurnace extends Machine<FluidFurnaceMenu> {
	private int burnTime = 0;
	private int maxBurnTime = 0;
	
	public static final Map<FluidIngredient, Integer> FLUID_FUELS = Map.of(
			FluidIngredient.of(FluidTags.LAVA, 100), 1000,
			FluidIngredient.of(ModTags.Fluids.DIESEL, 100), 2000
	);
	
	public FluidFurnace(BlockPos pos, BlockState state) {
		super(ModMachines.FLUID_FURNACE.getBlockEntity(), pos, state, (a, b, c, d) -> new FluidFurnaceMenu(ModMachines.FLUID_FURNACE.getMenu(),a, b, c, d));
		outputSlots.add(1);
		setInventory(() -> createRecipeFinderInventory(2, outputSlots));
		setTank(() -> createBasicTank(8000, true, true));
		setRecipeTypeAndContainer(RecipeType.SMELTING, () -> new SimpleContainer(inventory.getStackInSlot(0)));
	}
	
	@Override
	protected ContainerData createContainerData() {
		return new ContainerData() {
			@Override
			public int get(int index) {
				return switch (index) {
					case 0 -> progress;
					case 1 -> maxProgress;
					case 2 -> burnTime;
					case 3 -> maxBurnTime;
					default -> throw new IllegalStateException("Unexpected value: " + index);
				};
			}
			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> progress = value;
					case 1 -> maxProgress = value;
					case 2 -> burnTime = value;
					case 3 -> maxBurnTime = value;
				}
			}
			@Override
			public int getCount() {
				return 4;
			}
		};
	}
	
	@Override
	protected void configureProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		if (recipe instanceof SmeltingRecipe smeltingRecipe) {
			ItemStack assembled = smeltingRecipe.assemble(recipeContainer.get(), getLevel().registryAccess());
			condition.queueImport(assembled);
			condition.commitQueuedImports();
		}
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		burnTime = tag.getInt("BurnTime");
		maxBurnTime = tag.getInt("MaxBurnTime");
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("BurnTime", burnTime);
		tag.putInt("MaxBurnTime", maxBurnTime);
	}
	
	@Override
	public void registerConfigs() {}
	
	@Override
	protected boolean canWork(ModEnergyStorage energy) {
		return burnTime > 0;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		if (isWorking()) getBlockState().setValue(BlockStateProperties.LIT, true);
		else getBlockState().setValue(BlockStateProperties.LIT, false);
	}
	
	@Override
	public boolean shouldSyncWorkingState() {
		return true;
	}
	
	@Override
	public void setWorking(boolean working) {
		if (working) getBlockState().setValue(BlockStateProperties.LIT, true);
		else getBlockState().setValue(BlockStateProperties.LIT, false);
		super.setWorking(working);
	}
	
	@Override
	public int spendEnergy(ModEnergyStorage energy) {
		int i = burnTime--;
		markDirty();
		return i;
	}
	
	@Override
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		
		if (burnTime == 0 && !inventory.getStackInSlot(0).isEmpty()) {
			FluidStack fluidStack = tank.getFluidInTank(0);
			FLUID_FUELS.forEach((fluidIngredient, burnTime) -> {
				FluidStack foundStack = fluidIngredient.findSustained(fluidStack);
				if (!foundStack.isEmpty()) {
					FluidStack drained = tank.drain(foundStack.getAmount(), IFluidHandler.FluidAction.SIMULATE);
					if (drained.getAmount() == foundStack.getAmount()) {
						tank.drain(foundStack.getAmount(), IFluidHandler.FluidAction.EXECUTE);
						this.burnTime = burnTime;
						this.maxBurnTime = burnTime;
						markDirty();
					}
				}
			});
		}
		
		progress(() -> processCondition.shouldAbourt() || burnTime == 0, () -> {
			SmeltingRecipe cachedRecipe = (SmeltingRecipe) getCachedRecipe();
			ItemStack assembled = cachedRecipe.assemble(recipeContainer.get(), getLevel().registryAccess());
			ItemHelper.insertItemForced(inventory, assembled, false);
			inventory.extractItem(0, 1, false);
		}, null);
		
		if (inventory.getStackInSlot(0).isEmpty()) {
			burnTime = Math.max(burnTime - 2, 0);
			markDirty();
		}
	}
}
