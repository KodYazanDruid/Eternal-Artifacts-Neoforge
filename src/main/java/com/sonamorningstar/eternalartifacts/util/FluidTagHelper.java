package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.List;

public class FluidTagHelper {
    public static List<FluidStack> getMatchingFluidStacks(TagKey<Fluid> tag, int amount) {
        List<FluidStack> fluids = new ArrayList<>();
        if(tag != null) {
            fluids = BuiltInRegistries.FLUID.getTag(tag)
                    .stream()
                    .flatMap(HolderSet::stream)
                    .map(fluid -> new FluidStack(fluid, amount)).toList();

        }
        return fluids.isEmpty() ? List.of(FluidStack.EMPTY) : fluids;
    }
}
