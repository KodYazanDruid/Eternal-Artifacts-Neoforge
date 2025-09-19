package com.sonamorningstar.eternalartifacts.registrar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

@RequiredArgsConstructor
public class FluidDeferredHolder<T extends FluidType, S extends Fluid, F extends Fluid, BI extends BucketItem, B extends LiquidBlock> {
    private final DeferredHolder<FluidType, T> fluidType;
    private final DeferredHolder<Fluid, S> still;
    private final DeferredHolder<Fluid, F> flowing;
    private final DeferredHolder<Item, BI> bucket;
    private final DeferredHolder<Block, B> block;
    @Getter
    private final int tintColor;

    public DeferredHolder<FluidType, T> getFluidTypeHolder() {return fluidType;}
    public T getFluidType() {return fluidType.get();}
    public DeferredHolder<Fluid, S> getStillFluidHolder() {return still;}
    public S getStillFluid() {return still == null ? null : still.get();}
    public DeferredHolder<Fluid, F> getFlowingFluidHolder() {return flowing;}
    public F getFlowingFluid() {return flowing == null ? null : flowing.get();}
    public DeferredHolder<Item, BI> getBucketItemHolder() {return bucket;}
    public BI getBucketItem() {return bucket == null ? null : bucket.get();}
    public DeferredHolder<Block, B> getFluidBlockHolder() {return block;}
    public B getFluidBlock() {return block == null ? null : block.get();}
    public S getFluid() {return getStillFluid();}

    public ResourceLocation getRegistryName() {
        return BuiltInRegistries.FLUID.getKey(getFluid());
    }

    public String getTranslationKey() {
        return getFluidType().getDescriptionId();
    }

    public FluidStack getFluidStack(int size) {
        return new FluidStack(getFluid(), size);
    }

}
