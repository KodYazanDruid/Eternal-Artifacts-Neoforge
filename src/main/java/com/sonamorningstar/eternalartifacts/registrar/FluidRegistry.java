package com.sonamorningstar.eternalartifacts.registrar;
import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import com.sonamorningstar.eternalartifacts.content.fluid.PotionFluidType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simplified fluid registry using builder pattern.
 * <p>Example usage:</p>
 * <pre>{@code
 * public static final FluidRegistry FLUIDS = new FluidRegistry(MODID);
 *
 * // Simple registration with builder
 * public static final FluidHolder<LiquidBlock> NOUS = FLUIDS.register(
 *     FluidRegistration.create("nous")
 *         .light(7)
 *         .density(3000)
 *         .viscosity(4500)
 *         .rarity(Rarity.EPIC)
 *         .color(38, 178, 82)
 *         .mapColor(MapColor.COLOR_LIGHT_GREEN)
 *         .build()
 * );
 *
 * // Shorthand for simple fluids
 * public static final FluidHolder<LiquidBlock> WATER_LIKE = FLUIDS.register("water_like",
 *     b -> b.density(1000).viscosity(1000).color(0, 100, 255).genericTexture());
 *
 * // Potion fluid
 * public static final FluidHolder<LiquidBlock> POTION = FLUIDS.registerPotion("potion");
 * }</pre>
 */
public class FluidRegistry {
    private final String modId;
    private final DeferredRegister<FluidType> fluidTypeRegister;
    private final DeferredRegister<Fluid> fluidRegister;
    private final DeferredRegister.Blocks blockRegister;
    private final DeferredRegister.Items itemRegister;
    
    private final List<FluidHolder<?>> allFluids = new ArrayList<>();
    
    private static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");
    private static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
    private static final ResourceLocation WATER_RENDER_OVERLAY = new ResourceLocation("misc/underwater");
    
