package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.base.FilterSyncable;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.BlockBreaker;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.network.FluidStackFilterToServer;
import com.sonamorningstar.eternalartifacts.network.FluidTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.ItemTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockInteractorMenu extends AbstractMachineMenu implements FilterSyncable {
	public static final int FILTER_SIZE = 9;
	
	protected final SimpleContainer fakeSlots;
	@Getter
	protected final NonNullList<FilterEntry> filterEntries;
	@Getter
	public final List<FilterFakeSlot> FAKE_FILTER_SLOTS = new ArrayList<>();
	@Getter @Setter protected boolean isWhitelist;
	@Getter @Setter protected boolean ignoresNbt;
	
	public BlockInteractorMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
		this.fakeSlots = new SimpleContainer(FILTER_SIZE);
		this.filterEntries = NonNullList.withSize(FILTER_SIZE, ItemFilterEntry.Empty.create(true));
		this.fakeSlots.addListener(this::slotsChanged);
		
		if (entity instanceof Filterable filterable) {
			isWhitelist = filterable.isItemFilterWhitelist();
			ignoresNbt = filterable.isItemFilterIgnoreNBT();
			
			for (int i = 0; i < filterable.getItemFilters().size() && i < FILTER_SIZE; i++) {
				ItemFilterEntry entry = filterable.getItemFilters().get(i);
				if (!entry.isEmpty()) {
					filterEntries.set(i, entry);
					if (entry instanceof ItemStackEntry ise) {
						fakeSlots.setItem(i, ise.getFilterStack());
					}
				}
			}
			for (int i = 0; i < filterable.getFluidFilters().size() && i < FILTER_SIZE; i++) {
				FluidFilterEntry entry = filterable.getFluidFilters().get(i);
				if (!entry.isEmpty()) {
					if (filterEntries.get(i).isEmpty()) {
						filterEntries.set(i, entry);
					} else {
						for (int j = 0; j < FILTER_SIZE; j++) {
							if (filterEntries.get(j).isEmpty()) {
								filterEntries.set(j, entry);
								break;
							}
						}
					}
				}
			}
		}
		
		if (beInventory != null) {
			boolean isBreaker = entity instanceof BlockBreaker;
			
			if (isBreaker) {
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
		
		addFilterFakeSlots();
	}
	
	private void addFilterFakeSlots() {
		for (int i = 0; i < FILTER_SIZE; i++) {
			FilterFakeSlot filterSlot = new FilterFakeSlot(fakeSlots, filterEntries.get(i), i, 0, 0, false);
			FAKE_FILTER_SLOTS.add(filterSlot);
			addSlot(filterSlot);
		}
	}
	
	@Override
	public void fakeSlotSynch(UpdateFakeSlotToServer pkt) {
		fakeSlots.setItem(pkt.index(), pkt.slotItem());
		ItemStackEntry itemStackEntry = new ItemStackEntry(pkt.slotItem(), ignoresNbt);
		filterEntries.set(pkt.index(), itemStackEntry);
		FAKE_FILTER_SLOTS.get(pkt.index()).setFilter(itemStackEntry);
		saveFilterEntries();
	}
	
	@Override
	public void itemTagFilterSynch(ItemTagFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		ItemTagEntry itemTagEntry = new ItemTagEntry(pkt.tag());
		filterEntries.set(pkt.index(), itemTagEntry);
		FAKE_FILTER_SLOTS.get(pkt.index()).setFilter(itemTagEntry);
		saveFilterEntries();
	}
	
	@Override
	public void fluidStackFilterSync(FluidStackFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		FluidStackEntry fluidStackEntry = new FluidStackEntry(pkt.fluidStack(), ignoresNbt);
		filterEntries.set(pkt.index(), fluidStackEntry);
		FAKE_FILTER_SLOTS.get(pkt.index()).setFilter(fluidStackEntry);
		saveFilterEntries();
	}
	
	@Override
	public void fluidTagFilterSync(FluidTagFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		FluidTagEntry fluidTagEntry = new FluidTagEntry(pkt.tag());
		filterEntries.set(pkt.index(), fluidTagEntry);
		FAKE_FILTER_SLOTS.get(pkt.index()).setFilter(fluidTagEntry);
		saveFilterEntries();
	}
	
	@Override
	public boolean clickMenuButton(Player pPlayer, int buttonId) {
		if (buttonId == 0) {
			isWhitelist = !isWhitelist;
			for (FilterEntry entry : filterEntries) {
				entry.setWhitelist(isWhitelist);
			}
			saveFilterEntries();
			return true;
		} else if (buttonId == 1) {
			ignoresNbt = !ignoresNbt;
			for (FilterEntry entry : filterEntries) {
				entry.setIgnoreNBT(ignoresNbt);
			}
			saveFilterEntries();
			return true;
		}
		return super.clickMenuButton(pPlayer, buttonId);
	}
	
	protected void saveFilterEntries() {
		if (blockEntity instanceof Filterable filterable) {
			NonNullList<ItemFilterEntry> itemFilters = filterable.getItemFilters();
			NonNullList<FluidFilterEntry> fluidFilters = filterable.getFluidFilters();
			
			itemFilters.replaceAll(ignored -> ItemFilterEntry.Empty.create(isWhitelist));
			fluidFilters.replaceAll(ignored -> FluidFilterEntry.Empty.create(isWhitelist));
			
			for (int i = 0; i < filterEntries.size(); i++) {
				FilterEntry entry = filterEntries.get(i);
				if (entry instanceof ItemFilterEntry ife && !ife.isEmpty()) {
					if (i < itemFilters.size()) {
						itemFilters.set(i, ife);
					}
				} else if (entry instanceof FluidFilterEntry ffe && !ffe.isEmpty()) {
					if (i < fluidFilters.size()) {
						fluidFilters.set(i, ffe);
					}
				}
			}
			
			filterable.setItemFilterWhitelist(isWhitelist);
			filterable.setItemFilterIgnoreNBT(ignoresNbt);
			filterable.setFluidFilterWhitelist(isWhitelist);
			filterable.setFluidFilterIgnoreNBT(ignoresNbt);
			
			if (blockEntity instanceof ModBlockEntity mbe) mbe.sendUpdate();
		}
	}
}
