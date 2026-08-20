package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTagEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTypeEntry;
import com.sonamorningstar.eternalartifacts.content.block.base.EntityFilterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.core.ModDamageSources;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.EntityFilterHelper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.function.Predicate;

@Getter
@Setter
public class MobHarvester extends GenericMachine implements WorkingAreaProvider, EntityFilterable {
	private EntityPredicateEntry entityFilter = new EntityPredicateEntry();
	private List<EntityTypeEntry> entityTypeEntries = new ArrayList<>();
	private List<EntityTagEntry> entityTagEntries = new ArrayList<>();
	Predicate<EntityPredicateEntry.EntityPredicate> filterValidator = e ->
		!Objects.equals(e, EntityPredicateEntry.EntityPredicate.DEAD) &&
		!Objects.equals(e, EntityPredicateEntry.EntityPredicate.ALIVE);
	
	public MobHarvester(BlockPos pos, BlockState blockState) {
		super(ModMachines.MOB_HARVESTER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.EXPERIENCE), true, false));
		outputSlots.addAll(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
		setInventory(() -> createBasicInventory(16, outputSlots, (slot, stack) -> slot == 0, slot -> {
			if (!level.isClientSide() && slot == 0) {
				calculateMaxProgress();
			}
		}));
		setEnergyPerTick(250);
		isChargeProgress = true;
		screenInfo.setArrowPos(46, 19);
		screenInfo.setSlotPosition(46, 44, 0);
		for (int i = 0; i < 15; i++) {
			int x = i % 5;
			int y = i / 5;
			screenInfo.setSlotPosition(75 + x * 18, 19 + y * 18, i + 1);
		}
	}
	
	private void calculateMaxProgress() {
		getFakePlayer().detectEquipmentUpdates();
		double attackSpeed = Math.max(1.0D, Math.min(2.5D, getFakePlayer().getAttributeValue(Attributes.ATTACK_SPEED)));
		double multiplier = 2.0D / attackSpeed;
		maxProgress = (int) (defaultMaxProgress * multiplier);
		int effLvl = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
		int reduction = 10;
		double reductionFactor = (100 - reduction) / 100.0;
		maxProgress = (int) Math.max(1, Math.round(maxProgress * Math.pow(reductionFactor, effLvl)));
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		if (!level.isClientSide()) {
			calculateMaxProgress();
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("EntityFilter", entityFilter.serializeNBT());
		EntityFilterHelper.saveTypeEntries(tag, entityTypeEntries);
		EntityFilterHelper.saveTagEntries(tag, entityTagEntries);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("EntityFilter")) {
			entityFilter.deserializeNBT(tag.getCompound("EntityFilter"));
		}
		EntityFilterHelper.loadTypeEntries(tag, entityTypeEntries);
		EntityFilterHelper.loadTagEntries(tag, entityTagEntries);
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
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		if (!redstoneChecks(lvl)) return;
		List<LivingEntity> targets = lvl.getEntitiesOfClass(LivingEntity.class, getWorkingArea(getBlockPos()))
			.stream().filter(living ->
				!living.isSpectator() && !living.isDeadOrDying() && living.isAlive() &&
				!living.isInvulnerable() && matchesAllFilters(living) && getFakePlayer().canAttack(living)
			).toList();
		progressCharge(targets::isEmpty, () -> {
			spendEnergy(energy);
			int killCount = 1 + getEnchantmentLevel(ModEnchantments.CELERITY.get());
			List<LivingEntity> copy = new ArrayList<>(targets);
			copy.sort(Comparator.comparingDouble(living -> living.distanceToSqr(getFakePlayer())));
			while (killCount > 0 && !copy.isEmpty()) {
				killAndRemoveTarget(copy, lvl);
				killCount--;
			}
			return true;
		}, energy);
	}
	
	private void killAndRemoveTarget(List<LivingEntity> targets, ServerLevel lvl) {
		LivingEntity target = targets.get(lvl.random.nextInt(targets.size()));
		getFakePlayer().attack(target);
		if (target.isAlive() && !(target instanceof Player)) {
			target.setHealth(0.0F);
			target.die(ModDamageSources.INSTANCES.get(lvl).execute(getFakePlayer()));
		}
		targets.remove(target);
	}
	
	@Override
	public AABB getWorkingArea(BlockPos anchor) {
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		return new AABB(anchor.relative(facing.getOpposite(), 5)).inflate(4, 1, 4).move(0D, 1D, 0D);
	}
}
