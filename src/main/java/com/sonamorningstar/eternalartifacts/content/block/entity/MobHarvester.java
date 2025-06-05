package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.IAreaRenderer;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.LivingEntityExposer;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;

public class MobHarvester extends GenericMachine implements IAreaRenderer {
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
			screenInfo.setSlotPosition(64 + x * 18, 17 + y * 18, i + 1);
		}
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		if (!redstoneChecks(redstoneConfigs.get(0), lvl)) return;
		
		FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, level);
		fakePlayer.setYRot(st.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot());
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
			List<LivingEntity> targets = lvl.getEntitiesOfClass(LivingEntity.class, getWorkingArea(getBlockPos(), getBlockState()))
				.stream().filter(living ->
					!living.isSpectator() && !living.isDeadOrDying() &&
						living.isAlive() && !living.isInvulnerable() &&
						fakePlayer.canAttack(living)
				).toList();
			if (!targets.isEmpty()) {
				spendEnergy(energy);
				fakePlayer.attack(targets.get(lvl.random.nextInt(targets.size())));
			}
		}
	}
	
	private static AABB getWorkingArea(BlockPos pos, BlockState state) {
		Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		return new AABB(pos.relative(facing.getOpposite(), 2)).inflate(1).move(0D, 1D, 0D);
	}
	
	@Override
	public boolean shouldRender() {
		return false;
	}
	
	@Override
	public AABB getBoundingBox() {
		return getWorkingArea(getBlockPos(), getBlockState());
	}
}
