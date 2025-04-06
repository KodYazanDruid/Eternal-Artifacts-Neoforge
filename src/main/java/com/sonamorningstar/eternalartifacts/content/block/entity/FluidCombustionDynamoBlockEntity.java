package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.container.FluidCombustionMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidCombustionRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidCombustionDynamoBlockEntity extends MachineBlockEntity<FluidCombustionMenu> implements ITickableClient {
    public FluidCombustionDynamoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), pos, blockState, FluidCombustionMenu::new);
        setEnergy(() -> createBasicEnergy(100000, 5000, false, true));
        setTank(() -> createRecipeFinderTank(16000, false, true));
        setRecipeTypeAndContainer(ModRecipes.FLUID_COMBUSTING.getType(), () -> new SimpleFluidContainer(tank.getFluid(0)));
    }

    private int tickCounter = 0;
    public boolean isWorking = false;
    @Getter
    private DynamoProcessCache cache;

    @Override
    protected void saveAdditional(CompoundTag tag) {
        if(cache != null) cache.writeToNbt(tag);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        isWorking = tag.getBoolean("IsWorking");
        cache = DynamoProcessCache.readFromNbt(tag, energy, this).orElse(null);
        super.load(tag);
    }

    @Override
    protected void findRecipe() {
        super.findRecipe();
        if (RecipeCache.getCachedRecipe(this) instanceof FluidCombustionRecipe recipe) {
            int celerity = getEnchantmentLevel(ModEnchantments.CELERITY.get());
            setEnergyPerTick(recipe.getGeneration() * ((celerity / 3) + 1));
            int eff = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
            setMaxProgress(recipe.getDuration() * ((eff / 5) + 1));
        }
    }

    @Override
    protected void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
        tag.putBoolean("IsWorking", isWorking);
    }

    @Override
    public void saveContents(CompoundTag additionalTag) {
        super.saveContents(additionalTag);
        if(cache != null) cache.writeToNbt(additionalTag);
    }

    @Override
    public void loadContents(CompoundTag additionalTag) {
        super.loadContents(additionalTag);
        cache = DynamoProcessCache.readFromNbt(additionalTag, energy, this).orElse(null);
    }

    public float getAnimationLerp(float tick) {
        return isWorking ? Mth.lerp((1.0F - Mth.cos((tick + tickCounter) * 0.25F)) / 2F, 4.0F, 9.0F) : 4.0F;
    }
    
    @Override
    protected void applyEfficiency(int level) {}
    
    @Override
    public void tickClient(Level lvl, BlockPos pos, BlockState st) {
        if(isWorking) tickCounter++;
        else tickCounter = 0;
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        if(hasAnyEnergy(energy)) outputEnergyToDir(lvl, pos, getBlockState().getValue(BlockStateProperties.FACING), energy);

        //TODO: Create parent DynamoBlockEntity class and move this to there.
        Recipe<?> recipe = RecipeCache.getCachedRecipe(this);
        if(cache != null) {
            if(!cache.isDone()) {
                cache.process();
                progress = cache.getDuration();
            } else if (cache.isDone()) {
                cache = null;
                progress = 0;
                if(recipe == null){
                    isWorking = false;
                    sendUpdate();
                }
            }
        }else{
            if(recipe != null) {
                FluidStack drained = tank.drainForced(100, IFluidHandler.FluidAction.SIMULATE);
                if(drained.getAmount() == 100) {
                    tank.drainForced(100, IFluidHandler.FluidAction.EXECUTE);
                    cache = new DynamoProcessCache(maxProgress, energy, energyPerTick, this);
                }
            }
        }
    }

}
