package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineSixWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import com.sonamorningstar.eternalartifacts.util.function.MenuConstructor;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Represents a complete machine registration with all its components.
 * Use {@link MachineRegistry} to create instances.
 */
@Getter
public class MachineRegistration<M extends AbstractMachineMenu, BE extends Machine<M>, B extends BaseMachineBlock<BE>, I extends BlockItem> {
    private final String name;
    private final MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menuFactory;
    private final BlockEntityType.BlockEntitySupplier<BE> blockEntityFactory;
    private final BiFunction<BlockBehaviour.Properties, BlockEntityType.BlockEntitySupplier<BE>, B> blockFactory;
    private final BiFunction<Block, Item.Properties, I> itemFactory;
    private final int dataSize;
    private final boolean hasUniqueTexture;
    private final boolean hasCustomRender;
    private final boolean isGeneric;
    private final Map<CapabilityType, Object> customCapabilities;
    private final List<ExtraCapabilityRegistrar<BE, I>> extraCapabilityRegistrars;

    private MachineRegistration(Builder<M, BE, B, I> builder) {
        this.name = builder.name;
        this.menuFactory = builder.menuFactory;
        this.blockEntityFactory = builder.blockEntityFactory;
        this.blockFactory = builder.blockFactory;
        this.itemFactory = builder.itemFactory;
        this.dataSize = builder.dataSize;
        this.hasUniqueTexture = builder.hasUniqueTexture;
        this.hasCustomRender = builder.hasCustomRender;
        this.isGeneric = builder.isGeneric;
        this.customCapabilities = new EnumMap<>(builder.customCapabilities);
        this.extraCapabilityRegistrars = new ArrayList<>(builder.extraCapabilityRegistrars);
    }
    
    /**
     * Functional interface for registering extra capabilities from other mods.
     * Used for compat with Mekanism, Create, etc.
     *
     * @param <BE> BlockEntity type
     * @param <I> Item type
     */
    @FunctionalInterface
    public interface ExtraCapabilityRegistrar<BE extends BlockEntity, I extends Item> {
        /**
         * Register extra capabilities for this machine.
         *
         * @param context Contains event, block entity type, and item for registration
         */
        void register(ExtraCapabilityContext<BE, I> context);
    }
    
    /**
     * Context object passed to extra capability registrars.
     */
    public record ExtraCapabilityContext<BE extends BlockEntity, I extends Item>(
        net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event,
        BlockEntityType<BE> blockEntityType,
        I item
    ) {}
    /**
     * Capability types that can be customized per machine.
     */
    public enum CapabilityType {
        BLOCK_ITEM,
        BLOCK_FLUID,
        BLOCK_ENERGY,
        ITEM_ITEM,
        ITEM_FLUID,
        ITEM_ENERGY
    }
    
    /**
     * Functional interface for block capability providers.
     */
    @FunctionalInterface
    public interface BlockCapabilityProvider<BE extends BlockEntity, T> {
        @Nullable T provide(BE blockEntity, @Nullable Direction direction);
    }
    
    /**
     * Functional interface for item capability providers.
     */
    @FunctionalInterface
    public interface ItemCapabilityProvider<T> {
        @Nullable T provide(ItemStack stack, Void context);
    }
    
    @SuppressWarnings("unchecked")
    public <T> BlockCapabilityProvider<BE, T> getBlockCapability(CapabilityType type) {
        return (BlockCapabilityProvider<BE, T>) customCapabilities.get(type);
    }
    
    @SuppressWarnings("unchecked")
    public <T> ItemCapabilityProvider<T> getItemCapability(CapabilityType type) {
        return (ItemCapabilityProvider<T>) customCapabilities.get(type);
    }
    
    public boolean hasCustomCapability(CapabilityType type) {
        return customCapabilities.containsKey(type);
    }

    public static <BE extends GenericMachine> Builder<GenericMachineMenu, BE, MachineFourWayBlock<BE>, MachineBlockItem> generic(
            String name, BlockEntityType.BlockEntitySupplier<BE> blockEntityFactory) {
        return new Builder<GenericMachineMenu, BE, MachineFourWayBlock<BE>, MachineBlockItem>(name)
                .blockEntity(blockEntityFactory)
                .menu(GenericMachineMenu::new)
                .block(MachineFourWayBlock::new)
                .item(MachineBlockItem::new)
                .generic(true);
    }

    public static <M extends AbstractMachineMenu, BE extends Machine<M>> Builder<M, BE, MachineFourWayBlock<BE>, MachineBlockItem> standard(
            String name,
            MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menuFactory,
            BlockEntityType.BlockEntitySupplier<BE> blockEntityFactory) {
        return new Builder<M, BE, MachineFourWayBlock<BE>, MachineBlockItem>(name)
                .menu(menuFactory)
                .blockEntity(blockEntityFactory)
                .block(MachineFourWayBlock::new)
                .item(MachineBlockItem::new);
    }

    public static <M extends AbstractMachineMenu, BE extends Machine<M>> Builder<M, BE, MachineSixWayBlock<BE>, MachineBlockItem> sixWay(
            String name,
            MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menuFactory,
            BlockEntityType.BlockEntitySupplier<BE> blockEntityFactory) {
        return new Builder<M, BE, MachineSixWayBlock<BE>, MachineBlockItem>(name)
                .menu(menuFactory)
                .blockEntity(blockEntityFactory)
                .block(MachineSixWayBlock::new)
                .item(MachineBlockItem::new);
    }

