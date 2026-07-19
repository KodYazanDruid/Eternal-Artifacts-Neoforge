package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.TickableClient;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.RelativeBlockPos;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class PumpjackBlockEntity extends AbstractMultiblockBlockEntity implements TickableClient {

	public PumpjackBlockEntity(BlockPos pos, BlockState state) {
		super(ModMultiblocks.PUMPJACK.getBlockEntity(), pos, state, ModMultiblocks.PUMPJACK.getMultiblock());
		setEnergy(() -> createBasicEnergy(200000, 2500, true, false));
		setTank(() -> new ModFluidStorage(50000, fs -> fs.is(ModTags.Fluids.CRUDE_OIL)));
		setEnergyPerTick(250);
	}
	
	@Override
	public boolean shouldSyncWorkingState() {
		return true;
	}
	
	@Override
	public boolean onDeform(Level level, BlockPos masterPos, RelativeBlockPos relativePos) {
		BlockPos deformedPos = masterPos.offset(relativePos.x(), relativePos.y(), relativePos.z());
		((ServerLevel) level).sendParticles(
			ParticleTypes.FLAME, deformedPos.getX() + 0.5, deformedPos.getY() + 1, deformedPos.getZ() + 0.5,
			5, 0, 0, 0, 0
		);
		return super.onDeform(level, masterPos, relativePos);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putFloat("ArmAngle", armAngle);
		tag.putFloat("AnimPhase", animPhase);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		armAngle = tag.getFloat("ArmAngle");
		prevArmAngle = armAngle;
		animPhase = tag.getFloat("AnimPhase");
	}
	
	private float animPhase = 0.0F;
	@Getter
	private float prevArmAngle = 0.0F;
	@Getter
	private float armAngle = 0.0F;
	
	private static final float ANIMATION_SPEED = 0.07F;
	private static final float MAX_STEP_PER_TICK = 3.0F;
	public static final float MAX_ANGLE = 22.5F;
	
	private void updateArmAnimation() {
		prevArmAngle = armAngle;
		
		float target;
		if (isWorking()) {
			animPhase += ANIMATION_SPEED;
			float progress = (Mth.sin(animPhase) + 1.0F) * 0.5F;
			progress = progress * progress * (3.0F - 2.0F * progress);
			target = Mth.lerp(progress, 0.0F, MAX_ANGLE);
		} else {
			target = 0.0F;
			animPhase = 0.0F;
		}
		
		float diff = target - armAngle;
		if (Math.abs(diff) <= MAX_STEP_PER_TICK) {
			armAngle = target;
		} else {
			armAngle += Math.signum(diff) * MAX_STEP_PER_TICK;
		}
	}
	
	@Override
	public void tickClient(ClientLevel lvl, BlockPos pos, BlockState st) {
		if (!isMaster()) return;
		updateArmAnimation();
	}
	
	@Override
	public void tickMaster(ServerLevel lvl, BlockPos pos, BlockState st) {
		Holder<Biome> biome = lvl.getBiome(pos);
		if (canWork(energy) && (biome.is(BiomeTags.IS_DEEP_OCEAN) || biome.is(Tags.Biomes.IS_DESERT))) {
			FluidStack oil = ModFluids.CRUDE_OIL.getFluidStack(20);
			int inserted = tank.fillForced(oil, IFluidHandler.FluidAction.SIMULATE);
			if (inserted == oil.getAmount()) {
				setWorking(true);
				tank.fillForced(oil, IFluidHandler.FluidAction.EXECUTE);
				spendEnergy(energy);
			} else setWorking(false);
		} else setWorking(false);
		updateArmAnimation();
	}
}
