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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

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

