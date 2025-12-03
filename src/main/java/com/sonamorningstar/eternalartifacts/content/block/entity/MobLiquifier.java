package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AreaRenderer;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleEntityContainer;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MobLiquifier extends GenericMachine implements AreaRenderer {

    public MobLiquifier(BlockPos blockPos, BlockState blockState) {
        super(ModMachines.MOB_LIQUIFIER, blockPos, blockState);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> new MultiFluidTank<>(
                createBasicTank(8000, true, false),
                createBasicTank(8000, true, false),
                createBasicTank(8000, true, false),
                createBasicTank(8000, true, false)
        ));
        setRecipeTypeAndContainer(ModRecipes.MOB_LIQUIFYING.getType(), () -> new SimpleEntityContainer(livingList));
        screenInfo.setOverrideArrowPos(true);
        screenInfo.setArrowPos(110, 35);
        screenInfo.setTankPosition(24, 20, 0);
        screenInfo.setTankPosition(44, 20, 1);
        screenInfo.setTankPosition(64, 20, 2);
        screenInfo.setTankPosition(84, 20, 3);
        screenInfo.setShouldDrawInventoryTitle(false);
        screenInfo.addButton(MODID, "textures/gui/sprites/blank_red.png", 110, 8, 16, 16, () -> {
            shouldRenderArea = !shouldRenderArea;
            sendUpdate();
        });
    }
    List<LivingEntity> livingList = new ArrayList<>();
    private boolean shouldRenderArea = false;

    @Override
    public boolean shouldRender() {
        return shouldRenderArea;
    }

    @Override
    public AABB getWorkingArea() {
        return getWorkingArea(getBlockPos(), getBlockState());
    }

    @Override
    protected void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
        tag.putBoolean("RenderArea", shouldRenderArea);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        shouldRenderArea = tag.getBoolean("RenderArea");
    }

    private static AABB getWorkingArea(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return new AABB(pos.relative(facing.getOpposite(), 2)).inflate(1).move(0D, 1D, 0D);
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        if (recipe instanceof MobLiquifierRecipe liq) {
            condition.initOutputTank(tank);
            for (FluidStack stack : liq.getResultFluidList()) condition.queueImport(stack);
            condition.commitQueuedFluidStackImports();
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        if (!redstoneChecks(lvl)) return;
        
        livingList = lvl.getEntitiesOfClass(LivingEntity.class, getWorkingArea(pos, st))
                        .stream().filter(living -> {
                    EntityType<?> type = living.getType();
                    return !(EntityType.PLAYER == type || living.isDeadOrDying() || living.isBaby());
                }).toList();

        if(livingList.isEmpty()) {
            progress = 0;
            return;
        }

        LivingEntity entityToHurt = null;
        MobLiquifierRecipe recipe = null;
        for(LivingEntity living : livingList) {
            findRecipe();
            recipe = (MobLiquifierRecipe) RecipeCache.getCachedRecipe(this);
            if(recipe == null) continue;
            setProcessCondition(new ProcessCondition(this), recipe);
            if(recipe.getEntity().test(living.getType())) {
                entityToHurt = living;
                break;
            }
        }
        if(recipe != null && entityToHurt != null) {
            NonNullList<FluidStack> outputs = recipe.getResultFluidList();
            LivingEntity finalEntityToHurt = entityToHurt;

            progress(()-> {
                finalEntityToHurt.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 200));
                finalEntityToHurt.hurt(lvl.damageSources().cramming(), 1);
                for(FluidStack stack : outputs) tank.fillForced(stack.copy(), IFluidHandler.FluidAction.EXECUTE);
            });
        }

    }
}
