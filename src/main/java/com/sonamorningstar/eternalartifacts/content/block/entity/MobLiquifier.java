package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.base.EntityFilterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleEntityContainer;
import com.sonamorningstar.eternalartifacts.core.*;
import lombok.Getter;
import lombok.Setter;
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
import java.util.Objects;
import java.util.function.Predicate;

@Getter
@Setter
public class MobLiquifier extends GenericMachine implements WorkingAreaProvider, EntityFilterable {
    private EntityPredicateEntry entityFilter = new EntityPredicateEntry();
    Predicate<EntityPredicateEntry.EntityPredicate> filterValidator = e ->
        !Objects.equals(e, EntityPredicateEntry.EntityPredicate.PLAYER) &&
        !Objects.equals(e, EntityPredicateEntry.EntityPredicate.BABY) &&
        !Objects.equals(e, EntityPredicateEntry.EntityPredicate.DEAD) &&
        !Objects.equals(e, EntityPredicateEntry.EntityPredicate.ALIVE) &&
        !Objects.equals(e, EntityPredicateEntry.EntityPredicate.ADULT);
    
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
        screenInfo.setArrowPos(35, 40);
        screenInfo.setTankPosition(68, 20, 0);
        screenInfo.setTankPosition(88, 20, 1);
        screenInfo.setTankPosition(108, 20, 2);
        screenInfo.setTankPosition(128, 20, 3);
        screenInfo.setShouldDrawInventoryTitle(false);
    }
    List<LivingEntity> livingList = new ArrayList<>();

    @Override
    public AABB getWorkingArea(BlockPos anchor) {
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        return new AABB(anchor.relative(facing.getOpposite(), 5)).inflate(4, 1, 4).move(0D, 1D, 0D);
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("EntityFilter", entityFilter.serializeNBT());
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("EntityFilter")) {
            entityFilter.deserializeNBT(tag.getCompound("EntityFilter"));
        }
    }
    
    @Override
    public void saveContents(CompoundTag additionalTag) {
        super.saveContents(additionalTag);
        additionalTag.put("EntityFilter", entityFilter.serializeNBT());
    }
    
    @Override
    public void loadContents(CompoundTag additionalTag) {
        super.loadContents(additionalTag);
        if (additionalTag.contains("EntityFilter")) {
            entityFilter.deserializeNBT(additionalTag.getCompound("EntityFilter"));
        }
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
        
        livingList = lvl.getEntitiesOfClass(LivingEntity.class, getWorkingArea(pos)).stream().filter(living -> {
                    EntityType<?> type = living.getType();
                    return !(EntityType.PLAYER == type || living.isDeadOrDying() || living.isBaby()) && entityFilter.matches(living);
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
