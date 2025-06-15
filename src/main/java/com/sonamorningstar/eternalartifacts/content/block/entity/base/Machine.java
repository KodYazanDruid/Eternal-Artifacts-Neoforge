package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.google.common.util.concurrent.Runnables;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
public abstract class Machine<T extends AbstractMachineMenu> extends ModBlockEntity implements MenuProvider, ITickableServer {
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
    protected int progressStep = 1;
    protected int defaultMaxProgress = 100;
    protected int maxProgress;
    protected int defaultEnergyPerTick = 40;
    @Getter
    protected int energyPerTick;

    public final List<Integer> outputSlots = new ArrayList<>();
    protected RecipeType<? extends Recipe<? extends Container>> recipeType;
    protected Supplier<? extends Container> recipeContainer;
    protected Recipe<? extends Container> previousRecipe = null;
    @Setter(value = AccessLevel.NONE)
    protected ProcessCondition processCondition;
    @Getter
    protected Map<Integer, SidedTransferMachine.RedstoneType> redstoneConfigs = new HashMap<>(1);
    
    /*@Getter
    protected AABB area = null;*/

    /* CONSTRUCTOR */
    public Machine(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF) {
        super(type, pos, blockState);
        this.menuConstructor = quadF;
        this.maxProgress = defaultMaxProgress;
        this.energyPerTick = defaultEnergyPerTick;
        data = createContainerData();
    }
    
    protected ContainerData createContainerData() {
        return new ContainerData() {
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
    protected boolean shouldSyncOnUpdate() {return true;}

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
    
    protected void setMaxProgress(int maxProgress) {
        this.defaultMaxProgress = maxProgress;
        this.maxProgress = maxProgress;
    }
    
    @Override
    public void onEnchanted(Enchantment enchantment, int level) {
        if (enchantment == ModEnchantments.VOLUME.get()){
            resetEnergy();
            resetInventory();
            resetTank();
        }
        
        if (enchantment == Enchantments.BLOCK_EFFICIENCY) {
			applyEfficiency(level);
        }
        
       if (enchantment == ModEnchantments.CELERITY.get()) {
           setProgressStep(level + 1);
           energyPerTick = defaultEnergyPerTick * (level + 1);
       }
    }
    
    public void resetEnergy() {
        if (energySetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Energy", energy.serializeNBT());
            this.energy = energySetter.get();
            energy.deserializeNBT(oldData.get("Energy"));
            this.invalidateCapabilities();
        }
    }
    public void resetInventory() {
        if (inventorySetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Inventory", inventory.serializeNBT());
            this.inventory = inventorySetter.get();
            inventory.deserializeNBT(oldData.getCompound("Inventory"));
            this.invalidateCapabilities();
        }
    }
    public void resetTank() {
        if (tankSetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Fluid", tank.serializeNBT());
            this.tank = tankSetter.get();
            tank.deserializeNBT(oldData.getCompound("Fluid"));
            this.invalidateCapabilities();
        }
    }
    
    protected void applyEfficiency(int level) {
        int reduction = 10;
        double reductionFactor = (100 - reduction) / 100.0;
		maxProgress = (int) Math.max(1, Math.round(defaultMaxProgress * Math.pow(reductionFactor, level)));
    }
    
    public boolean isGenerator() {
        return false;
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        Recipe<Container> recipe = (Recipe<Container>) RecipeCache.getCachedRecipe(this);
        if (recipeType != null && recipe == null) {
            progress = 0;
            return;
        }
        if (previousRecipe != null && previousRecipe != recipe) {
            progress = 0;
        }
        previousRecipe = recipe;
    }
    
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        processCondition = condition;
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
        setProcessCondition(new ProcessCondition(this), getCachedRecipe());
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
        FakePlayerHelper.removeFakePlayer(this);
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
    
    /*protected void prepareFakePlayer(FakePlayer fakePlayer) {
    
    }*/
    
    protected void progress(Runnable result) {
        if (processCondition != null && energy != null) {
            progress(processCondition::getResult, result, energy);
        }
    }
    
    protected void progress(BooleanSupplier test, Runnable result, ModEnergyStorage energy) {
        progress(test, Runnables.doNothing(), result, energy);
    }

    protected void progress(BooleanSupplier test, Runnable running, Runnable result, ModEnergyStorage energy) {
        if (!canWork(energy) || level == null) return;
        SidedTransferMachine.RedstoneType type = redstoneConfigs.get(0);
        if (redstoneChecks(type, level)) {
            if (test.getAsBoolean()) {
                progress = 0;
                return;
            }
            spendEnergy(energy);
            running.run();
            progress = Math.min(maxProgress, progress + progressStep);
            if (progress >= maxProgress) {
                result.run();
                progress = 0;
            }
        }
    }

    protected boolean redstoneChecks(SidedTransferMachine.RedstoneType type, Level level) {
        return (type == SidedTransferMachine.RedstoneType.HIGH && level.getDirectSignalTo(getBlockPos()) > 0) ||
                (type == SidedTransferMachine.RedstoneType.LOW && level.getDirectSignalTo(getBlockPos()) == 0) ||
                type == SidedTransferMachine.RedstoneType.IGNORED || type == null;
    }

    protected boolean canWork(ModEnergyStorage energy) {
        if (energyPerTick <= 0) return true;
        return energy.extractEnergyForced(energyPerTick, true) == energyPerTick;
    }

    protected boolean hasAnyEnergy(ModEnergyStorage energy) {
        return energy.getEnergyStored() > 0;
    }
    
    public int spendEnergy(ModEnergyStorage energy) {
        if (energy == null || level == null) return 0;
        int lvl = getEnchantmentLevel(Enchantments.UNBREAKING);
        if (lvl > 0) {
            float chance = 1.0f / (lvl + 1);
            if (level.random.nextFloat() > chance) {
                return energy.extractEnergyForced(energyPerTick, false);
            } else return 0;
        } else return energy.extractEnergyForced(energyPerTick, false);
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
        if (additionalTag.contains("Progress")) progress = additionalTag.getInt("Progress");
        if (additionalTag.contains("MaxProgress")) maxProgress = additionalTag.getInt("MaxProgress");
        if (additionalTag.contains("EnergyPerTick")) energyPerTick = additionalTag.getInt("EnergyPerTick");
    }
}
