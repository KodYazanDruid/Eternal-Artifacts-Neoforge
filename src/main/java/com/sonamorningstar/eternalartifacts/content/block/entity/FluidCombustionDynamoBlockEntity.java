package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.container.FluidCombustionMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidCombustionRecipe;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.dynamo.DynamoProcessCache;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;


public class FluidCombustionDynamoBlockEntity extends MachineBlockEntity<FluidCombustionMenu> implements ITickableClient {
    public FluidCombustionDynamoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), pos, blockState, FluidCombustionMenu::new);
    }

    private int tickCounter = 0;
    public boolean isWorking = false;
    private DynamoProcessCache cache;

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
            if(currentRecipe != null && currentRecipe instanceof FluidCombustionRecipe fcr) {
                setEnergyPerTick(fcr.getGeneration());
                setMaxProgress(fcr.getDuration());
            }
        }
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Energy", energy.serializeNBT());
        tag.putBoolean("IsWorking", isWorking);
        tank.writeToNBT(tag);
        if(cache != null) cache.writeToNbt(tag);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        energy.deserializeNBT(tag.get("Energy"));
        isWorking = tag.getBoolean("IsWorking");
        tank.readFromNBT(tag);
        cache = DynamoProcessCache.readFromNbt(tag, energy, this).orElse(null);
        super.load(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        findRecipe(ModRecipes.FLUID_COMBUSTING_TYPE.get(), tank.getFluid().getFluid());
        if(currentRecipe != null && currentRecipe instanceof FluidCombustionRecipe fcr) {
            setEnergyPerTick(fcr.getGeneration());
            setMaxProgress(fcr.getDuration());
        }
    }

    public float getAnimationLerp(float tick) {
        return isWorking ? Mth.lerp((1.0F - Mth.cos((tick + tickCounter) * 0.25F) ) / 2F, 4.0F, 9.0F) : 4.0F;
    }

    @Override
    public void tickClient(Level lvl, BlockPos pos, BlockState st) {
        if(isWorking) tickCounter++;
        else tickCounter = 0;
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        //TODO: Create parent DynamoBlockEntity class and move this to there.
        if(cache != null) {
            if(!cache.isDone()) {
                cache.process();
                FluidCombustionDynamoBlockEntity.this.sendUpdate();
            } else if (cache.isDone()) {
                cache = null;
                FluidCombustionDynamoBlockEntity.this.sendUpdate();
            }
        }else {
            if(currentRecipe instanceof  FluidCombustionRecipe) {
                FluidStack drained = tank.drainForced(50, IFluidHandler.FluidAction.SIMULATE);
                if(drained.getAmount() == 50) {
                    tank.drainForced(50, IFluidHandler.FluidAction.EXECUTE);
                    cache = new DynamoProcessCache(maxProgress, energy, energyPerTick, this);
                }
            }
        }

    }

}
