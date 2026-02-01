package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.google.common.util.concurrent.Runnables;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.forceload.ForceLoadManager;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.api.machine.config.*;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.api.machine.config.RedstoneConfig.Mode;

@Setter
public abstract class Machine<T extends AbstractMachineMenu> extends ModBlockEntity implements MenuProvider, TickableServer, ChunkLoader {
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
    protected final Set<ForceLoadManager.ForcedChunkPos> forcedChunks = new HashSet<>();
    private int chunkUnloadCooldown = 200;
    private int chunkUpdateCooldown = 100;
    protected FakePlayer fakePlayer = null;
    protected boolean isFakePlayerSetUp = false;

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
        if (getConfiguration().getConfigs().values().stream().map(Config::getClass)
                .toList()
                .contains(SideConfig.class))
            registerCapabilityConfigs(Capabilities.ItemHandler.BLOCK);
    }
    
    public <TANK extends AbstractFluidTank> void setTank(Supplier<TANK> tankSetter) {
        this.tankSetter = tankSetter;
        this.tank = tankSetter.get();
        if (getConfiguration().getConfigs().values().stream().map(Config::getClass)
                .toList()
                .contains(SideConfig.class))
            registerCapabilityConfigs(Capabilities.FluidHandler.BLOCK);
    }
    
    protected void setMaxProgress(int maxProgress) {
        this.defaultMaxProgress = maxProgress;
        this.maxProgress = maxProgress;
    }
    
    @Override
    public void onEnchanted(Enchantment enchantment, int level) {
        super.onEnchanted(enchantment, level);
        if (enchantment == ModEnchantments.VOLUME.get()) {
            resetBaseCapabilities();
        }
        if (enchantment == Enchantments.BLOCK_EFFICIENCY) {
            applyEfficiency(level);
        }
        if (enchantment == ModEnchantments.CELERITY.get()) {
            setProgressStep(level + 1);
            energyPerTick = defaultEnergyPerTick * (level + 1);
        }
    }
    
    public void resetBaseCapabilities() {
        resetEnergy();
        resetInventory();
        resetTank();
        this.invalidateCapabilities();
    }
    
    public void resetEnergy() {
        if (energySetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Energy", energy.serializeNBT());
            this.energy = energySetter.get();
            energy.deserializeNBT(oldData.get("Energy"));
        }
    }
    public void resetInventory() {
        if (inventorySetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Inventory", inventory.serializeNBT());
            this.inventory = inventorySetter.get();
            inventory.deserializeNBT(oldData.getCompound("Inventory"));
        }
    }
    public void resetTank() {
        if (tankSetter != null) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Fluid", tank.serializeNBT());
            this.tank = tankSetter.get();
            tank.deserializeNBT(oldData.getCompound("Fluid"));
        }
    }
    
    @Override
    public void registerConfigs() {
        super.registerConfigs();
        getConfiguration().add(new RedstoneConfig());
    }
    
    @Override
    public void registerCapabilityConfigs(BlockCapability<?,?> cap) {
        super.registerCapabilityConfigs(cap);
        if (cap == Capabilities.ItemHandler.BLOCK) getConfiguration().add(new ReverseToggleConfig("item_transfer"));
        if (cap == Capabilities.FluidHandler.BLOCK) getConfiguration().add(new ReverseToggleConfig("fluid_transfer"));
    }
    
    protected FakePlayer getFakePlayer() {
        if (fakePlayer == null) {
            this.fakePlayer = FakePlayerHelper.getFakePlayer(this, level);
        }
		return fakePlayer;
    }
    protected void setupFakePlayer(BlockState st) {
        if (fakePlayer == null || isFakePlayerSetUp) return;
        fakePlayer.setPosRaw(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
        if (st.hasProperty(BlockStateProperties.FACING)) {
            fakePlayer.setYRot(st.getValue(BlockStateProperties.FACING).toYRot());
        } else if (st.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            fakePlayer.setYRot(st.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot());
        }
        isFakePlayerSetUp = true;
    }
    protected void setupFakePlayerInventory(ItemStack mainHandItem) {
        if (fakePlayer == null) return;
        Inventory fakePlayerInventory = fakePlayer.getInventory();
        fakePlayerInventory.selected = 0;
        fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, mainHandItem);
        int size = fakePlayerInventory.items.size();
        for (int i = 1; i < size - 1; i++) {
            for (int j = 0; j < inventory.getSlots(); j++) {
                if (i == j) {
                    fakePlayerInventory.items.set(i, inventory.getStackInSlot(j).copy());
                }
            }
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
    
    @SuppressWarnings("unchecked")
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        chunkUnloadCooldown = Math.max(0, chunkUnloadCooldown - 1);
        ServerLevel sLevel = (ServerLevel) lvl;
        MinecraftServer server = sLevel.getServer();
        if (!server.isStopped() && canLoadChunks() && needsForceLoaderUpdate()) {
            chunkUpdateCooldown = 100;
            updateForcedChunks();
        } else chunkUpdateCooldown = Math.max(0, chunkUpdateCooldown - 1);
        
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
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) return InteractionResult.sidedSuccess(level.isClientSide());
        var useAfter = this.useAfter(state, level, pos, player, hand, hit);
        if (useAfter != InteractionResult.PASS) return useAfter;
        if (canConstructMenu()) {
            AbstractMachineMenu.openContainer(player, pos);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
    
    protected InteractionResult useAfter(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
    
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        processCondition = condition;
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        findRecipe();
        setProcessCondition(new ProcessCondition(this), getCachedRecipe());
        if (!level.isClientSide() && canLoadChunks()) {
            addToManager();
            updateForcedChunks();
        }
    }
    
    @Override
    @SuppressWarnings("ConstantConditions")
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
        if(energy != null && tag.contains("Energy") && shouldSerializeEnergy()) energy.deserializeNBT(tag.get("Energy"));
        if(inventory != null && shouldSerializeInventory()) inventory.deserializeNBT(tag.getCompound("Inventory"));
        if(tank != null && shouldSerializeTank()) tank.deserializeNBT(tag.getCompound("Fluid"));
        energyPerTick = tag.getInt("EnergyPerTick");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("progress", progress);
        if(energy != null) tag.put("Energy", energy.serializeNBT());
        if(inventory != null) tag.put("Inventory", inventory.serializeNBT());
        if(tank != null) tag.put("Fluid", tank.serializeNBT());
        tag.putInt("EnergyPerTick", energyPerTick);
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
    
    @Override
    public void setRemoved() {
        RecipeCache.clearRecipes(this);
        FakePlayerHelper.removeFakePlayer(this);
        removeFromManager();
        forcedChunks.clear();
        super.setRemoved();
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
    
    protected void progress(Runnable result) {
        if (processCondition != null && energy != null) {
            progress(processCondition::shouldAbourt, result, energy);
        }
    }
    
    protected void progress(BooleanSupplier test, Runnable result, ModEnergyStorage energy) {
        progress(test, Runnables.doNothing(), result, energy);
    }

    protected void progress(BooleanSupplier test, Runnable running, Runnable result, ModEnergyStorage energy) {
        if (!canWork(energy) || level == null) return;;
        if (redstoneChecks(level)) {
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
    
    protected boolean redstoneChecks(Level level) {
        RedstoneConfig redstoneConfig = getConfiguration().get(RedstoneConfig.class);
        Mode mode = redstoneConfig != null ? redstoneConfig.getMode() : Mode.IGNORE;
        return redstoneChecks(mode, level);
    }

    protected boolean redstoneChecks(Mode type, Level level) {
        return switch (type) {
            case IGNORE -> true;
            case HIGH -> level.hasNeighborSignal(getBlockPos());
            case LOW -> !level.hasNeighborSignal(getBlockPos());
        };
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
    
    protected void outputItemToDir(Level lvl, BlockPos pos, Direction dir, IItemHandlerModifiable inventory, List<Integer> outputSlotList, Predicate<ItemStack> canOutput) {
        IItemHandler targetInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());
        if(targetInv != null) {
            for(int output : outputSlotList) {
                try {
                    ItemStack stack = inventory.getStackInSlot(output);
                    if (stack.isEmpty() || !canOutput.test(stack.copy())) continue;
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(targetInv, stack, true);
                    if(remainder.isEmpty()) {
                        ItemHandlerHelper.insertItemStacked(targetInv, stack.copyWithCount(stack.getCount()), false);
                        inventory.extractItem(output, stack.getCount(), false);
                    }else {
                        int transferred = stack.getCount() - remainder.getCount();
                        ItemHandlerHelper.insertItemStacked(targetInv, stack.copyWithCount(transferred), false);
                        inventory.extractItem(output, transferred, false);
                    }
                } catch (IndexOutOfBoundsException e) {
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
    
    
    /**
     * Method used to save machine data when the machine is mined with a wrench.
     * Machine data is saved to an NBT tag which can later be restored when the machine is placed again.
     * This allows the machine's contents (e.g., items, fluids, energy) to be preserved during relocation.
     *
     * @param additionalTag The NBT tag used to store the machine contents
     */
    public void saveContents(CompoundTag additionalTag) {
        additionalTag.putInt("Progress", progress);
        additionalTag.putInt("MaxProgress", maxProgress);
        additionalTag.putInt("EnergyPerTick", energyPerTick);
        CompoundTag configTag = new CompoundTag();
        getConfiguration().save(configTag);
        additionalTag.put(ModBlockEntity.CONFIG_TAG_KEY, configTag);
    }
    
    /**
     * Method used to load machine data when a machine that was mined with a wrench is placed again.
     * Reads machine data from an NBT tag and saves it to the dropped machine item.
     * This allows the machine's contents (e.g., items, fluids, energy) to be preserved during relocation.
     *
     * @param additionalTag The NBT tag containing the machine contents to load
     */
    public void loadContents(CompoundTag additionalTag) {
        if (additionalTag.contains("Progress")) progress = additionalTag.getInt("Progress");
        if (additionalTag.contains("MaxProgress")) maxProgress = additionalTag.getInt("MaxProgress");
        if (additionalTag.contains("EnergyPerTick")) energyPerTick = additionalTag.getInt("EnergyPerTick");
        CompoundTag configTag = additionalTag.getCompound(ModBlockEntity.CONFIG_TAG_KEY);
        getConfiguration().load(configTag);
    }
    
    //region ChunkLoader implementation
    @Override
    public Set<ForceLoadManager.ForcedChunkPos> getForcedChunks() {
        return forcedChunks;
    }
    
    @Override
    public boolean canLoadChunks() {
        return getEnchantmentLevel(ModEnchantments.WORLDBIND.get()) > 0;
    }
    
    @Override
    public int getLoadingRange() {
        return 1;
    }
    
    @Override
    public int getChunkUnloadCooldown() {
        return chunkUnloadCooldown;
    }
    
    @Override
    public void setChunkUnloadCooldown(int cooldown) {
        chunkUnloadCooldown = cooldown;
    }
    
    @Override
    public boolean needsForceLoaderUpdate() {
        return chunkUpdateCooldown <= 0;
    }
    //endregion
}
