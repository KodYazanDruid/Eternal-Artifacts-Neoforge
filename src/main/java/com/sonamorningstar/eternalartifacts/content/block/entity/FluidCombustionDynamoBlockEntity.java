package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.container.FluidCombustionMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidCombustionRecipe;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;


public class FluidCombustionDynamoBlockEntity extends MachineBlockEntity<FluidCombustionMenu> implements ITickableClient {
    public FluidCombustionDynamoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), pos, blockState, FluidCombustionMenu::new);
    }

    private int tickCounter = 0;
    private boolean isWorking = false;

    public ModEnergyStorage energy = new ModEnergyStorage(20000, 2500) {
        @Override
        public void onEnergyChanged() {
            FluidCombustionDynamoBlockEntity.this.sendUpdate();
        }
    };

    public ModFluidStorage tank = new ModFluidStorage(16000) {
        @Override
        protected void onContentsChanged() {
            FluidCombustionDynamoBlockEntity.this.sendUpdate();
            findRecipe(ModRecipes.FLUID_COMBUSTING_TYPE.get(), tank.getFluid().getFluid());
        }
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energy.serializeNBT());
        tank.writeToNBT(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energy.deserializeNBT(tag.get("Energy"));
        tank.readFromNBT(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        findRecipe(ModRecipes.FLUID_COMBUSTING_TYPE.get(), tank.getFluid().getFluid());
    }

    public float getAnimationLerp(float tick) {
        return progress <= 0 ? 4.0F : Mth.lerp((1.0F - Mth.cos((tick + tickCounter) * 0.25F) ) / 2F, 4.0F, 9.0F);
    }

    @Override
    public void tickClient(Level lvl, BlockPos pos, BlockState st) {
        if(progress > 0) tickCounter++;
        else tickCounter = 0;
    }

    //TODO: needs
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        if(currentRecipe instanceof FluidCombustionRecipe fcr) {
            energyPerTick = fcr.getGeneration();
            maxProgress = fcr.getDuration();
            dynamoProgress(()-> {
                tank.drainForced(1000, IFluidHandler.FluidAction.EXECUTE);
            }, energy);
        } else {
            isWorking = false;
            progress = 0;
        }
    }

    protected void dynamoProgress(Runnable run, ModEnergyStorage energy) {
        if(energy.getEnergyStored() + energyPerTick >= energy.getMaxEnergyStored()) return;
        SidedTransferMachineBlockEntity.RedstoneType type = redstoneConfigs.get(0);
        if(type == SidedTransferMachineBlockEntity.RedstoneType.HIGH && level.hasNeighborSignal(getBlockPos()) ||
                type == SidedTransferMachineBlockEntity.RedstoneType.LOW && !level.hasNeighborSignal(getBlockPos()) ||
                (type == SidedTransferMachineBlockEntity.RedstoneType.IGNORED || type == null)){
            energy.receiveEnergyForced(energyPerTick, false);
            progress++;
            isWorking = true;
            if (progress >= maxProgress) {
                run.run();
                progress = 0;
            }
        }
    }

}
