package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.container.MobLiquifierMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class MobLiquifierBlockEntity extends SidedTransferBlockEntity<MobLiquifierMenu>{
    public MobLiquifierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MOB_LIQUIFIER.get(), blockPos, blockState, MobLiquifierMenu::new);
    }

    public MultiFluidTank tanks = new MultiFluidTank(
            createBasicTank(8000),
            createBasicTank(8000),
            createBasicTank(8000),
            createBasicTank(8000)
    );

    public ModEnergyStorage energy = new ModEnergyStorage(50000, 2500) {
        @Override
        public void onEnergyChanged() {
            MobLiquifierBlockEntity.this.sendUpdate();
        }
        @Override
        public boolean canExtract() { return false; }
    };

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        energy.deserializeNBT(pTag.get("Energy"));
        tanks.readFromNBT(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", energy.serializeNBT());
        tanks.writeToNBT(pTag);
    }

    @Override
    public void tick(Level lvl, BlockPos pos, BlockState st) {
        List<LivingEntity> livingList = lvl.getEntitiesOfClass(LivingEntity.class, new AABB(pos.above(2)).inflate(1));
        for(LivingEntity living : livingList) {
            if(living.isDeadOrDying()) continue;
            findRecipe(ModRecipes.MOB_LIQUIFIER_TYPE.get(), living.getType());
            if(currentRecipe instanceof MobLiquifierRecipe mlr) {
                NonNullList<FluidStack> outputs = mlr.getResultFluidList();
                progress(()-> {
                    for(FluidStack stack : outputs) {
                        int inserted = tanks.fill(stack, IFluidHandler.FluidAction.SIMULATE);
                        return inserted < stack.getAmount();
                    }
                    return false;
                }, ()-> {
                    living.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 200));
                    living.hurt(lvl.damageSources().cramming(), 2);
                    for(FluidStack stack : outputs) tanks.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                }, energy);
            }
        }

    }

}
