package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.filter.FilterEntry;
import com.sonamorningstar.eternalartifacts.content.block.base.AttachmentablePipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidPipe;
import com.sonamorningstar.eternalartifacts.content.block.entity.ItemPipe;
import com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;

@Setter
public abstract class FilterablePipeBlockEntity<CAP> extends AbstractPipeBlockEntity<CAP> implements MenuProvider {
	public EnumMap<Direction, NonNullList<FilterEntry>> filterEntries = new EnumMap<>(Direction.class);
	public EnumMap<Direction, Boolean> whitelists = new EnumMap<>(Direction.class);
	public EnumMap<Direction, Boolean> nbtIgnores = new EnumMap<>(Direction.class);
	
	public FilterablePipeBlockEntity(BlockEntityType<?> type, Class<CAP> cls, BlockPos pos, BlockState state) {
		super(type, cls, pos, state);
	}
	
	@Override
	public Component getDisplayName() { return Component.empty(); }
	
	@Override
	public void openMenu(ServerPlayer player, Direction dir) {
		PipeConnectionProperty.PipeConnection conn = getBlockState().getValue(AttachmentablePipeBlock.CONNECTION_BY_DIRECTION.get(dir));
		player.openMenu(this, wr -> {
			wr.writeBlockPos(getBlockPos());
			wr.writeEnum(dir);
			wr.writeVarInt(conn == PipeConnectionProperty.PipeConnection.EXTRACT ? 0 : 1);
			wr.writeBoolean(whitelists.get(dir) != null && whitelists.get(dir));
			wr.writeBoolean(nbtIgnores.get(dir) != null && nbtIgnores.get(dir));
			wr.writeCollection(filterEntries.get(dir), (buff, entry) -> {
				entry.toNetwork(buff);
			});
		});
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		loadFilterEntries(tag);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		saveFilterEntries(tag);
	}
	
	public void saveFilterEntries(CompoundTag tag) {
		ListTag filterList = new ListTag();
		CompoundTag filterData = new CompoundTag();
		filterEntries.forEach((dir, entries) -> {
			ListTag dirList = new ListTag();
			for (FilterEntry entry : entries) {
				dirList.add(entry.serializeNBT());
			}
			CompoundTag dirTag = new CompoundTag();
			dirTag.put(dir.getName(), dirList);
			filterList.add(dirTag);
		});
		whitelists.forEach((dir, isWhitelist) -> {
			filterData.putBoolean(dir.getName() + "_whitelist", isWhitelist);
		});
		nbtIgnores.forEach((dir, ignoreNbt) -> {
			filterData.putBoolean(dir.getName() + "_ignore_nbt", ignoreNbt);
		});
		filterData.put("FilterEntries", filterList);
		tag.put("FilterData", filterData);
	}
	
	public CompoundTag saveAndRemoveForDir(Direction dir) {
		var entries = filterEntries.get(dir);
		if (entries == null) return new CompoundTag();
		CompoundTag filterData = new CompoundTag();
		ListTag dirList = new ListTag();
		for (FilterEntry entry : entries) {
			dirList.add(entry.serializeNBT());
			filterEntries.remove(dir);
			sendUpdate();
		}
		/*for (int i = 0; i < entries.size(); i++) {
			FilterEntry entry = entries.get(i);
			CompoundTag entryTag = entry.serializeNBT();
			entryTag.putInt("Index", i);
			dirList.add(entryTag);
			filterEntries.remove(dir);
			sendUpdate();
		}*/
		CompoundTag dirTag = new CompoundTag();
		if (this instanceof ItemPipe) dirTag.put("ItemFilters", dirList);
		else if (this instanceof FluidPipe) dirTag.put("FluidFilters", dirList);
		else throw new IllegalArgumentException("Unknown pipe type: " + this.getClass().getName());
		dirTag.putBoolean("whitelist", whitelists.containsKey(dir) && whitelists.remove(dir));
		dirTag.putBoolean("ignore_nbt", nbtIgnores.containsKey(dir) && nbtIgnores.remove(dir));
		filterData.put("FilterData", dirTag);
		return filterData;
	}
	
	public abstract void loadFilterEntries(CompoundTag data);
	public abstract void loadFromItemFilter(ItemStack stack, Direction direction);
	
}
