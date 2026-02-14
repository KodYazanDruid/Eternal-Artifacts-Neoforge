package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.content.block.base.EntityFilterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.LivingEntityExposer;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Getter
@Setter
public class MobHarvester extends GenericMachine implements WorkingAreaProvider, EntityFilterable {
	private EntityPredicateEntry entityFilter = new EntityPredicateEntry();
	Predicate<EntityPredicateEntry.EntityPredicate> filterValidator = e ->
		!Objects.equals(e, EntityPredicateEntry.EntityPredicate.DEAD) &&
		!Objects.equals(e, EntityPredicateEntry.EntityPredicate.ALIVE);
	
	public MobHarvester(BlockPos pos, BlockState blockState) {
		super(ModMachines.MOB_HARVESTER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.EXPERIENCE), true, false));
		outputSlots.addAll(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
		setInventory(() -> createBasicInventory(16, outputSlots, (slot, stack) -> slot == 0, s -> {
			//if (s == 0) FakePlayerHelper.getFakePlayer(this, getLevel());
		}));
		setEnergyPerTick(250);
		screenInfo.setShouldDrawArrow(false);
		screenInfo.setSlotPosition(46, 44, 0);
		
		for (int i = 0; i < 15; i++) {
			int x = i % 5;
			int y = i / 5;
			screenInfo.setSlotPosition(67 + x * 18, 19 + y * 18, i + 1);
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("EntityFilter", entityFilter.serializeNBT());
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("EntityFilter")) {
			entityFilter.deserializeNBT(tag.getCompound("EntityFilter"));
		}
	}
	
	@Override
	public void saveContents(CompoundTag additionalTag) {
		super.saveContents(additionalTag);
		additionalTag.put("EntityFilter", entityFilter.serializeNBT());
	}
	
	@Override
	public void loadContents(CompoundTag additionalTag) {
		super.loadContents(additionalTag);
		if (additionalTag.contains("EntityFilter")) {
			entityFilter.deserializeNBT(additionalTag.getCompound("EntityFilter"));
		}
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		if (!redstoneChecks(lvl)) return;
		
		FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, level);
		fakePlayer.setYRot(st.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite().toYRot());
		fakePlayer.setPosRaw(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
		ItemStack tool = inventory.getStackInSlot(0);
		fakePlayer.getInventory().selected = 0;
		fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, tool);
		for (int i = 1; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			fakePlayer.getInventory().setItem(i, stack);
		}
		fakePlayer.detectEquipmentUpdates();
		if (fakePlayer instanceof LivingEntityExposer exp) exp.incrementAttackStrengthTicker(1);
		if (canWork(energy) && fakePlayer.getAttackStrengthScale(0) == 1.0F) {
			List<LivingEntity> targets = lvl.getEntitiesOfClass(LivingEntity.class, getWorkingArea(getBlockPos()))
				.stream().filter(living ->
					!living.isSpectator() && !living.isDeadOrDying() && living.isAlive() && !living.isInvulnerable() &&
						entityFilter.matches(living) && fakePlayer.canAttack(living)
				).toList();
			if (!targets.isEmpty()) {
				spendEnergy(energy);
				fakePlayer.attack(targets.get(lvl.random.nextInt(targets.size())));
			}
		}
	}
	
	@Override
	public AABB getWorkingArea(BlockPos anchor) {
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		return new AABB(anchor.relative(facing.getOpposite(), 5)).inflate(4, 1, 4).move(0D, 1D, 0D);
	}
}
