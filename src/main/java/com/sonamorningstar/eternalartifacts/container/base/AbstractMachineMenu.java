package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public abstract class AbstractMachineMenu extends AbstractModContainerMenu {
    @Getter
    @Nullable
    protected final IItemHandler beInventory;
    @Getter
    @Nullable
    protected final IEnergyStorage beEnergy;
    @Getter
    @Nullable
    protected final IFluidHandler beTank;
    protected final Level level;
    @Getter
    protected final BlockEntity blockEntity;
    protected final List<Integer> outputSlots = new ArrayList<>();
    public final ContainerData data;
    public final MenuType<?> menuType;

    public AbstractMachineMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(menuType, id, inv);
        this.menuType = menuType;
        this.level = inv.player.level();
        this.blockEntity = entity;
        this.beInventory = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        this.beEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        this.beTank = level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        this.data = data;
        addPlayerInventoryAndHotbar(inv, 8, 66);
        addDataSlots(data);
        if (blockEntity instanceof SidedTransferMachine<?> sided) outputSlots.addAll(sided.outputSlots);
        if (blockEntity instanceof ModBlockEntity mbe) setMachineConfigs(mbe.getConfiguration());
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, blockEntity.getBlockState().getBlock());
    }

    public int getEnergyProgress() {
        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, blockEntity.getBlockPos(), null);
        if(energyStorage != null){
            int stored = energyStorage.getEnergyStored();
            int max = energyStorage.getMaxEnergyStored();
            int barHeight = 50;
            return max != 0 && stored != 0 ? stored * barHeight / max : 0;
        } else return 0;
    }

    public int getFluidProgress(int slot, int height) {
        IFluidHandler tank = level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), null);
        if(tank != null){
            int amount = tank.getFluidInTank(slot).getAmount();
            int max = tank.getTankCapacity(slot);
            return max != 0 && amount != 0 ? amount * height / max : 0;
        } else return 0;
    }

    public int getScaledProgress(int size) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);

        return maxProgress != 0 && progress != 0 ? progress * size / maxProgress : 0;
    }

    public boolean isWorking() {
        return data.get(0) > 0;
    }

    public static OptionalInt openContainer(Player player, BlockPos pos) {
        final BlockEntity blockEntity = player.level().getBlockEntity(pos);
        if (!(blockEntity instanceof MenuProvider prov)) return OptionalInt.empty();
        if (blockEntity instanceof ModBlockEntity mbe && mbe.getConfiguration() == null) return OptionalInt.empty();
        return player.openMenu(prov, pos);
    }
}
