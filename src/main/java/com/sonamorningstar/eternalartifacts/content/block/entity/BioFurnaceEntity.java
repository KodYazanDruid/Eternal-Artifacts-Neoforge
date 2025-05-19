package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.container.BioFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class BioFurnaceEntity extends Machine<BioFurnaceMenu> {

    public BioFurnaceEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BIOFURNACE.get(), pPos, pBlockState, BioFurnaceMenu::new);
        setInventory(() -> createBasicInventory(1, List.of(), (slot, stack) -> true));
        setEnergy(this::createDefaultEnergy);
    }

    public void tickServer(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tickServer(pLevel, pPos, pState);
        generatePower();
        distributePower();
    }
    
    @Override
    public boolean isGenerator() {
        return true;
    }
    
    private void generatePower() {
        ItemStack fuel = inventory.getStackInSlot(0);
        if(energy.getEnergyStored() < energy.getMaxEnergyStored()) {
            if(progress <= 0) {
                if(fuel.isEmpty() || !fuel.is(ModItems.ORANGE.get())) return;
                inventory.extractItem(0, 1, false);
                progress = maxProgress;
            }else{
                progress--;
                energy.receiveEnergyForced(40, false);
            }
        }
    }

    private void distributePower() {
        if(energy.getEnergyStored() <=0 ) return;
        for(Direction direction : Direction.values()){
            BlockEntity be = level.getBlockEntity(getBlockPos().relative(direction));
            if(be != null) {
                IEnergyStorage es = level.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), direction.getOpposite());
                if(es != null && es.canReceive()) {
                    int received = es.receiveEnergy(Math.min(energy.getEnergyStored() , 5000), false);
                    energy.extractEnergy(received, false);
                }
            }
        }
    }
    

}


