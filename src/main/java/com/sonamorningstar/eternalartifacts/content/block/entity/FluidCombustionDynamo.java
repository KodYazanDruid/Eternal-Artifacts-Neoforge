package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.base.DynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidCombustionDynamo extends AbstractDynamo<DynamoMenu> {
    public FluidCombustionDynamo(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), pos, blockState, DynamoMenu::new);
        setTank(() -> createRecipeFinderTank(16000, false, true));
        setRecipeTypeAndContainer(ModRecipes.FLUID_COMBUSTING.getType(), () -> new SimpleFluidContainer(tank.getFluid(0)));
    }
    
    @Override
    protected void executeRecipe(Recipe<?> recipe, QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
        FluidStack drained = tank.drainForced(100, IFluidHandler.FluidAction.SIMULATE);
        if(drained.getAmount() == 100) {
            tank.drainForced(100, IFluidHandler.FluidAction.EXECUTE);
            cacheGetter.apply(maxProgress, energy, energyPerTick, this);
        }
    }
}
