package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.caches.FluidVeinCache;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class FluidPump extends GenericMachine {
	@Nullable
	public FluidVeinCache veinCache = null;
	public int veinSize = 0;
	
	public FluidPump(BlockPos pos, BlockState blockState) {
		super(ModMachines.FLUID_PUMP, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(50_000, true, false));
		fluidTransferRate = 50_000;
		screenInfo.setShouldDrawArrow(false);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("VeinSize", veinSize);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		veinSize = tag.getInt("VeinSize");
	}
	
	@Override
	public void saveContents(CompoundTag additionalTag) {
		super.saveContents(additionalTag);
		additionalTag.putInt("VeinSize", veinSize);
	}
	
	@Override
	public void loadContents(CompoundTag additionalTag) {
		super.loadContents(additionalTag);
		veinSize = additionalTag.getInt("VeinSize");
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		createVeinCacheIfNeeded(lvl, pos);
		if (!redstoneChecks(lvl)) return;
		
		if (veinCache != null && canWork(energy)) {
			for (int i = 0; i < 1 + getEnchantmentLevel(ModEnchantments.CELERITY.get()); i++) {
				if (veinCache.getCache().isEmpty()) {
					veinCache = null;
					veinSize = 0;
					break;
				}
				veinCache.mine(veinCache.getCache(), p -> {
					FluidState fluidState = lvl.getFluidState(p);
					if (fluidState.isEmpty() || !fluidState.isSource()) return false;
					FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
					int filled = tank.fillForced(fluidStack, IFluidHandler.FluidAction.SIMULATE);
					if (filled == 1000) {
						tank.fillForced(fluidStack, IFluidHandler.FluidAction.EXECUTE);
						BlockState targetState = lvl.getBlockState(p);
						Block targetBlock = targetState.getBlock();
						boolean picked = pickFluid(targetBlock, targetState, p, lvl);
						veinSize = veinCache.getCache().size();
						if (picked) sendUpdate();
						return picked;
					}
					return false;
				});
			}
		}
		
		if (veinCache == null || veinCache.getCache().isEmpty()) {
			veinSize = 0;
			sendUpdate();
		}
	}
	
	private boolean pickFluid(Block targetBlock, BlockState targetState, BlockPos p, Level lvl) {
		if (targetBlock instanceof LiquidBlock) {
			lvl.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
			spendEnergy(energy);
			return true;
		} else if (targetBlock instanceof SimpleWaterloggedBlock && targetState.hasProperty(BlockStateProperties.WATERLOGGED)) {
			lvl.setBlock(p, targetState.setValue(BlockStateProperties.WATERLOGGED, false), 3);
			if (!targetState.canSurvive(lvl, p)) {
				lvl.destroyBlock(p, true);
			}
			spendEnergy(energy);
			return true;
		} else if (targetBlock instanceof BucketPickup bucketPickup) {
			bucketPickup.pickupBlock(null, lvl, p, targetState);
			spendEnergy(energy);
			return true;
		} else if (targetBlock instanceof LiquidBlockContainer) {
			lvl.destroyBlock(p, true);
			lvl.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
			spendEnergy(energy);
			return true;
		}
		return false;
	}
	
	private void createVeinCacheIfNeeded(Level lvl, BlockPos pos) {
		if (veinCache == null) {
			BlockState belowState = lvl.getBlockState(pos.below());
			if (belowState.getFluidState().isEmpty()) {
				veinSize = 0;
				sendUpdate();
				return;
			}
			veinCache = new FluidVeinCache(lvl, pos.below(), 25, true);
			veinCache.takeableFluids.add(belowState.getFluidState().getType());
			veinCache.scanForBlocks();
			veinSize = veinCache.getCache().size();
			sendUpdate();
		}
	}
	
	private static class FluidKey {
		private final Either<Fluid, TagKey<Fluid>> key;
		
		public FluidKey(Fluid fluid) {
			this.key = Either.left(fluid);
		}
		
		public FluidKey(TagKey<Fluid> fluidTag) {
			this.key = Either.right(fluidTag);
		}
		
		public boolean matches(Fluid fluid) {
			return key.map(
				f -> f == fluid,
				fluid::is
			);
		}
		
		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			key.ifLeft(fluid -> tag.putString("Fluid", BuiltInRegistries.FLUID.getKey(fluid).toString()));
			key.ifRight(fluidTag -> tag.putString("FluidTag", fluidTag.location().toString()));
			return tag;
		}
		
		public static FluidKey deserialize(CompoundTag tag) {
			if (tag.contains("Fluid")) {
				Fluid fluid = BuiltInRegistries.FLUID.getOptional(new ResourceLocation(tag.getString("Fluid"))).orElse(Fluids.EMPTY);
				return new FluidKey(fluid);
			} else if (tag.contains("FluidTag")) {
				TagKey<Fluid> fluidTag = TagKey.create(BuiltInRegistries.FLUID.key(), new ResourceLocation(tag.getString("FluidTag")));
				return new FluidKey(fluidTag);
			}
			throw new IllegalArgumentException("Invalid FluidKey tag");
		}
	}
}
