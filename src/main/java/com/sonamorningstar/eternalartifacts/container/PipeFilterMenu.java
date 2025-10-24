package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractPipeFilterMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidPipe;
import com.sonamorningstar.eternalartifacts.content.block.entity.ItemPipe;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.FilterablePipeBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Getter
public class PipeFilterMenu extends AbstractPipeFilterMenu {
	protected final BlockPos pos;
	protected final Direction dir;
	protected final Level level;
	protected final FilterablePipeBlockEntity<?> pipe;

	public PipeFilterMenu(int id, Inventory inv,
						  BlockPos pos, Direction dir, int type,
						  boolean isWhitelist, boolean ignoresNbt,
						  NonNullList<FilterEntry> filters) {
		super(ModMenuTypes.PIPE_FILTER.get(), id, inv, type);
		this.pos = pos;
		this.dir = dir;
		this.level = inv.player.level();
		this.pipe = (FilterablePipeBlockEntity<?>) inv.player.level().getBlockEntity(pos);
		this.isWhitelist = isWhitelist;
		this.ignoresNbt = ignoresNbt;
		for (int i = 0; i < 9; i++) {
			FilterEntry entry = filters.get(i);
			if (pipe instanceof ItemPipe && entry instanceof ItemFilterEntry ife) filterEntries.set(i, ife);
			else if (pipe instanceof FluidPipe && entry instanceof FluidFilterEntry fse) filterEntries.set(i, fse);
			if (entry instanceof ItemStackEntry ise) fakeSlots.setItem(i, ise.getFilterStack());
			else fakeSlots.setItem(i, ItemStack.EMPTY);
			entry.setWhitelist(isWhitelist);
			entry.setIgnoreNBT(ignoresNbt);
		}
		
	}
	
	@Override
	protected void saveFilterEntries() {
		pipe.filterEntries.put(dir, filterEntries);
		pipe.whitelists.put(dir, isWhitelist);
		pipe.nbtIgnores.put(dir, ignoresNbt);
		pipe.sendUpdate();
	}
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(player.level(), pos), player, player.level().getBlockState(pos).getBlock());
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
				else if (be instanceof FluidPipe) return FluidFilterEntry.readFromNetwork(b);
				else {
					EternalArtifacts.LOGGER.error("Unknown pipe type {} at {}", be.getClass().getName(), pos);
					return ItemFilterEntry.Empty.create(true);
				}
			}));
	}
}
