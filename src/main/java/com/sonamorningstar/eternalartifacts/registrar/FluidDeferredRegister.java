package com.sonamorningstar.eternalartifacts.registrar;

import com.google.common.collect.ImmutableList;
import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class FluidDeferredRegister{
    private final DeferredRegister<FluidType> fluidTypeRegister;
    private final DeferredRegister<Fluid> fluidRegister;
    private final DeferredRegister.Blocks blockRegister;
    private final DeferredRegister.Items itemRegister;
    private final Map<FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, ? extends LiquidBlock>, Boolean> entryMap = new HashMap<>();

    private static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");
    private static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
    private static final ResourceLocation WATER_RENDER_OVERLAY = new ResourceLocation("misc/underwater");
    private static final ResourceLocation GENERIC_LIQUID_STILL = new ResourceLocation(MODID, "block/generic_liquid_still");
    private static final ResourceLocation GENERIC_LIQUID_FLOW = new ResourceLocation(MODID, "block/generic_liquid_flow");

    public FluidDeferredRegister(String modid) {
        this.fluidTypeRegister = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, modid);
        this.fluidRegister = DeferredRegister.create(Registries.FLUID, modid);
        this.blockRegister = DeferredRegister.createBlocks(modid);
        this.itemRegister = DeferredRegister.createItems(modid);
    }

    private static ResourceLocation getStillTexture(String name) {return new ResourceLocation(MODID, "block/"+name+"_still");}
    private static ResourceLocation getFlowTexture(String name) {return new ResourceLocation(MODID, "block/"+name+"_flow");}

    public FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, LiquidBlock> register(
            String name, int light, int density, int viscosity, Rarity rarity,
            int fogX, int fogY, int fogZ, MapColor mapColor, boolean isGeneric, int... tint) {
        return register(name, LiquidBlock::new, light, density, viscosity, rarity, fogX, fogY, fogZ, mapColor, isGeneric, tint);
    }

    public <B extends LiquidBlock> FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, B> register(
            String name, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, B> liquidBlockFun, int light, int density, int viscosity, Rarity rarity,
            int fogX, int fogY, int fogZ, MapColor mapColor, boolean isGeneric, int... tint) {

        ResourceLocation stillTex = isGeneric ? WATER_STILL : getStillTexture(name);
        ResourceLocation flowTex = isGeneric ? WATER_FLOW : getFlowTexture(name);
        int tintColor = tint.length > 0 ? tint[0] : 0xFFFFFFFF;

        DeferredHolder<FluidType, BaseFluidType> fluidType = fluidTypeRegister.register(name, ()->
                new BaseFluidType(stillTex, flowTex, WATER_RENDER_OVERLAY, tintColor, new Vector3f((float) fogX / 255, (float) fogY / 255, (float) fogZ / 255),
                    FluidType.Properties.create().lightLevel(light).density(density).viscosity(viscosity).rarity(rarity).canExtinguish(true).canSwim(true)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY))
        );

        ResourceLocation baseKey = new ResourceLocation(fluidRegister.getNamespace(), name);
        BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(fluidType, DeferredHolder.create(Registries.FLUID, baseKey), DeferredHolder.create(Registries.FLUID, baseKey.withSuffix("_flow")))
                .bucket(DeferredHolder.create(Registries.ITEM, baseKey.withSuffix("_bucket")))
                .block(DeferredHolder.create(Registries.BLOCK, baseKey));

        DeferredHolder<Fluid, BaseFlowingFluid.Source> source = fluidRegister.register(name,()-> new BaseFlowingFluid.Source(fluidProperties));
        DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing = fluidRegister.register(name+"_flow",()-> new BaseFlowingFluid.Flowing(fluidProperties));

        DeferredItem<BucketItem> bucket = itemRegister.register(name+"_bucket", ()-> new BucketItem(source, new Item.Properties().stacksTo(1)));
        //DeferredBlock<LiquidBlock> liquidBlock = blockRegister.register(name, ()-> new LiquidBlock(source, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(mapColor)));
        DeferredBlock<B> liquidBlock = blockRegister.register(name, ()-> liquidBlockFun.apply(source, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(mapColor)));

        FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, B> holder = new FluidDeferredHolder<>(fluidType, source, flowing, bucket, liquidBlock, tintColor);
        entryMap.put(holder, isGeneric);
        return holder;
    }

    public void register(IEventBus bus) {
        this.blockRegister.register(bus);
        this.fluidRegister.register(bus);
        this.fluidTypeRegister.register(bus);
        this.itemRegister.register(bus);
    }

    public boolean isGeneric(FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem,? extends LiquidBlock> holder) {
        return entryMap.get(holder);
    }

    public Collection<DeferredHolder<FluidType, ? extends FluidType>> getFluidTypeEntries() {
        return this.fluidTypeRegister.getEntries();
    }

    public void forEachFluidTypeEntry(Consumer<DeferredHolder<FluidType, ? extends FluidType>> consumer) {
        for(DeferredHolder<FluidType, ? extends FluidType> holder : getFluidTypeEntries()) {
            consumer.accept(holder);
        }
    }

    public Collection<DeferredHolder<Fluid, ? extends Fluid>> getFluidEntries() {
        return this.fluidRegister.getEntries();
    }

    public void forEachFluidEntry(Consumer<DeferredHolder<Fluid, ? extends Fluid>> consumer) {
        for(DeferredHolder<Fluid, ? extends Fluid> holder : getFluidEntries()) {
            consumer.accept(holder);
        }
    }

    public Collection<DeferredHolder<Block, ? extends Block>> getBlockEntries() {
        return this.blockRegister.getEntries();
    }

    public void forEachBlockEntry(Consumer<DeferredHolder<Block, ? extends Block>> consumer) {
        for(DeferredHolder<Block, ? extends Block> holder : getBlockEntries()) {
            consumer.accept(holder);
        }
    }

    public Collection<DeferredHolder<Item, ? extends Item>> getBucketEntries() {
        return this.itemRegister.getEntries();
    }

    public void forEachBucketEntry(Consumer<DeferredHolder<Item, ? extends Item>> consumer) {
        for(DeferredHolder<Item, ? extends Item> holder : getBucketEntries()) {
            consumer.accept(holder);
        }
    }

    public Set<FluidDeferredHolder<BaseFluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, BucketItem, ? extends LiquidBlock>> getEntries() {
        return Collections.unmodifiableSet(entryMap.keySet());
    }

}
