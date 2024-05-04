package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.container.BioFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class BioFurnaceEntity extends MachineBlockEntity implements MenuProvider, ITickable {

    public final ModItemStorage inventory = new ModItemStorage(1) {
        @Override
        protected void onContentsChanged(int slot) { BioFurnaceEntity.this.sendUpdate(); }
    };
    public final ModEnergyStorage energy = new ModEnergyStorage(100000, 40, 5000) {
        @Override
        public void onEnergyChanged() { BioFurnaceEntity.this.sendUpdate(); }

        @Override
        public boolean canReceive() { return false; }
    };

    public BioFurnaceEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BIOFURNACE.get(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() { return Component.translatable(ModBlocks.BIOFURNACE.get().getDescriptionId()); }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BioFurnaceMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        energy.deserializeNBT(pTag.get("Energy"));
        inventory.deserializeNBT(pTag.getCompound("Inventory"));
    }

    @Override
    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            container.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(level, this.worldPosition, container);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", energy.serializeNBT());
        pTag.put("Inventory", inventory.serializeNBT());
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
            setChanged();
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
                    setChanged();
                }
            }
        }
    }

}


