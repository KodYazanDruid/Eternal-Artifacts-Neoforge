package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidPipe;
import com.sonamorningstar.eternalartifacts.content.block.entity.ItemPipe;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.FilterablePipeBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.network.FluidStackFilterToServer;
import com.sonamorningstar.eternalartifacts.network.FluidTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.ItemTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Getter
public class PipeFilterMenu extends AbstractModContainerMenu {
	protected SimpleContainer fakeSlots;
	protected NonNullList<FilterEntry> filters;
	@Setter
	protected boolean isWhitelist;
	@Setter
	protected boolean ignoresNbt;
	protected final BlockPos pos;
	protected final Direction dir;
	protected final int attType;
	protected final Level level;
	protected final FilterablePipeBlockEntity<?> pipe;

	public PipeFilterMenu(int id, Inventory inv,
						  BlockPos pos, Direction dir, int type,
						  boolean isWhitelist, boolean ignoresNbt,
						  NonNullList<FilterEntry> filters) {
		super(ModMenuTypes.PIPE_FILTER.get(), id);
		this.pos = pos;
		this.dir = dir;
		this.attType = type;
		this.level = inv.player.level();
		this.pipe = (FilterablePipeBlockEntity<?>) inv.player.level().getBlockEntity(pos);
		this.fakeSlots = new SimpleContainer(9);
		this.filters = filters;
		this.isWhitelist = isWhitelist;
		this.ignoresNbt = ignoresNbt;
		for (int i = 0; i < 9; i++) {
			FilterEntry entry = filters.get(i);
			if (entry instanceof ItemStackEntry ise) fakeSlots.setItem(i, ise.getFilterStack());
			else fakeSlots.setItem(i, ItemStack.EMPTY);
			entry.setWhitelist(isWhitelist);
			entry.setIgnoreNBT(ignoresNbt);
		}
		
		this.fakeSlots.addListener(this::slotsChanged);
		addPlayerInventoryAndHotbar(inv, 8, 66);
		addFakeSlots(62, 17);
	}
	
	private void addFakeSlots(int xOff, int yOff) {
		for (int i = 0; i < 9; i++) {
			addSlot(new FilterFakeSlot(fakeSlots, filters.get(i), i, xOff + (i % 3) * 18, yOff + (i / 3) * 18, false));
		}
	}
	
	@Override
	public void fakeSlotSynch(UpdateFakeSlotToServer pkt) {
		fakeSlots.setItem(pkt.index(), pkt.slotItem());
		filters.set(pkt.index(), new ItemStackEntry(pkt.slotItem(), ignoresNbt));
		saveFilterEntries();
	}
	
	public void itemTagFilter(ItemTagFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filters.set(pkt.index(), new ItemTagEntry(pkt.tag()));
		saveFilterEntries();
	}
	
	public void fluidStackFilter(FluidStackFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filters.set(pkt.index(), new FluidStackEntry(pkt.fluidStack(), ignoresNbt));
		saveFilterEntries();
	}
	public void fluidTagFilter(FluidTagFilterToServer pkt) {
		fakeSlots.setItem(pkt.index(), ItemStack.EMPTY);
		filters.set(pkt.index(), new FluidTagEntry(pkt.tag()));
		saveFilterEntries();
	}
	
	private void saveFilterEntries() {
		pipe.filterEntries.put(dir, filters);
		pipe.sendUpdate();
	}
	
	@Override
	public boolean clickMenuButton(Player pPlayer, int id) {
		if (id == 0) {
			isWhitelist = !isWhitelist;
			pipe.whitelists.put(dir, isWhitelist);
			for (FilterEntry filter : filters) {
				filter.setWhitelist(isWhitelist);
			}
			pipe.sendUpdate();
			return true;
		} else if (id == 1) {
			ignoresNbt = !ignoresNbt;
			pipe.nbtIgnores.put(dir, ignoresNbt);
			for (FilterEntry filter : filters) {
				filter.setIgnoreNBT(ignoresNbt);
			}
			pipe.sendUpdate();
			return true;
		}
		return super.clickMenuButton(pPlayer, id);
	}
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(player.level(), pos), player, player.level().getBlockState(pos).getBlock());
	}
	
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int index) {
		return ItemStack.EMPTY;
	}
	
	public static PipeFilterMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		BlockEntity be = inv.player.level().getBlockEntity(pos);
		if (be == null) throw new IllegalArgumentException("No block entity found at position: " + pos);
		return new PipeFilterMenu(id, inv, pos,
			buf.readEnum(Direction.class),
			buf.readVarInt(),
			buf.readBoolean(), buf.readBoolean(),
			buf.readCollection(NonNullList::createWithCapacity, b -> {
				if (be instanceof ItemPipe) return ItemFilterEntry.readFromNetwork(b);
				if (be instanceof FluidPipe) return FluidFilterEntry.readFromNetwork(b);
				throw new IllegalArgumentException("Unknown pipe type: " + be.getClass().getName());
			}));
	}
}
