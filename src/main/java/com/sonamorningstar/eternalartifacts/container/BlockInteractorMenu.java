package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.BlockBreaker;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

@Getter
public class BlockInteractorMenu extends AbstractMachineMenu {
    protected final boolean isBlockBreaker;
    
    public BlockInteractorMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(menuType, id, inv, entity, data);
        this.isBlockBreaker = entity instanceof BlockBreaker;
        this.fakeSlots.addListener(this::slotsChanged);
        
        if (beInventory != null) {
            if (isBlockBreaker) {
                addSlot(new SlotItemHandler(beInventory, 0, 54, 35));
                
                for (int i = 1; i < beInventory.getSlots(); i++) {
                    int outputIndex = i - 1;
                    int col = outputIndex % 4;
                    int row = outputIndex / 4;
                    int x = 80 + col * 18;
                    int y = 26 + row * 18;
                    addSlot(new SlotItemHandler(beInventory, i, x, y));
                }
            } else {
                for (int i = 0; i < beInventory.getSlots(); i++) {
                    int col = i % 2;
                    int row = i / 2;
                    int x = 80 + col * 18;
                    int y = 26 + row * 18;
                    addSlot(new SlotItemHandler(beInventory, i, x, y));
                }
            }
        }
        
        //addFilterFakeSlots();
    }
    
    private void addFilterFakeSlots() {
        if (isBlockBreaker) {
            for (int i = 0; i < FILTER_SIZE; i++) {
                FilterFakeSlot filterSlot = new FilterFakeSlot(fakeSlots, blockFilterEntries.get(i), i, 0, 0, false);
                blockFilterFakeSlots.add(filterSlot);
                addSlot(filterSlot);
            }
        } else {
            for (int i = 0; i < FILTER_SIZE; i++) {
                FilterFakeSlot filterSlot = new FilterFakeSlot(fakeSlots, itemFilterEntries.get(i), i, 0, 0, false);
                itemFilterFakeSlots.add(filterSlot);
                addSlot(filterSlot);
            }
        }
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
            
            if (isBlockBreaker) {
                NonNullList<BlockFilterEntry> blockFilters = filterable.getBlockFilters();
                blockFilters.replaceAll(ignored -> BlockFilterEntry.Empty.create(isBlockWhitelist));
                
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
                
                filterable.setBlockFilterWhitelistSilent(isBlockWhitelist);
                filterable.setBlockFilterIgnorePropertiesSilent(isBlockIgnoresProps);
            } else {
                NonNullList<ItemFilterEntry> itemFilters = filterable.getItemFilters();
                itemFilters.replaceAll(ignored -> ItemFilterEntry.Empty.create(isItemWhitelist));
                
                for (int i = 0; i < itemFilterEntries.size(); i++) {
                    FilterEntry entry = itemFilterEntries.get(i);
                    if (entry instanceof ItemFilterEntry ife && !ife.isEmpty()) {
                        if (i < itemFilters.size()) {
                            itemFilters.set(i, ife);
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
                
                filterable.setItemFilterWhitelistSilent(isItemWhitelist);
                filterable.setItemFilterIgnoreNBTSilent(isItemIgnoresNbt);
            }
            
            filterable.setFluidFilterWhitelistSilent(isFluidWhitelist);
            filterable.setFluidFilterIgnoreNBTSilent(isFluidIgnoresNbt);
            
            if (blockEntity instanceof ModBlockEntity mbe) mbe.markDirty();
        }
    }
}
