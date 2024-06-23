package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.container.FluidCombustionMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FluidCombustionDynamoBlockEntity extends MachineBlockEntity<FluidCombustionMenu> implements ITickableClient {
    public FluidCombustionDynamoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), pos, blockState, FluidCombustionMenu::new);
    }

    private int tickCounter = 0;

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

    public float getAnimationLerp(float tick) {
        return tank.isEmpty() ? 4.0F : Mth.lerp((1.0F - Mth.cos((tick + tickCounter) * 0.25F) ) / 2F, 4.0F, 9.0F);
    }

    @Override
    public void tickClient(Level lvl, BlockPos pos, BlockState st) {
        if(!tank.isEmpty()) tickCounter++;
        else tickCounter = 0;
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {

    }

}
