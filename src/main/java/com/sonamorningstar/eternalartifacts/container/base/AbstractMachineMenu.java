package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Map;

import com.sonamorningstar.eternalartifacts.network.*;

public abstract class AbstractMachineMenu extends AbstractModContainerMenu implements FilterSyncable {
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
    
    public static final int FILTER_SIZE = 9;
    
    protected final SimpleContainer fakeSlots = new SimpleContainer(FILTER_SIZE);
    
    @Getter protected final NonNullList<ItemFilterEntry> itemFilterEntries = NonNullList.withSize(FILTER_SIZE, ItemFilterEntry.Empty.create(true));
    @Getter protected final NonNullList<FluidFilterEntry> fluidFilterEntries = NonNullList.withSize(FILTER_SIZE, FluidFilterEntry.Empty.create(true));
    @Getter protected final NonNullList<BlockFilterEntry> blockFilterEntries = NonNullList.withSize(FILTER_SIZE, BlockFilterEntry.Empty.create(true));

    @Getter public final List<FilterFakeSlot> itemFilterFakeSlots = new ArrayList<>();
    @Getter public final List<FilterFakeSlot> fluidFilterFakeSlots = new ArrayList<>();
    @Getter public final List<FilterFakeSlot> blockFilterFakeSlots = new ArrayList<>();
    
    @Getter @Setter protected boolean isItemWhitelist = true;
    @Getter @Setter protected boolean isItemIgnoresNbt = true;
    @Getter @Setter protected boolean isFluidWhitelist = true;
    @Getter @Setter protected boolean isFluidIgnoresNbt = true;
    @Getter @Setter protected boolean isBlockWhitelist = true;
    @Getter @Setter protected boolean isBlockIgnoresProps = true;

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
        
        if (entity instanceof Filterable filterable) {
            if (filterable instanceof AbstractMultiblockBlockEntity part && !part.isMaster()) return;
            
            isItemWhitelist = filterable.isItemFilterWhitelist();
            isItemIgnoresNbt = filterable.isItemFilterIgnoreNBT();
            isFluidWhitelist = filterable.isFluidFilterWhitelist();
            isFluidIgnoresNbt = filterable.isFluidFilterIgnoreNBT();
            isBlockWhitelist = filterable.isBlockFilterWhitelist();
            isBlockIgnoresProps = filterable.isBlockFilterIgnoreProperties();
            
            for (int i = 0; i < FILTER_SIZE && i < filterable.getItemFilters().size(); i++) {
                itemFilterEntries.set(i, filterable.getItemFilters().get(i));
            }
            for (int i = 0; i < FILTER_SIZE && i < filterable.getFluidFilters().size(); i++) {
                fluidFilterEntries.set(i, filterable.getFluidFilters().get(i));
            }
            for (int i = 0; i < FILTER_SIZE && i < filterable.getBlockFilters().size(); i++) {
                blockFilterEntries.set(i, filterable.getBlockFilters().get(i));
            }
            
            if (filterable.hasItemFilters()) {
                for (int i = 0; i < FILTER_SIZE; i++) {
                    FilterFakeSlot filterSlot = new FilterFakeSlot(fakeSlots, itemFilterEntries.get(i), i, 0, 0, false);
                    itemFilterFakeSlots.add(filterSlot);
                    addSlot(filterSlot);
                }
            }
            if (filterable.hasFluidFilters()) {
                for (int i = 0; i < FILTER_SIZE; i++) {
                    FilterFakeSlot filterSlot = new FilterFakeSlot(fakeSlots, fluidFilterEntries.get(i), i, 0, 0, false);
                    fluidFilterFakeSlots.add(filterSlot);
                    addSlot(filterSlot);
                }
            }
            if (filterable.hasBlockFilters()) {
                for (int i = 0; i < FILTER_SIZE; i++) {
                    FilterFakeSlot filterSlot = new FilterFakeSlot(fakeSlots, blockFilterEntries.get(i), i, 0, 0, false);
                    blockFilterFakeSlots.add(filterSlot);
                    addSlot(filterSlot);
                }
            }
        }
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

