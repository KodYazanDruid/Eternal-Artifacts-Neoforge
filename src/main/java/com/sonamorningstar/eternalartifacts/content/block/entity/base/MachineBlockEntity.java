package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.google.common.util.concurrent.Runnables;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
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
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Setter
public abstract class MachineBlockEntity<T extends AbstractMachineMenu> extends ModBlockEntity implements MenuProvider, ITickableServer {
    @Nullable
    private final QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> menuConstructor;

    @Setter(value = AccessLevel.NONE)
    public ModItemStorage inventory = null;
    private Supplier<? extends ModItemStorage> inventorySetter = null;
    @Setter(value = AccessLevel.NONE)
    public AbstractFluidTank tank = null;
    private Supplier<? extends AbstractFluidTank> tankSetter = null;
    @Setter(value = AccessLevel.NONE)
    public ModEnergyStorage energy = null;
    private Supplier<? extends ModEnergyStorage> energySetter = null;
    public int itemTransferRate = 1;
    public int fluidTransferRate = 1000;
    public int energyTransferRate = 1000;
    
    protected final ContainerData data;
    protected int progress;
    protected int maxProgress = 100;
    protected final int defaultMaxProgress = 100;
    protected int energyPerTick = 40;

    public final List<Integer> outputSlots = new ArrayList<>();
    protected RecipeType<? extends Recipe<? extends Container>> recipeType;
    protected Supplier<? extends Container> recipeContainer;

    @Getter
    protected Map<Integer, SidedTransferMachineBlockEntity.RedstoneType> redstoneConfigs = new HashMap<>(1);

