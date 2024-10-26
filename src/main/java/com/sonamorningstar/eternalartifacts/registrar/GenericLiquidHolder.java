package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class GenericLiquidHolder extends LiquidBlockFluidHolder<LiquidBlock>{
    public GenericLiquidHolder(DeferredHolder<FluidType, BaseFluidType> fluidType, DeferredHolder<Fluid, BaseFlowingFluid.Source> still, DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing, DeferredHolder<Item, BucketItem> bucket, DeferredHolder<Block, LiquidBlock> block, int tintColor) {
        super(fluidType, still, flowing, bucket, block, tintColor);
    }

    public static GenericLiquidHolder convert(LiquidBlockFluidHolder<LiquidBlock> child) {
        return new GenericLiquidHolder(child.getFluidTypeHolder(), child.getStillFluidHolder(), child.getFlowingFluidHolder(), child.getBucketItemHolder(), child.getFluidBlockHolder(), child.getTintColor());
    }
}
