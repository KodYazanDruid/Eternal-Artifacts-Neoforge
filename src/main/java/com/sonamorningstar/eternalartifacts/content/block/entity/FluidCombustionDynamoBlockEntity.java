package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.container.FluidCombustionMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidCombustionRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.caches.DynamoProcessCache;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidCombustionDynamoBlockEntity extends MachineBlockEntity<FluidCombustionMenu> implements ITickableClient {
    public FluidCombustionDynamoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), pos, blockState, FluidCombustionMenu::new);
    }

    private int tickCounter = 0;
    public boolean isWorking = false;
    private DynamoProcessCache cache;
    private final RecipeCache<FluidCombustionRecipe, SimpleFluidContainer> recipeCache = new RecipeCache<>();

    public ModEnergyStorage energy = new ModEnergyStorage(20000, 2500) {
        @Override
        public void onEnergyChanged() {
            FluidCombustionDynamoBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    public ModFluidStorage tank = new ModFluidStorage(16000) {
        @Override
        protected void onContentsChanged() {
            FluidCombustionDynamoBlockEntity.this.sendUpdate();
            findRecipeAndSet();
        }
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Energy", energy.serializeNBT());
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
        //tickCounter = tag.getInt("AnimationTick");
        super.load(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        findRecipeAndSet();
    }

    private void findRecipeAndSet() {
        recipeCache.findRecipe(ModRecipes.FLUID_COMBUSTING_TYPE.get(), new SimpleFluidContainer(tank.getFluid()), level);
        if(recipeCache.getRecipe() != null) {
            setEnergyPerTick(recipeCache.getRecipe().getGeneration());
            setMaxProgress(recipeCache.getRecipe().getDuration());
        }
    }

    @Override
    protected void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
        tag.putBoolean("IsWorking", isWorking);
        //tag.putInt("AnimationTick", tickCounter);
    }

    public float getAnimationLerp(float tick) {
        return isWorking ? Mth.lerp((1.0F - Mth.cos((tick + tickCounter) * 0.25F)) / 2F, 4.0F, 9.0F) : 4.0F;
    }

    @Override
    public void tickClient(Level lvl, BlockPos pos, BlockState st) {
        if(isWorking) tickCounter++;
        else tickCounter = 0;
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        if(energy.getEnergyStored() > 0) outputEnergyToDir(lvl, pos, getBlockState().getValue(BlockStateProperties.FACING), energy);

        //TODO: Create parent DynamoBlockEntity class and move this to there.
        if(cache != null) {
            if(!cache.isDone()) {
                cache.process();
                progress++;
                isWorking = true;
                sendUpdate();
            } else if (cache.isDone()) {
                cache = null;
                progress = 0;
                if(recipeCache.getRecipe() == null){
                    isWorking = false;
                    sendUpdate();
                }
            }
        }else{
            if(recipeCache.getRecipe() != null) {
                FluidStack drained = tank.drainForced(50, IFluidHandler.FluidAction.SIMULATE);
                if(drained.getAmount() == 50) {
                    tank.drainForced(50, IFluidHandler.FluidAction.EXECUTE);
                    cache = new DynamoProcessCache(maxProgress, energy, energyPerTick);
                }
            }
        }
    }

}