    /* CONSTRUCTOR */
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
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                }
            }
            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    protected boolean shouldSerializeEnergy() {return true;}
    protected boolean shouldSerializeInventory() {return true;}
    protected boolean shouldSerializeTank() {return true;}
    
    public <ENERGY extends ModEnergyStorage> void setEnergy(Supplier<ENERGY> energySetter) {
        this.energySetter = energySetter;
        this.energy = energySetter.get();
    }
    
    public <ITEM extends ModItemStorage> void setInventory(Supplier<ITEM> inventorySetter) {
        this.inventorySetter = inventorySetter;
        this.inventory = inventorySetter.get();
    }
    
    public <TANK extends AbstractFluidTank> void setTank(Supplier<TANK> tankSetter) {
        this.tankSetter = tankSetter;
        this.tank = tankSetter.get();
    }
    
    @Override
    public void onEnchanted() {
        if (energySetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Energy", energy.serializeNBT());
            this.energy = energySetter.get();
            energy.deserializeNBT(oldData.get("Energy"));
        }
        if (inventorySetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Inventory", inventory.serializeNBT());
            this.inventory = inventorySetter.get();
            inventory.deserializeNBT(oldData.getCompound("Inventory"));
        }
        if (tankSetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Fluid", tank.serializeNBT());
            this.tank = tankSetter.get();
            tank.deserializeNBT(oldData.getCompound("Fluid"));
        }
        
        int effLevel = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
        int reduction = 10;
        double reductionFactor = (100 - reduction) / 100.0;
        int newMaxProgress = (int) Math.max(1, Math.round(defaultMaxProgress * Math.pow(reductionFactor, effLevel)));
        setMaxProgress(newMaxProgress);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
    
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
    @SuppressWarnings("ConstantConditions")
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
        if(energy != null && tag.contains("Energy") && shouldSerializeEnergy()) energy.deserializeNBT(tag.get("Energy"));
        if(inventory != null && shouldSerializeInventory()) inventory.deserializeNBT(tag.getCompound("Inventory"));
        if(tank != null && shouldSerializeTank()) tank.deserializeNBT(tag.getCompound("Fluid"));
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
        if (menuConstructor == null) return null;
        else return menuConstructor.apply(pContainerId, pPlayerInventory, this, data);
    }
    public boolean canConstructMenu() {
        return menuConstructor != null;
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
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        RecipeCache.clearRecipes(this);
    }
    
    protected void setRecipeTypeAndContainer(RecipeType<? extends Recipe<? extends Container>> type, Supplier<? extends Container> container) {
        this.recipeType = type;
        this.recipeContainer = container;
    }
    
    @Nullable
    protected Recipe<? extends Container> getCachedRecipe() {
        return RecipeCache.getCachedRecipe(this);
    }
    
    @Override
    protected void findRecipe() {
        findRecipeFor(recipeType, recipeContainer);
    }
    
    protected void findRecipeFor(RecipeType<? extends Recipe<? extends Container>> type, Supplier<? extends Container> container) {
        findRecipeFor(type, container, -1, false);
    }
    
    @SuppressWarnings("unchecked")
    protected void findRecipeFor(RecipeType<? extends Recipe<? extends Container>> type, Supplier<? extends Container> container, int index, boolean allowDuplicate) {
        if (level != null && type != null && container != null) {
            boolean indexed = index >= 0;
            Recipe<? extends Container> cachedRecipe = indexed ? RecipeCache.getCachedRecipe(this, index) : getCachedRecipe();
            if (cachedRecipe != null) {
                if (cachedRecipe.getType() != type || !((Recipe<Container>) cachedRecipe).matches(container.get(), level)) {
                    if (indexed){
                        RecipeCache.removeRecipe(this, index);
                        RecipeCache.findRecipeFor(this, type, container.get(), level, allowDuplicate, index);
                    }
                    else {
                        RecipeCache.removeRecipe(this, cachedRecipe);
                        RecipeCache.findRecipeFor(this, type, container.get(), level, allowDuplicate);
                    }
                }
            } else {
                if (indexed) RecipeCache.findRecipeFor(this, type, container.get(), level, allowDuplicate, index);
                else RecipeCache.findRecipeFor(this, type, container.get(), level, allowDuplicate);
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
            int lvl = getEnchantmentLevel(Enchantments.UNBREAKING);
            if (lvl > 0) {
                float chance = 1.0f / (lvl + 1);
                if (level.random.nextFloat() > chance) {
                    energy.extractEnergyForced(energyPerTick, false);
                }
            } else energy.extractEnergyForced(energyPerTick, false);
            running.run();
            if (progress++ >= maxProgress) {
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
        if (amount <= 0) return true;
        return hasAnyEnergy(energy) || energy.extractEnergyForced(amount, true) == amount;
    }

    protected boolean hasAnyEnergy(ModEnergyStorage energy) {
        return energy.getEnergyStored() > 0;
    }

    //region Transfer methods.
    protected void insertItemFromDir(Level lvl, BlockPos pos, Direction dir, IItemHandlerModifiable inventory) {
        IItemHandler sourceInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());
        if(sourceInv != null) {
            for(int i = 0; i < sourceInv.getSlots(); i++) {
                if(sourceInv.getStackInSlot(i).isEmpty()) continue;
                ItemStack extracted = sourceInv.extractItem(i, itemTransferRate, true);
                if (!extracted.isEmpty()) {
                    ItemStack remained = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
                    if (remained.getCount() != extracted.getCount()) {
                        int count = extracted.getCount() - remained.getCount();
                        sourceInv.extractItem(i, count, false);
                        ItemHandlerHelper.insertItemStacked(inventory, extracted.copyWithCount(count), false);
                        break;
                    }
                }
            }
        }
    }

    protected void outputItemToDir(Level lvl, BlockPos pos, Direction dir, IItemHandlerModifiable inventory) {
        IItemHandler targetInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());
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

    protected void inputFluidFromDir(Level lvl, BlockPos pos, Direction dir, AbstractFluidTank tank) {
        transferFluidToTank(lvl, pos, dir, tank, true);
    }

    protected void outputFluidToDir(Level lvl, BlockPos pos, Direction dir, AbstractFluidTank tank) {
        transferFluidToTank(lvl, pos, dir, tank, false);
    }

    protected void transferFluidToTank(Level lvl, BlockPos pos, Direction dir, AbstractFluidTank tank, boolean isReverse) {
        IFluidHandler targetTank = lvl.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(dir), dir.getOpposite());
        if(targetTank != null) {
            if(isReverse) FluidUtil.tryFluidTransfer(tank, targetTank, fluidTransferRate, true);
            else FluidUtil.tryFluidTransfer(targetTank, tank, fluidTransferRate, true);
        }
    }

    protected void inputEnergyToDir(Level lvl, BlockPos pos, Direction dir, ModEnergyStorage energy) {
        IEnergyStorage target = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), dir.getOpposite());
        if(target != null && target.canExtract()) {
            int extracted = target.extractEnergy(energyTransferRate, true);
            if(extracted > 0) {
                int received = energy.receiveEnergy(extracted, true);
                if(received > 0) {
                    energy.receiveEnergy(received, false);
                    target.extractEnergy(received, false);
                }
            }
        }
    }

    protected void outputEnergyToDir(Level lvl, BlockPos pos, Direction dir, ModEnergyStorage energy) {
        IEnergyStorage target = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), dir.getOpposite());
        if(target != null && target.canReceive()) {
            int extracted = energy.extractEnergyForced(energyTransferRate, true);
            if (extracted > 0) {
                int received = target.receiveEnergy(extracted, true);
                if (received > 0) {
                    target.receiveEnergy(received, false);
                    energy.extractEnergy(received, false);
                }
            }
        }
    }
    //endregion

    public void saveContents(CompoundTag additionalTag) {
        additionalTag.putInt("Progress", progress);
        additionalTag.putInt("MaxProgress", maxProgress);
        additionalTag.putInt("EnergyPerTick", energyPerTick);
    }

    public void loadContents(CompoundTag additionalTag) {
        progress = additionalTag.getInt("Progress");
        maxProgress = additionalTag.getInt("MaxProgress");
        energyPerTick = additionalTag.getInt("EnergyPerTick");
    }
}
