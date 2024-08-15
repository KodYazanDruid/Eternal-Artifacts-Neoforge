package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.container.MobLiquifierMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class MobLiquifierMachineBlockEntity extends SidedTransferMachineBlockEntity<MobLiquifierMenu> {
    public MobLiquifierMachineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MOB_LIQUIFIER.get(), blockPos, blockState, MobLiquifierMenu::new);
        setEnergy(createDefaultEnergy());
        setTank(new MultiFluidTank<>(
                createBasicTank(8000),
                createBasicTank(8000),
                createBasicTank(8000),
                createBasicTank(8000)
        ));
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        //Living entities inside 3x3 above the machine.
        List<LivingEntity> livingList =
                lvl.getEntitiesOfClass(LivingEntity.class, new AABB(pos.above(2)).inflate(1))
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
        //Finding entity to hurt that fits recipe.
        for(LivingEntity living : livingList) {
            findRecipe(ModRecipes.MOB_LIQUIFIER_TYPE.get(), living.getType());
            if(currentRecipe == null) continue;
            //Found entity to hurt.
            if(((MobLiquifierRecipe) currentRecipe).getEntity().test(living.getType())) {
                entityToHurt = living;
                break;
            }
        }
        //If recipe is correct and entity is present
        if(currentRecipe instanceof MobLiquifierRecipe mlr && entityToHurt != null) {
            NonNullList<FluidStack> outputs = mlr.getResultFluidList();
            LivingEntity finalEntityToHurt = entityToHurt;
            progress(()-> {
                //if(tanks.getEmptyTankCount() < outputs.size()) return true;
                for(FluidStack stack : outputs) {
                    int inserted = tank.fill(stack, IFluidHandler.FluidAction.SIMULATE);
                    if(inserted < stack.getAmount()) return true;
                }
                return false;
            }, ()-> {
                finalEntityToHurt.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 200));
                finalEntityToHurt.hurt(lvl.damageSources().cramming(), 1);
                for(FluidStack stack : outputs) tank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
            }, energy);
        }

    }

}
