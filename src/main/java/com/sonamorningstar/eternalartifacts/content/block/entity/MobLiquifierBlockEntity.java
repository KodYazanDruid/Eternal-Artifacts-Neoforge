package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.IAreaRenderer;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleEntityContainer;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MobLiquifierBlockEntity extends GenericMachineBlockEntity implements IAreaRenderer {
    public MobLiquifierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModMachines.MOB_LIQUIFIER, blockPos, blockState);
        setEnergy(createDefaultEnergy());
        setTank(new MultiFluidTank<>(
                createBasicTank(8000, true, false),
                createBasicTank(8000, true, false),
                createBasicTank(8000, true, false),
                createBasicTank(8000, true, false)
        ));
        screenInfo.setOverrideArrowPos(true);
        screenInfo.setArrowX(110);
        screenInfo.setArrowY(35);
        screenInfo.setTankPosition(24, 20, 0);
        screenInfo.setTankPosition(44, 20, 1);
        screenInfo.setTankPosition(64, 20, 2);
        screenInfo.setTankPosition(84, 20, 3);
        screenInfo.setShouldDrawInventoryTitle(false);
        screenInfo.addButton(MODID, "textures/gui/sprites/blank_red.png", 110, 8, 16, 16, (b, i) -> {
            shouldRenderArea = !shouldRenderArea;
            sendUpdate();
        });
    }
    List<LivingEntity> livingList = new ArrayList<>();
    RecipeCache<MobLiquifierRecipe, SimpleEntityContainer> cache = new RecipeCache<>();
    private boolean shouldRenderArea = false;

    @Override
    protected void findRecipe() {
        EntityType<?>[] typeArray = livingList.stream().map(Entity::getType).toArray(EntityType[]::new);
        cache.findRecipe(ModRecipes.MOB_LIQUIFYING.getType(), new SimpleEntityContainer(typeArray), level);
    }

    @Override
    public boolean shouldRender() {
        return shouldRenderArea;
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

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        //Living entities inside 3x3 above the machine.
        livingList = lvl.getEntitiesOfClass(LivingEntity.class, new AABB(pos.above(2)).inflate(1))
                        .stream().filter(living -> {
                    EntityType<?> type = living.getType();
                    return !(EntityType.PLAYER == type || living.isDeadOrDying() || living.isBaby());
                }).toList();

        if(livingList.isEmpty()) {
            progress = 0;
            return;
        }

        //Entity to hurt
        LivingEntity entityToHurt = null;
        MobLiquifierRecipe recipe = cache.getRecipe();
        //Finding entity to hurt that fits recipe.
        for(LivingEntity living : livingList) {
            findRecipe();
            if(recipe == null) continue;
            //Found entity to hurt.
            if(recipe.getEntity().test(living.getType())) {
                entityToHurt = living;
                break;
            }
        }
        //If recipe is correct and entity is present
        if(recipe != null && entityToHurt != null) {
            NonNullList<FluidStack> outputs = recipe.getResultFluidList();
            LivingEntity finalEntityToHurt = entityToHurt;

            ProcessCondition condition = new ProcessCondition().initOutputTank(tank);
            for (FluidStack stack : outputs) condition.queueFluidStack(stack);
            condition.commitQueuedFluidStacks();
            progress(condition::getResult, ()-> {
                finalEntityToHurt.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 200));
                finalEntityToHurt.hurt(lvl.damageSources().cramming(), 1);
                for(FluidStack stack : outputs) tank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
            }, energy);
        }

    }
}
