package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.util.QuadFunction;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public abstract class MachineBlockEntity<T extends AbstractMachineMenu> extends ModBlockEntity implements MenuProvider, ITickableServer {
    QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF;
    protected Recipe<?> currentRecipe = null;
    @Getter
    @Setter
    protected Map<Integer, SidedTransferMachineBlockEntity.RedstoneType> redstoneConfigs = new HashMap<>(1);
    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF) {
        super(type, pos, blockState);
        this.quadF = quadF;
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> throw new IllegalStateException("Unexpected value: " + index);
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 ->  progress = value;
                    case 1 -> maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    protected ContainerData data;
    protected int progress;
    @Setter
    protected int maxProgress = 100;
    @Setter
    protected int energyPerTick = 40;

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return quadF.apply(pContainerId, pPlayerInventory, this, data);
    }

    protected void fillTankFromSlot(ModItemStorage inventory, ModFluidStorage tank, int fluidSlot) {
        ItemStack stack = inventory.getStackInSlot(fluidSlot);
        if(!stack.isEmpty() && tank.getFluidAmount() < tank.getCapacity()) {
            IFluidHandlerItem itemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if(itemHandler != null && tank.isFluidValid(itemHandler.getFluidInTank(0)) &&
                    (tank.getFluid().getFluid() == itemHandler.getFluidInTank(0).getFluid() || tank.getFluid().isEmpty())) {
                int amountToDrain = tank.getCapacity() - tank.getFluidAmount();
                int amount = itemHandler.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE).getAmount();
                if (amount > 0) {
                    tank.fill(itemHandler.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                }
                if (amount <= amountToDrain) {
                    inventory.setStackInSlot(fluidSlot, itemHandler.getContainer());
                }
            }
        }
    }

    protected void progress(BooleanSupplier test, Runnable run, ModEnergyStorage energy) {
        if(!hasEnergy(energyPerTick, energy) || level == null) return;
        SidedTransferMachineBlockEntity.RedstoneType type = redstoneConfigs.get(0);
        if(type == SidedTransferMachineBlockEntity.RedstoneType.HIGH && level.hasNeighborSignal(getBlockPos()) ||
            type == SidedTransferMachineBlockEntity.RedstoneType.LOW && !level.hasNeighborSignal(getBlockPos()) ||
            (type == SidedTransferMachineBlockEntity.RedstoneType.IGNORED || type == null)){
            if (test.getAsBoolean()) {
                progress = 0;
                return;
            }
            energy.extractEnergyForced(energyPerTick, false);
            progress++;
            if (progress >= maxProgress) {
                run.run();
                progress = 0;
            }
        }
    }

    protected boolean hasEnergy(int amount, ModEnergyStorage energy) {
        return energy.extractEnergyForced(amount, true) >= amount;
    }

    protected void insertItemFromDir(Level lvl, BlockPos pos, Direction dir, IItemHandlerModifiable inventory) {
        BlockEntity targetBe = lvl.getBlockEntity(pos.relative(dir));
        if(targetBe != null) {
            IItemHandler sourceInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, targetBe.getBlockPos(), targetBe.getBlockState(), targetBe, dir.getOpposite());
            if(sourceInv != null) {
                for(int i = 0; i < sourceInv.getSlots(); i++) {
                    if(sourceInv.getStackInSlot(i).isEmpty()) continue;
                    ItemStack inserted = ItemHandlerHelper.insertItemStacked(inventory, sourceInv.getStackInSlot(i), true);
                    if(inserted.isEmpty()) {
                        ItemHandlerHelper.insertItemStacked(inventory, sourceInv.getStackInSlot(i).copyWithCount(sourceInv.getStackInSlot(i).getCount()), false);
                        sourceInv.extractItem(i, sourceInv.getStackInSlot(i).getCount(), false);
                    }
                }
            }
        }
    }

    protected void outputItemToDir(Level lvl, BlockPos pos, Direction dir, IItemHandlerModifiable inventory, int... outputSlots) {
        BlockEntity targetBe = lvl.getBlockEntity(pos.relative(dir));
        if(targetBe != null) {
            IItemHandler targetInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, targetBe.getBlockPos(), targetBe.getBlockState(), targetBe, dir.getOpposite());
            if(targetInv != null) {
                for(int output : outputSlots) {
                    try {
                        ItemStack inserted = ItemHandlerHelper.insertItemStacked(targetInv, inventory.getStackInSlot(output), true);
                        if(inserted.isEmpty()) {
                            ItemHandlerHelper.insertItemStacked(targetInv, inventory.getStackInSlot(output), false);
                            inventory.extractItem(output, inventory.getStackInSlot(output).getCount(), false);
                        }
                    }catch (IndexOutOfBoundsException e) {
                        EternalArtifacts.LOGGER.error("Output slot {} is out of bounds in {} sized inventory", output, targetInv.getSlots());
                    }
                }
            }
        }
    }

    protected void inputFluidFromDir(Level lvl, BlockPos pos, Direction dir, IFluidHandler tank) {
        transferFluidToBE(lvl, pos, dir, tank, true);
    }

    protected void outputFluidToDir(Level lvl, BlockPos pos, Direction dir, IFluidHandler tank) {
        transferFluidToBE(lvl, pos, dir, tank, false);
    }

    protected void transferFluidToBE(Level lvl, BlockPos pos, Direction dir, IFluidHandler tank, boolean isReverse) {
        BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
        if(be != null) {
            IFluidHandler targetTank = lvl.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
            if(targetTank != null) {
                if(isReverse) FluidUtil.tryFluidTransfer(tank, targetTank, 1000, true);
                else FluidUtil.tryFluidTransfer(targetTank, tank, 1000, true);
            }
        }
    }

    protected void outputEnergyToDir(Level lvl, BlockPos pos, Direction dir, IEnergyStorage energy) {
        BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
        if(be != null) {
            IEnergyStorage target = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
            if(target != null && target.canReceive()) {
                int extracted = energy.extractEnergy(Math.min(energy.getEnergyStored(), target.getMaxEnergyStored() - target.getEnergyStored()), true);
                if(extracted > 0) {
                    target.receiveEnergy(extracted, false);
                    energy.extractEnergy(extracted, false);
                }
            }
        }
    }

    protected <R extends Recipe<C>, C extends Container> @Nullable R findRecipe(RecipeType<R> recipeType, C container) {
        if(level == null) return null;
        List<R> recipeList = level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
        for(R recipe : recipeList) {
            if(recipe.matches(container, level)) {
                currentRecipe = recipe;
                return recipe;
            }
        }
        return null;
    }

    protected <R extends Recipe<C>, C extends Container> @Nullable R findRecipe(RecipeType<R> recipeType, EntityType<?> type) {
        if(level == null) return null;
        if(currentRecipe != null && ((MobLiquifierRecipe) currentRecipe).matches(type)) return null;
        currentRecipe = null;
        List<R> recipeList = level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
        for(R recipe : recipeList) {
            if(((MobLiquifierRecipe) recipe).matches(type)) {
                currentRecipe = recipe;
                return recipe;
            }
        }
        return null;
    }

}