    public FluidRegistry(String modId) {
        this.modId = modId;
        this.fluidTypeRegister = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, modId);
        this.fluidRegister = DeferredRegister.create(Registries.FLUID, modId);
        this.blockRegister = DeferredRegister.createBlocks(modId);
        this.itemRegister = DeferredRegister.createItems(modId);
    }
    
    public void register(IEventBus bus) {
        fluidTypeRegister.register(bus);
        fluidRegister.register(bus);
        blockRegister.register(bus);
        itemRegister.register(bus);
    }
    
    private ResourceLocation getStillTexture(String name) {
        return new ResourceLocation(modId, "block/" + name + "_still");
    }
    
    private ResourceLocation getFlowTexture(String name) {
        return new ResourceLocation(modId, "block/" + name + "_flow");
    }
    
    /**
     * Registers a fluid using the provided registration configuration.
     */
    public <B extends LiquidBlock> FluidHolder<B> register(FluidRegistration<B> registration) {
        String name = registration.getName();
        ResourceLocation baseKey = new ResourceLocation(modId, name);
        
        ResourceLocation stillTex = registration.isUseGenericTexture() ? WATER_STILL : getStillTexture(name);
        ResourceLocation flowTex = registration.isUseGenericTexture() ? WATER_FLOW : getFlowTexture(name);
        
        // FluidType registration
        DeferredHolder<FluidType, ? extends FluidType> fluidTypeHolder;
        if (registration.isPotion()) {
            fluidTypeHolder = fluidTypeRegister.register(name, () ->
                new PotionFluidType(WATER_STILL, WATER_FLOW, WATER_RENDER_OVERLAY,
                    FluidType.Properties.create()
                        .lightLevel(registration.getLightLevel())
                        .density(registration.getDensity())
                        .viscosity(registration.getViscosity())
                        .rarity(registration.getRarity())
                        .canExtinguish(true)
                        .canSwim(true)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)
                )
            );
        } else {
            fluidTypeHolder = fluidTypeRegister.register(name, () ->
                new BaseFluidType(stillTex, flowTex, WATER_RENDER_OVERLAY, registration.getTintColor(),
                    registration.getFogColor(),
                    FluidType.Properties.create()
                        .lightLevel(registration.getLightLevel())
                        .density(registration.getDensity())
                        .viscosity(registration.getViscosity())
                        .rarity(registration.getRarity())
                        .canExtinguish(true)
                        .canSwim(true)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)
                )
            );
        }
        
        // Fluid properties
        DeferredHolder<Fluid, BaseFlowingFluid.Source> sourceRef = DeferredHolder.create(Registries.FLUID, baseKey);
        DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingRef = DeferredHolder.create(Registries.FLUID, baseKey.withSuffix("_flow"));
        
        BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(
            fluidTypeHolder, sourceRef, flowingRef
        );
        
        // Bucket registration (optional)
        DeferredHolder<Item, BucketItem> bucketHolder = null;
        if (registration.isHasBucket()) {
            bucketHolder = itemRegister.register(name + "_bucket", () ->
                new BucketItem(sourceRef, new Item.Properties().stacksTo(1))
            );
            fluidProperties.bucket(DeferredHolder.create(Registries.ITEM, baseKey.withSuffix("_bucket")));
        }
        
        // Block registration (optional)
        DeferredHolder<Block, B> blockHolder = null;
        if (registration.isHasBlock()) {
            blockHolder = blockRegister.register(name, () ->
                registration.getBlockFactory().apply(sourceRef,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(registration.getMapColor()))
            );
            fluidProperties.block(DeferredHolder.create(Registries.BLOCK, baseKey));
        }
        
        // Fluid registration
        DeferredHolder<Fluid, BaseFlowingFluid.Source> sourceHolder = fluidRegister.register(name, () ->
            new BaseFlowingFluid.Source(fluidProperties)
        );
        DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingHolder = fluidRegister.register(name + "_flow", () ->
            new BaseFlowingFluid.Flowing(fluidProperties)
        );
        
        FluidHolder<B> holder = new FluidHolder<>(
            fluidTypeHolder, sourceHolder, flowingHolder,
            bucketHolder, blockHolder,
            registration.getTintColor(),
            registration.isUseGenericTexture()
        );
        
        allFluids.add(holder);
        return holder;
    }
    
    /**
     * Shorthand for registering a fluid with a builder consumer.
     */
    public FluidHolder<LiquidBlock> register(String name, Consumer<FluidRegistration.Builder<LiquidBlock>> builderConsumer) {
        FluidRegistration.Builder<LiquidBlock> builder = FluidRegistration.create(name);
        builderConsumer.accept(builder);
        return register(builder.build());
    }
    
    /**
     * Registers a potion-type fluid (no bucket, no block, generic texture).
     */
    public FluidHolder<LiquidBlock> registerPotion(String name, int lightLevel, int density, int viscosity) {
        return register(FluidRegistration.potion(name)
            .light(lightLevel)
            .density(density)
            .viscosity(viscosity)
            .build()
        );
    }
    
    public List<FluidHolder<?>> getFluids() {
        return Collections.unmodifiableList(allFluids);
    }
    
    public boolean isGenericTexture(FluidHolder<?> holder) {
        return holder.isGenericTexture();
    }
    
    public Collection<DeferredHolder<FluidType, ? extends FluidType>> getFluidTypeEntries() {
        return fluidTypeRegister.getEntries();
    }
    
    public void forEachFluidTypeEntry(Consumer<DeferredHolder<FluidType, ? extends FluidType>> consumer) {
        getFluidTypeEntries().forEach(consumer);
    }
    
    public Collection<DeferredHolder<Fluid, ? extends Fluid>> getFluidEntries() {
        return fluidRegister.getEntries();
    }
    
    public void forEachFluidEntry(Consumer<DeferredHolder<Fluid, ? extends Fluid>> consumer) {
        getFluidEntries().forEach(consumer);
    }
    
    public Collection<DeferredHolder<Block, ? extends Block>> getBlockEntries() {
        return blockRegister.getEntries();
    }
    
    public void forEachBlockEntry(Consumer<DeferredHolder<Block, ? extends Block>> consumer) {
        getBlockEntries().forEach(consumer);
    }
    
    public Collection<DeferredHolder<Item, ? extends Item>> getItemEntries() {
        return itemRegister.getEntries();
    }
    
}