package com.sonamorningstar.eternalartifacts.registrar;

import lombok.Getter;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import org.joml.Vector3f;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Represents a fluid registration configuration.
 * Use {@link FluidRegistry} to create instances.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Simple fluid with generic texture
 * FluidHolder<LiquidBlock> WATER_LIKE = REGISTRY.register(
 *     FluidRegistration.create("my_fluid")
 *         .density(1000)
 *         .viscosity(1000)
 *         .color(0, 100, 200)
 *         .mapColor(MapColor.WATER)
 *         .genericTexture()
 *         .build()
 * );
 *
 * // Custom fluid with unique texture
 * FluidHolder<LiquidBlock> OIL = REGISTRY.register(
 *     FluidRegistration.create("oil")
 *         .density(800)
 *         .viscosity(1500)
 *         .rarity(Rarity.UNCOMMON)
 *         .color(30, 20, 10)
 *         .tint(0xFF1E140A)
 *         .mapColor(MapColor.COLOR_BLACK)
 *         .build()
 * );
 *
 * // Custom liquid block
 * FluidHolder<MyLiquidBlock> SLIME = REGISTRY.register(
 *     FluidRegistration.create("slime")
 *         .block(MyLiquidBlock::new)
 *         .density(2000)
 *         .viscosity(5000)
 *         .build()
 * );
 * }</pre>
 */
@Getter
public class FluidRegistration<B extends LiquidBlock> {
    private final String name;
    private final BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, B> blockFactory;
    private final int lightLevel;
    private final int density;
    private final int viscosity;
    private final Rarity rarity;
    private final Vector3f fogColor;
    private final MapColor mapColor;
    private final boolean useGenericTexture;
    private final int tintColor;
    private final boolean isPotion;
    private final boolean hasBucket;
    private final boolean hasBlock;

    private FluidRegistration(Builder<B> builder) {
        this.name = builder.name;
        this.blockFactory = builder.blockFactory;
        this.lightLevel = builder.lightLevel;
        this.density = builder.density;
        this.viscosity = builder.viscosity;
        this.rarity = builder.rarity;
        this.fogColor = new Vector3f(builder.fogR / 255f, builder.fogG / 255f, builder.fogB / 255f);
        this.mapColor = builder.mapColor;
        this.useGenericTexture = builder.useGenericTexture;
        this.tintColor = builder.tintColor;
        this.isPotion = builder.isPotion;
        this.hasBucket = builder.hasBucket;
        this.hasBlock = builder.hasBlock;
    }

    public static Builder<LiquidBlock> create(String name) {
        return new Builder<>(name);
    }

    public static Builder<LiquidBlock> potion(String name) {
        return new Builder<LiquidBlock>(name)
                .genericTexture()
                .rarity(Rarity.UNCOMMON)
                .noBucket()
                .noBlock()
                .potion();
    }

    public static class Builder<B extends LiquidBlock> {
        private final String name;
        private BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, B> blockFactory;
        private int lightLevel = 0;
        private int density = 1000;
        private int viscosity = 1000;
        private Rarity rarity = Rarity.COMMON;
        private int fogR = 255, fogG = 255, fogB = 255;
        private MapColor mapColor = MapColor.WATER;
        private boolean useGenericTexture = false;
        private int tintColor = 0xFFFFFFFF;
        private boolean isPotion = false;
        private boolean hasBucket = true;
        private boolean hasBlock = true;

        @SuppressWarnings("unchecked")
        public Builder(String name) {
            this.name = name;
            this.blockFactory = (fluid, props) -> (B) new LiquidBlock(fluid, props);
        }

        public Builder<B> light(int level) {
            this.lightLevel = level;
            return this;
        }

        public Builder<B> density(int density) {
            this.density = density;
            return this;
        }

        public Builder<B> viscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        public Builder<B> rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder<B> color(int r, int g, int b) {
            this.fogR = r;
            this.fogG = g;
            this.fogB = b;
            return this;
        }

        public Builder<B> fogColor(int r, int g, int b) {
            return color(r, g, b);
        }

        public Builder<B> mapColor(MapColor mapColor) {
            this.mapColor = mapColor;
            return this;
        }

        public Builder<B> genericTexture() {
            this.useGenericTexture = true;
            return this;
        }

        public Builder<B> tint(int tintColor) {
            this.tintColor = tintColor;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <NB extends LiquidBlock> Builder<NB> block(BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, NB> blockFactory) {
            Builder<NB> newBuilder = (Builder<NB>) this;
            newBuilder.blockFactory = blockFactory;
            return newBuilder;
        }

        private Builder<B> potion() {
            this.isPotion = true;
            return this;
        }

        public Builder<B> noBucket() {
            this.hasBucket = false;
            return this;
        }

        public Builder<B> noBlock() {
            this.hasBlock = false;
            return this;
        }

        public FluidRegistration<B> build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalStateException("Fluid name is required");
            }
            return new FluidRegistration<>(this);
        }
    }
}

