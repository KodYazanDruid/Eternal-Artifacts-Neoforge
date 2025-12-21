package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.caches.FluidVeinCache;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FluidPump extends GenericMachine {
	private FluidVeinCache veinCache = null;
	/*private Queue<BlockPos> savedCacheQueue = null;
	private List<FluidKey> allowedFluids = null;*/
	
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
		/*if (veinCache != null) {
			ListTag listTag = new ListTag();
			for (BlockPos pos : veinCache.getCache()) {
				listTag.add(LongTag.valueOf(pos.asLong()));
			}
			tag.put("VeinCache", listTag);
		}
		if (allowedFluids != null) {
			ListTag listTag = new ListTag();
			for (FluidKey key : allowedFluids) {
				listTag.add(key.serialize());
			}
			tag.put("AllowedFluids", listTag);
		}*/
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		/*if (tag.contains("VeinCache", Tag.TAG_LIST)) {
			Queue<BlockPos> queue = new LinkedList<>();
			for (Tag t : tag.getList("VeinCache", Tag.TAG_LONG)) {
				if (t instanceof LongTag longTag) {
					queue.add(BlockPos.of(longTag.getAsLong()));
				}
			}
			savedCacheQueue = queue;
		}
		if (tag.contains("AllowedFluids", Tag.TAG_LIST)) {
			List<FluidKey> keys = new LinkedList<>();
			for (Tag t : tag.getList("AllowedFluids", Tag.TAG_COMPOUND)) {
				if (t instanceof CompoundTag compoundTag) {
					keys.add(FluidKey.deserialize(compoundTag));
				}
			}
			allowedFluids = keys;
		}*/
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		createVeinCacheIfNeeded(lvl, pos);
		
		/*if (veinCache != null && savedCacheQueue != null) {
			veinCache.setCache(savedCacheQueue);
			savedCacheQueue = null;
		}*/
		
		if (veinCache != null && canWork(energy)) {
			for (int i = 0; i < 1 + getEnchantmentLevel(ModEnchantments.CELERITY.get()); i++) {
				if (veinCache.getCache().isEmpty()) {
					veinCache = null;
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
						}
					}
					return false;
				});
			}
		}
		
	}
	
	private void createVeinCacheIfNeeded(Level lvl, BlockPos pos) {
		if (veinCache == null) {
			/*if (savedCacheQueue != null && allowedFluids != null) {
				veinCache = new FluidVeinCache(lvl, pos.below(), 25, true);
				for (FluidKey key : allowedFluids) {
					key.key.ifLeft(fluid -> veinCache.takeableFluids.add(fluid));
					key.key.ifRight(fluidTag -> veinCache.takeableFluidTags.add(fluidTag));
				}
				veinCache.setCache(savedCacheQueue);
			} else*/ {
				BlockState belowState = lvl.getBlockState(pos.below());
				if (belowState.getFluidState().isEmpty()) return;
				veinCache = new FluidVeinCache(lvl, pos.below(), 25, true);
				veinCache.takeableFluids.add(belowState.getFluidState().getType());
				veinCache.scanForBlocks();
			}
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