    /**
     * API to add slots in a specific pattern.
     * @param handler The item handler.
     * @param pattern Array of strings representing the grid (e.g. {" X ", "X X"}).
     * @param mappings A map binding a character from the pattern to a specific slot index.
     * @param startX Starting X coordinate of the top-left corner of the pattern.
     * @param startY Starting Y coordinate of the top-left corner of the pattern.
     * @param slotSpacingX The horizontal spacing between slots (typically 18).
     * @param slotSpacingY The vertical spacing between slots (typically 18).
     */
    public void generateSlotPattern(IItemHandler handler, String[] pattern, Map<Character, Integer> mappings, int startX, int startY, int slotSpacingX, int slotSpacingY) {
        for (int row = 0; row < pattern.length; row++) {
            String line = pattern[row];
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                if (c != ' ' && mappings.containsKey(c)) {
                    int slotIndex = mappings.get(c);
                    int x = startX + col * slotSpacingX;
                    int y = startY + row * slotSpacingY;
                    addSlot(new SlotItemHandler(handler, slotIndex, x, y));
                }
            }
        }
    }

    public static OptionalInt openContainer(Player player, BlockPos pos) {
        final BlockEntity blockEntity = player.level().getBlockEntity(pos);
        if (!(blockEntity instanceof MenuProvider prov)) return OptionalInt.empty();
        if (blockEntity instanceof ModBlockEntity mbe && mbe.getConfiguration() == null) return OptionalInt.empty();
        return player.openMenu(prov, pos);
    }
    
    @Override
    public void fakeSlotSynch(UpdateFakeSlotToServer pkt) {
        fakeSlots.setItem(pkt.index(), pkt.slotItem());
        ItemStackEntry itemStackEntry = new ItemStackEntry(pkt.slotItem(), isItemIgnoresNbt);
        itemFilterEntries.set(pkt.index(), itemStackEntry);
        if(!itemFilterFakeSlots.isEmpty()) itemFilterFakeSlots.get(pkt.index()).setFilter(itemStackEntry);
        saveFilterEntries();
    }
    
    @Override
    public void itemTagFilterSynch(ItemTagFilterToServer pkt) {
        fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
        ItemTagEntry itemTagEntry = new ItemTagEntry(pkt.tag());
        itemFilterEntries.set(pkt.index(), itemTagEntry);
        if(!itemFilterFakeSlots.isEmpty()) itemFilterFakeSlots.get(pkt.index()).setFilter(itemTagEntry);
        saveFilterEntries();
    }
    
    @Override
    public void fluidStackFilterSync(FluidStackFilterToServer pkt) {
        fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
        FluidStackEntry fluidStackEntry = new FluidStackEntry(pkt.fluidStack(), isFluidIgnoresNbt);
        fluidFilterEntries.set(pkt.index(), fluidStackEntry);
        if(!fluidFilterFakeSlots.isEmpty()) fluidFilterFakeSlots.get(pkt.index()).setFilter(fluidStackEntry);
        saveFilterEntries();
    }
    
    @Override
    public void fluidTagFilterSync(FluidTagFilterToServer pkt) {
        fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
        FluidTagEntry fluidTagEntry = new FluidTagEntry(pkt.tag());
        fluidFilterEntries.set(pkt.index(), fluidTagEntry);
        if(!fluidFilterFakeSlots.isEmpty()) fluidFilterFakeSlots.get(pkt.index()).setFilter(fluidTagEntry);
        saveFilterEntries();
    }
    
    @Override
    public void blockStateFilterSync(BlockStateFilterToServer pkt) {
        fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
        BlockState state = pkt.blockState();
        if (state == null || state.isAir()) {
            blockFilterEntries.set(pkt.index(), BlockFilterEntry.Empty.create(isBlockWhitelist));
            if(!blockFilterFakeSlots.isEmpty()) blockFilterFakeSlots.get(pkt.index()).setFilter(BlockFilterEntry.Empty.create(isBlockWhitelist));
        } else {
            BlockStateEntry blockStateEntry = BlockStateEntry.matchBlockOnly(state);
            blockStateEntry.setIgnoreNBT(isBlockIgnoresProps);
            blockFilterEntries.set(pkt.index(), blockStateEntry);
            if(!blockFilterFakeSlots.isEmpty()) blockFilterFakeSlots.get(pkt.index()).setFilter(blockStateEntry);
        }
        saveFilterEntries();
    }
    
    @Override
    public void blockTagFilterSync(BlockTagFilterToServer pkt) {
        fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
        BlockTagEntry blockTagEntry = new BlockTagEntry(pkt.tag());
        blockFilterEntries.set(pkt.index(), blockTagEntry);
        if(!blockFilterFakeSlots.isEmpty()) blockFilterFakeSlots.get(pkt.index()).setFilter(blockTagEntry);
        saveFilterEntries();
    }
    
    @Override
    public void blockStatePropertiesFilterSync(BlockStatePropertiesFilterToServer pkt) {
        fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
        BlockState state = pkt.blockState();
        if (state == null || state.isAir()) {
            blockFilterEntries.set(pkt.index(), BlockFilterEntry.Empty.create(isBlockWhitelist));
            if(!blockFilterFakeSlots.isEmpty()) blockFilterFakeSlots.get(pkt.index()).setFilter(BlockFilterEntry.Empty.create(isBlockWhitelist));
        } else {
            BlockStateEntry blockStateEntry = new BlockStateEntry(state, false);
            blockStateEntry.setMatchingProperties(pkt.matchingProperties());
            blockStateEntry.setIgnoreNBT(isBlockIgnoresProps);
            blockStateEntry.setWhitelist(isBlockWhitelist);
            blockFilterEntries.set(pkt.index(), blockStateEntry);
            if(!blockFilterFakeSlots.isEmpty()) blockFilterFakeSlots.get(pkt.index()).setFilter(blockStateEntry);
        }
        saveFilterEntries();
    }
    
    @Override
    public boolean clickMenuButton(Player pPlayer, int buttonId) {
        if (buttonId == 0) {
            isItemWhitelist = !isItemWhitelist;
            for (FilterEntry entry : itemFilterEntries) {
                entry.setWhitelist(isItemWhitelist);
            }
            saveFilterEntries();
            return true;
        } else if (buttonId == 1) {
            isItemIgnoresNbt = !isItemIgnoresNbt;
            for (FilterEntry entry : itemFilterEntries) {
                entry.setIgnoreNBT(isItemIgnoresNbt);
            }
            saveFilterEntries();
            return true;
        } else if (buttonId == 2) {
            isFluidWhitelist = !isFluidWhitelist;
            for (FilterEntry entry : fluidFilterEntries) {
                entry.setWhitelist(isFluidWhitelist);
            }
            saveFilterEntries();
            return true;
        } else if (buttonId == 3) {
            isFluidIgnoresNbt = !isFluidIgnoresNbt;
            for (FilterEntry entry : fluidFilterEntries) {
                entry.setIgnoreNBT(isFluidIgnoresNbt);
            }
            saveFilterEntries();
            return true;
        } else if (buttonId == 4) {
            isBlockWhitelist = !isBlockWhitelist;
            for (FilterEntry entry : blockFilterEntries) {
                entry.setWhitelist(isBlockWhitelist);
            }
            saveFilterEntries();
            return true;
        } else if (buttonId == 5) {
            isBlockIgnoresProps = !isBlockIgnoresProps;
            for (FilterEntry entry : blockFilterEntries) {
                entry.setIgnoreNBT(isBlockIgnoresProps);
            }
            saveFilterEntries();
            return true;
        }
        return super.clickMenuButton(pPlayer, buttonId);
    }
    
    protected void saveFilterEntries() {
        if (blockEntity instanceof Filterable filterable) {
            NonNullList<FluidFilterEntry> fluidFilters = filterable.getFluidFilters();
            fluidFilters.replaceAll(ignored -> FluidFilterEntry.Empty.create(isFluidWhitelist));
            
            NonNullList<BlockFilterEntry> blockFilters = filterable.getBlockFilters();
            blockFilters.replaceAll(ignored -> BlockFilterEntry.Empty.create(isBlockWhitelist));
            
            NonNullList<ItemFilterEntry> itemFilters = filterable.getItemFilters();
            itemFilters.replaceAll(ignored -> ItemFilterEntry.Empty.create(isItemWhitelist));
            
            for (int i = 0; i < blockFilterEntries.size(); i++) {
                FilterEntry entry = blockFilterEntries.get(i);
                if (entry instanceof BlockFilterEntry bfe && !bfe.isEmpty()) {
                    if (i < blockFilters.size()) {
                        blockFilters.set(i, bfe);
                    }
                }
            }
            for (int i = 0; i < fluidFilterEntries.size(); i++) {
                FilterEntry entry = fluidFilterEntries.get(i);
                if (entry instanceof FluidFilterEntry ffe && !ffe.isEmpty()) {
                    if (i < fluidFilters.size()) {
                        fluidFilters.set(i, ffe);
                    }
                }
            }
            for (int i = 0; i < itemFilterEntries.size(); i++) {
                FilterEntry entry = itemFilterEntries.get(i);
                if (entry instanceof ItemFilterEntry ife && !ife.isEmpty()) {
                    if (i < itemFilters.size()) {
                        itemFilters.set(i, ife);
                    }
                }
            }
            
            filterable.setBlockFilterWhitelistSilent(isBlockWhitelist);
            filterable.setBlockFilterIgnorePropertiesSilent(isBlockIgnoresProps);
            filterable.setItemFilterWhitelistSilent(isItemWhitelist);
            filterable.setItemFilterIgnoreNBTSilent(isItemIgnoresNbt);
            filterable.setFluidFilterWhitelistSilent(isFluidWhitelist);
            filterable.setFluidFilterIgnoreNBTSilent(isFluidIgnoresNbt);
            
            if (blockEntity instanceof ModBlockEntity mbe) mbe.markDirty();
        }
    }
}
