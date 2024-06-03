package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.container.BioFurnaceMenu;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BioFurnaceEntity extends MachineBlockEntity<BioFurnaceMenu> implements IHasEnergy, IHasInventory, IHasMultiFluidTank {

    @Getter
    public final ModItemStorage inventory = new ModItemStorage(1) {
        @Override
        protected void onContentsChanged(int slot) { BioFurnaceEntity.this.sendUpdate(); }
    };
    @Getter
    public final ModEnergyStorage energy = new ModEnergyStorage(100000, 40, 5000) {
        @Override
        public void onEnergyChanged() { BioFurnaceEntity.this.sendUpdate(); }

        @Override
        public boolean canReceive() { return false; }
    };
    @Getter
    public final MultiFluidTank tanks = new MultiFluidTank(
            new ModFluidStorage(20000, fs -> fs.is(ModTags.Fluids.MEAT)) {
                @Override
                protected void onContentsChanged() {
                    BioFurnaceEntity.this.sendUpdate();
                }
            },
            new ModFluidStorage(2000) {
                @Override
                protected void onContentsChanged() {
                    BioFurnaceEntity.this.sendUpdate();
                }
            });

    public BioFurnaceEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BIOFURNACE.get(), pPos, pBlockState, BioFurnaceMenu::new);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        energy.deserializeNBT(pTag.get("Energy"));
        inventory.deserializeNBT(pTag.getCompound("Inventory"));
        tanks.readFromNBT(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", energy.serializeNBT());
        pTag.put("Inventory", inventory.serializeNBT());
        tanks.writeToNBT(pTag);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        generatePower();
        distributePower();
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
            //setChanged();
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
                    //setChanged();
                }
            }
        }
    }
    

}


