package com.sonamorningstar.eternalartifacts.registrar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;

/**
 * Holds all registered components for a fluid.
 * Provides type-safe access to fluid type, source, flowing, bucket, and block.
 */
@RequiredArgsConstructor
@Getter
public class FluidHolder<B extends LiquidBlock> implements ItemLike {
    private final DeferredHolder<FluidType, ? extends FluidType> fluidTypeHolder;
    private final DeferredHolder<Fluid, BaseFlowingFluid.Source> sourceHolder;
    private final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingHolder;
    @Nullable
    private final DeferredHolder<Item, BucketItem> bucketHolder;
    @Nullable
    private final DeferredHolder<Block, B> blockHolder;
    private final int tintColor;
    private final boolean genericTexture;

    public FluidType getFluidType() {
        return fluidTypeHolder.get();
    }

    public BaseFlowingFluid.Source getSource() {
        return sourceHolder.get();
    }

    public BaseFlowingFluid.Source getStillFluid() {
        return getSource();
    }

    public Fluid getFluid() {
        return getSource();
    }

    public BaseFlowingFluid.Flowing getFlowing() {
        return flowingHolder.get();
    }

    public BaseFlowingFluid.Flowing getFlowingFluid() {
        return getFlowing();
    }

    @Nullable
    public BucketItem getBucket() {
        return bucketHolder != null ? bucketHolder.get() : null;
    }

    @Nullable
    public BucketItem getBucketItem() {
        return getBucket();
    }

    @Nullable
    public B getBlock() {
        return blockHolder != null ? blockHolder.get() : null;
    }

    @Nullable
    public B getFluidBlock() {
        return getBlock();
    }

    public ResourceLocation getRegistryName() {
        return BuiltInRegistries.FLUID.getKey(getFluid());
    }

    public String getTranslationKey() {
        return getFluidType().getDescriptionId();
    }

    public FluidStack getFluidStack(int amount) {
        return new FluidStack(getFluid(), amount);
    }

    public boolean hasBucket() {
        return bucketHolder != null;
    }

    public boolean hasBlock() {
        return blockHolder != null;
    }

    @Override
    public Item asItem() {
        return getBucket();
    }
}