    public static class Builder<M extends AbstractMachineMenu, BE extends Machine<M>, B extends BaseMachineBlock<BE>, I extends BlockItem> {
        private final String name;
        private MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menuFactory;
        private BlockEntityType.BlockEntitySupplier<BE> blockEntityFactory;
        private BiFunction<BlockBehaviour.Properties, BlockEntityType.BlockEntitySupplier<BE>, B> blockFactory;
        private BiFunction<Block, Item.Properties, I> itemFactory;
        private int dataSize = 2;
        private boolean hasUniqueTexture = false;
        private boolean hasCustomRender = false;
        private boolean isGeneric = false;
        private final Map<CapabilityType, Object> customCapabilities = new EnumMap<>(CapabilityType.class);
        private final List<ExtraCapabilityRegistrar<BE, I>> extraCapabilityRegistrars = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder<M, BE, B, I> menu(MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menuFactory) {
            this.menuFactory = menuFactory;
            return this;
        }

        public Builder<M, BE, B, I> blockEntity(BlockEntityType.BlockEntitySupplier<BE> blockEntityFactory) {
            this.blockEntityFactory = blockEntityFactory;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <NB extends BaseMachineBlock<BE>> Builder<M, BE, NB, I> block(BiFunction<BlockBehaviour.Properties, BlockEntityType.BlockEntitySupplier<BE>, NB> blockFactory) {
            Builder<M, BE, NB, I> newBuilder = (Builder<M, BE, NB, I>) this;
            newBuilder.blockFactory = blockFactory;
            return newBuilder;
        }

        @SuppressWarnings("unchecked")
        public <NI extends BlockItem> Builder<M, BE, B, NI> item(BiFunction<Block, Item.Properties, NI> itemFactory) {
            Builder<M, BE, B, NI> newBuilder = (Builder<M, BE, B, NI>) this;
            newBuilder.itemFactory = itemFactory;
            return newBuilder;
        }

        public Builder<M, BE, B, I> dataSize(int dataSize) {
            this.dataSize = dataSize;
            return this;
        }

        public Builder<M, BE, B, I> uniqueTexture() {
            this.hasUniqueTexture = true;
            return this;
        }

        public Builder<M, BE, B, I> customRender() {
            this.hasCustomRender = true;
            return this;
        }

        private Builder<M, BE, B, I> generic(boolean isGeneric) {
            this.isGeneric = isGeneric;
            return this;
        }
        
        // ==================== Block Capability Overrides ====================
        
        /**
         * Override the block's item handler capability.
         * @param provider Custom provider for IItemHandler
         */
        public <T> Builder<M, BE, B, I> blockItemCap(BlockCapabilityProvider<BE, T> provider) {
            customCapabilities.put(CapabilityType.BLOCK_ITEM, provider);
            return this;
        }
        
        /**
         * Override the block's fluid handler capability.
         * @param provider Custom provider for IFluidHandler
         */
        public <T> Builder<M, BE, B, I> blockFluidCap(BlockCapabilityProvider<BE, T> provider) {
            customCapabilities.put(CapabilityType.BLOCK_FLUID, provider);
            return this;
        }
        
        /**
         * Override the block's energy storage capability.
         * @param provider Custom provider for IEnergyStorage
         */
        public <T> Builder<M, BE, B, I> blockEnergyCap(BlockCapabilityProvider<BE, T> provider) {
            customCapabilities.put(CapabilityType.BLOCK_ENERGY, provider);
            return this;
        }
        
        // ==================== Item Capability Overrides ====================
        
        /**
         * Override the item's item handler capability.
         * @param provider Custom provider for IItemHandler
         */
        public <T> Builder<M, BE, B, I> itemItemCap(ItemCapabilityProvider<T> provider) {
            customCapabilities.put(CapabilityType.ITEM_ITEM, provider);
            return this;
        }
        
        /**
         * Override the item's fluid handler capability.
         * @param provider Custom provider for IFluidHandler
         */
        public <T> Builder<M, BE, B, I> itemFluidCap(ItemCapabilityProvider<T> provider) {
            customCapabilities.put(CapabilityType.ITEM_FLUID, provider);
            return this;
        }
        
        /**
         * Override the item's energy storage capability.
         * @param provider Custom provider for IEnergyStorage
         */
        public <T> Builder<M, BE, B, I> itemEnergyCap(ItemCapabilityProvider<T> provider) {
            customCapabilities.put(CapabilityType.ITEM_ENERGY, provider);
            return this;
        }
        
        /**
         * Disable a specific capability for this machine.
         * Pass null provider to skip registration entirely.
         */
        public Builder<M, BE, B, I> disableBlockCap(CapabilityType type) {
            customCapabilities.put(type, null);
            return this;
        }
        
        /**
         * Register an extra capability from another mod (e.g., Mekanism, Create).
         * This is useful for addon mods or compat layers.
         *
         * <p>Example usage for Mekanism gas handler:</p>
         * <pre>{@code
         * .extraCapability(ctx -> {
         *     if (ModList.get().isLoaded("mekanism")) {
         *         ctx.event().registerBlockEntity(
         *             mekanism.common.capabilities.Capabilities.GAS.block(),
         *             ctx.blockEntityType(),
         *             (be, dir) -> be.getGasHandler()
         *         );
         *     }
         * })
         * }</pre>
         *
         * @param registrar The capability registrar to add
         */
        public Builder<M, BE, B, I> extraCapability(ExtraCapabilityRegistrar<BE, I> registrar) {
            extraCapabilityRegistrars.add(registrar);
            return this;
        }

        public MachineRegistration<M, BE, B, I> build() {
            if (name == null || name.isEmpty()) throw new IllegalStateException("Machine name is required");
            if (menuFactory == null) throw new IllegalStateException("Menu factory is required");
            if (blockEntityFactory == null) throw new IllegalStateException("BlockEntity factory is required");
            if (blockFactory == null) throw new IllegalStateException("Block factory is required");
            if (itemFactory == null) throw new IllegalStateException("Item factory is required");
            return new MachineRegistration<>(this);
        }
    }
}

