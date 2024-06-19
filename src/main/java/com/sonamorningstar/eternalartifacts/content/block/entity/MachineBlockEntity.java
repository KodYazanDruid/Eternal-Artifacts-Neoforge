package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.util.QuadFunction;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public abstract class MachineBlockEntity<T extends AbstractMachineMenu> extends ModBlockEntity implements MenuProvider, ITickable {
    Lazy<BlockEntity> entity;
    QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF;
    protected Recipe<SimpleContainer> currentRecipe = null;
    @Getter
    @Setter
    protected Map<Integer, SidedTransferMachineBlockEntity.RedstoneType> redstoneConfigs = new HashMap<>(1);
    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF) {
        super(type, pos, blockState);
        entity = Lazy.of(()->level.getBlockEntity(pos));
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
    protected int consume = 40;

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
        return Component.translatable(entity.get().getBlockState().getBlock().getDescriptionId());
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
        if(!hasEnergy(consume, energy)) return;
        SidedTransferMachineBlockEntity.RedstoneType type = redstoneConfigs.get(0);
        if(type == SidedTransferMachineBlockEntity.RedstoneType.HIGH && level.hasNeighborSignal(getBlockPos()) ||
            type == SidedTransferMachineBlockEntity.RedstoneType.LOW && !level.hasNeighborSignal(getBlockPos()) ||
            (type == SidedTransferMachineBlockEntity.RedstoneType.IGNORED || type == null)){
            if (test.getAsBoolean()) {
                progress = 0;
                return;
            }
            energy.extractEnergyForced(consume, false);
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

    protected <R extends Recipe<SimpleContainer>> void findRecipe(RecipeType<R> recipeType, SimpleContainer container) {
        if(currentRecipe != null && currentRecipe.matches(container, level)) return;
        currentRecipe = null;
        List<R> recipeList = level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
        for(R recipe : recipeList) {
            if(recipe.matches(container, level)) {
                currentRecipe = recipe;
                return;
            }
        }
    }

    //This is for one recipe type. Generics are useless i know. I need to change it later.
    protected <R extends Recipe<SimpleContainer>> void findRecipe(RecipeType<R> recipeType, EntityType<?> type) {
        if(currentRecipe != null && ((MobLiquifierRecipe) currentRecipe).matches(type)) return;
        currentRecipe = null;
        List<R> recipeList = level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
        for(R recipe : recipeList) {
            if(((MobLiquifierRecipe) recipe).matches(type)) {
                currentRecipe = recipe;
                return;
            }
        }
    }

}
