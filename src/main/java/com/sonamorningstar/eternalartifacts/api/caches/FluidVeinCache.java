package com.sonamorningstar.eternalartifacts.api.caches;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.HashSet;
import java.util.Set;

public class FluidVeinCache extends BlockVeinCache {
	public final Set<Fluid> takeableFluids = new HashSet<>();
	public final Set<TagKey<Fluid>> takeableFluidTags = new HashSet<>();
	public FluidVeinCache(Level level, BlockPos start, int range, boolean isReverse) {
		super(level, start, range, isReverse);
	}
	
	@Override
	protected boolean canMine(BlockPos pos) {
		FluidState state = level.getFluidState(pos);
		for (Fluid fluid : takeableFluids) {
			if (state.is(fluid)) return true;
		}
		for (TagKey<Fluid> tag : takeableFluidTags) {
			if (state.is(tag)) return true;
		}
		return false;
	}
}
