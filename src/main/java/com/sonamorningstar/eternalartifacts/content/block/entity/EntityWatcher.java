package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTagEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTypeEntry;
import com.sonamorningstar.eternalartifacts.api.machine.config.RedstoneOutputThreshold;
import com.sonamorningstar.eternalartifacts.content.block.base.EntityFilterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.EntityFilterHelper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@Setter
public class EntityWatcher extends GenericMachine implements WorkingAreaProvider, EntityFilterable {
	private EntityPredicateEntry entityFilter = new EntityPredicateEntry();
	private List<EntityTypeEntry> entityTypeEntries = new ArrayList<>();
	private List<EntityTagEntry> entityTagEntries = new ArrayList<>();
	public int entityCount = 0;
	
	public EntityWatcher(BlockPos pos, BlockState blockState) {
		super(ModMachines.ENTITY_WATCHER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		screenInfo.setShouldDrawArrow(false);
	}
	
	@Override
	public void registerConfigs() {
		super.registerConfigs();
		getConfiguration().add(new RedstoneOutputThreshold());
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("EntityFilter", entityFilter.serializeNBT());
		EntityFilterHelper.saveTypeEntries(tag, entityTypeEntries);
		EntityFilterHelper.saveTagEntries(tag, entityTagEntries);
		tag.putInt("EntityCount", entityCount);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("EntityFilter")) {
			entityFilter.deserializeNBT(tag.getCompound("EntityFilter"));
		}
		EntityFilterHelper.loadTypeEntries(tag, entityTypeEntries);
		EntityFilterHelper.loadTagEntries(tag, entityTagEntries);
		entityCount = tag.getInt("EntityCount");
	}
	
	@Override
	public void saveContents(CompoundTag additionalTag) {
		super.saveContents(additionalTag);
		additionalTag.put("EntityFilter", entityFilter.serializeNBT());
		EntityFilterHelper.saveTypeEntries(additionalTag, entityTypeEntries);
		EntityFilterHelper.saveTagEntries(additionalTag, entityTagEntries);
	}
	
	@Override
	public void loadContents(CompoundTag additionalTag) {
		super.loadContents(additionalTag);
		if (additionalTag.contains("EntityFilter")) {
			entityFilter.deserializeNBT(additionalTag.getCompound("EntityFilter"));
		}
		EntityFilterHelper.loadTypeEntries(additionalTag, entityTypeEntries);
		EntityFilterHelper.loadTagEntries(additionalTag, entityTagEntries);
	}
	
	@Override
	public Predicate<EntityPredicateEntry.EntityPredicate> getFilterValidator() {
		return p -> true;
	}
	
	@Override
	public AABB getWorkingArea(BlockPos anchor) {
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos center = anchor.relative(facing.getOpposite(), 5);
		return new AABB(center).inflate(4, 0.5, 4).move(0, 0.5, 0);
	}
	
	@Override
	public int getRedstoneOutput() {
		RedstoneOutputThreshold cfg = getConfiguration().get(RedstoneOutputThreshold.class);
		if (cfg != null){
			RedstoneOutputThreshold.ThresholdMode mode = cfg.getMode();
			if (mode == RedstoneOutputThreshold.ThresholdMode.IGNORE) return entityCount;
			int threshold = cfg.getThreshold();
			return switch (mode) {
				case BELOW -> entityCount < threshold ? 15 : 0;
				case BELOW_EQUAL -> entityCount <= threshold ? 15 : 0;
				case ABOVE -> entityCount > threshold ? 15 : 0;
				case ABOVE_EQUAL -> entityCount >= threshold ? 15 : 0;
				case EXACT -> entityCount == threshold ? 15 : 0;
				default -> entityCount;
			};
		}
		return entityCount;
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		if (!redstoneChecks(lvl)) return;
		
		if (canWork(energy)) {
			List<Entity> entities = lvl.getEntitiesOfClass(Entity.class, getWorkingArea(pos), this::matchesAllFilters);
			entityCount = entities.size();
			spendEnergy(energy);
		} else entityCount = 0;
		
		sendUpdate();
		
	}
}
