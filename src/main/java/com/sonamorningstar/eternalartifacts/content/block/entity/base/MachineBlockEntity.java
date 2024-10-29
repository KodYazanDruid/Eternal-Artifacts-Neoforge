package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.google.common.util.concurrent.Runnables;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public abstract class MachineBlockEntity<T extends AbstractMachineMenu> extends ModBlockEntity implements MenuProvider, ITickableServer {
    QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> menuConstructor;

    @Setter
    public ModItemStorage inventory = null;
    @Setter
    public AbstractFluidTank tank = null;
    @Setter
    public ModEnergyStorage energy = null;

    @Getter
    @Setter
    protected Map<Integer, SidedTransferMachineBlockEntity.RedstoneType> redstoneConfigs = new HashMap<>(1);
    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF) {
        super(type, pos, blockState);
        this.menuConstructor = quadF;
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> throw new IllegalStateException("Unexpected value: " + index);
                };
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 ->  progress = value;
                    case 1 -> maxProgress = value;
                }
            }
            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    protected ContainerData data;
    protected int progress;
    @Setter
    protected int maxProgress = 100;
    @Setter
    protected int energyPerTick = 40;

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("progress", progress);
        if(energy != null) tag.put("Energy", energy.serializeNBT());
        if(inventory != null) tag.put("Inventory", inventory.serializeNBT());
        if(tank != null) tag.put("Fluid", tank.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
        if(energy != null) energy.deserializeNBT(tag.get("Energy"));
        if(inventory != null) inventory.deserializeNBT(tag.getCompound("Inventory"));
        if(tank != null) tank.deserializeNBT(tag.getCompound("Fluid"));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        findRecipe();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return menuConstructor.apply(pContainerId, pPlayerInventory, this, data);
    }

    protected void initializeDefaultEnergyAndTank() {
        this.energy = createDefaultEnergy();
        this.tank = createDefaultTank();
    }

    protected void fillTankFromSlot(ModItemStorage inventory, AbstractFluidTank tank, int fluidSlot) {
        ItemStack stack = inventory.getStackInSlot(fluidSlot);
        if(!stack.isEmpty() && tank.getFluidAmount(0) < tank.getCapacity(0)) {
            IFluidHandlerItem itemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if(itemHandler != null && tank.isFluidValid(0, itemHandler.getFluidInTank(0)) &&
                    (tank.getFluid(0).getFluid() == itemHandler.getFluidInTank(0).getFluid() || tank.getFluid(0).isEmpty())) {
                int amountToDrain = tank.getCapacity(0) - tank.getFluidAmount(0);
                int amount = itemHandler.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE).getAmount();
                if (amount > 0) {
                    tank.fill(itemHandler.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                }
                if (amount <= amountToDrain) {
                    inventory.setStackInSlot(fluidSlot, itemHandler.getContainer());
                }
            }
        }
    }

    protected void progress(BooleanSupplier test, Runnable result, ModEnergyStorage energy) {
        progress(test, Runnables.doNothing(), result, energy);
    }

    protected void progress(BooleanSupplier test, Runnable running, Runnable result, ModEnergyStorage energy) {
        if(!hasEnergy(energyPerTick, energy) || level == null) return;
        SidedTransferMachineBlockEntity.RedstoneType type = redstoneConfigs.get(0);
        if(redstoneChecks(type, level)){
            if (test.getAsBoolean()) {
                progress = 0;
                return;
            }
            energy.extractEnergyForced(energyPerTick, false);
            progress++;
            running.run();
            if (progress >= maxProgress) {
                result.run();
                progress = 0;
            }
        }
    }

    private boolean redstoneChecks(SidedTransferMachineBlockEntity.RedstoneType type, Level level) {
        return (type == SidedTransferMachineBlockEntity.RedstoneType.HIGH && level.getDirectSignalTo(getBlockPos()) > 0) ||
                (type == SidedTransferMachineBlockEntity.RedstoneType.LOW && level.getDirectSignalTo(getBlockPos()) == 0) ||
                type == SidedTransferMachineBlockEntity.RedstoneType.IGNORED || type == null;
    }

    protected boolean hasEnergy(int amount, ModEnergyStorage energy) {
        return energy.extractEnergyForced(amount, true) >= amount;
    }

    protected void insertItemFromDir(Level lvl, BlockPos pos, Direction dir, IItemHandlerModifiable inventory) {
        BlockEntity targetBe = lvl.getBlockEntity(pos.relative(dir));
        if(targetBe != null) {
            IItemHandler sourceInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, targetBe.getBlockPos(), targetBe.getBlockState(), targetBe, dir.getOpposite());
            if(sourceInv != null) {
                for(int i = 0; i < sourceInv.getSlots(); i++) {
                    if(sourceInv.getStackInSlot(i).isEmpty()) continue;
                    ItemStack extracted = sourceInv.extractItem(i, 1, true);
                    if (!extracted.isEmpty()) {
                        ItemStack remained = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
                        if (remained.getCount() != extracted.getCount()) {
                            int count = extracted.getCount() - remained.getCount();
                            sourceInv.extractItem(i, count, false);
                            ItemHandlerHelper.insertItemStacked(inventory, extracted.copyWithCount(count), false);
                        }
                    }
                }
            }
        }
    }

    protected void outputItemToDir(Level lvl, BlockPos pos, Direction dir, IItemHandlerModifiable inventory, Integer... outputSlots) {
        BlockEntity targetBe = lvl.getBlockEntity(pos.relative(dir));
        if(targetBe != null) {
            IItemHandler targetInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, targetBe.getBlockPos(), targetBe.getBlockState(), targetBe, dir.getOpposite());
            if(targetInv != null) {
                for(int output : outputSlots) {
                    try {
                        ItemStack stack = inventory.getStackInSlot(output);
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(targetInv, stack, true);
                        if(remainder.isEmpty()) {
                            ItemHandlerHelper.insertItemStacked(targetInv, stack.copyWithCount(stack.getCount()), false);
                            inventory.extractItem(output, stack.getCount(), false);
                        }else {
                            int transferred = stack.getCount() - remainder.getCount();
                            ItemHandlerHelper.insertItemStacked(targetInv, stack.copyWithCount(transferred), false);
                            stack.shrink(transferred);
                        }
                    }catch (IndexOutOfBoundsException e) {
                        EternalArtifacts.LOGGER.error("Output slot {} is out of bounds in {} sized inventory", output, targetInv.getSlots());
                    }
                }
            }
        }
    }

    protected void inputFluidFromDir(Level lvl, BlockPos pos, Direction dir, AbstractFluidTank tank) {
        transferFluidToBE(lvl, pos, dir, tank, true);
    }

    protected void outputFluidToDir(Level lvl, BlockPos pos, Direction dir, AbstractFluidTank tank) {
        transferFluidToBE(lvl, pos, dir, tank, false);
    }

    protected void transferFluidToBE(Level lvl, BlockPos pos, Direction dir, AbstractFluidTank tank, boolean isReverse) {
        BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
        if(be != null) {
            IFluidHandler targetTank = lvl.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
            if(targetTank != null && tank != null) {
                if(isReverse) FluidUtil.tryFluidTransfer(tank, targetTank, 1000, true);
                else FluidUtil.tryFluidTransfer(targetTank, tank, 1000, true);
            }
        }
    }

    protected void outputEnergyToDir(Level lvl, BlockPos pos, Direction dir, IEnergyStorage energy) {
        BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
        if(be != null) {
            IEnergyStorage target = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
            if(target != null && target.canReceive()) {
                int inserted = target.receiveEnergy(energy.getEnergyStored(), true);
                if(inserted > 0) {
                    target.receiveEnergy(inserted, false);
                    energy.extractEnergy(inserted, false);
                }
            }
        }
    }

}
