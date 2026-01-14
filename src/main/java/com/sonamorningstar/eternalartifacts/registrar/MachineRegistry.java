package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.capabilities.energy.MachineItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MachineItemFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.MachineItemItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import com.sonamorningstar.eternalartifacts.util.CapabilityHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simplified machine registry using builder pattern.
 * <p>Example usage:</p>
 * <pre>{@code
 * // Generic machine (uses GenericMachineMenu automatically)
 * MachineHolder<GenericMachineMenu, MyMachine, MachineFourWayBlock<MyMachine>, MachineBlockItem> MY_MACHINE =
 *     REGISTRY.register(MachineRegistration.generic("my_machine", MyMachine::new));
 *
 * // Standard machine with custom menu
 * MachineHolder<MyMenu, MyMachine, MachineFourWayBlock<MyMachine>, MachineBlockItem> MY_MACHINE =
 *     REGISTRY.register(MachineRegistration.standard("my_machine", MyMenu::new, MyMachine::new));
 *
 * // Six-way machine
 * MachineHolder<MyMenu, MyMachine, MachineSixWayBlock<MyMachine>, MachineBlockItem> MY_MACHINE =
 *     REGISTRY.register(MachineRegistration.sixWay("my_machine", MyMenu::new, MyMachine::new));
 *
 * // Fully customized
 * REGISTRY.register(MachineRegistration.standard("my_machine", MyMenu::new, MyMachine::new)
 *     .block(CustomBlock::new)
 *     .item(CustomItem::new)
 *     .dataSize(4)
 *     .uniqueTexture()
 *     .customRender());
 * }</pre>
 */
public class MachineRegistry {
    private final String modId;
    private final DeferredRegister<MenuType<?>> menuRegister;
    private final DeferredRegister<BlockEntityType<?>> blockEntityRegister;
    private final DeferredRegister.Blocks blockRegister;
    private final DeferredRegister.Items itemRegister;

    private final List<MachineHolder<?, ?, ?, ?>> allMachines = new ArrayList<>();
    private final List<MachineHolder<GenericMachineMenu, ? extends GenericMachine, ?, ?>> genericMachines = new ArrayList<>();

    private final BlockBehaviour.Properties defaultBlockProps = BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL);

    public MachineRegistry(String modId) {
        this.modId = modId;
        this.menuRegister = DeferredRegister.create(Registries.MENU, modId);
        this.blockEntityRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);
        this.blockRegister = DeferredRegister.createBlocks(modId);
        this.itemRegister = DeferredRegister.createItems(modId);
    }

    public void register(IEventBus bus) {
        menuRegister.register(bus);
        blockEntityRegister.register(bus);
        blockRegister.register(bus);
        itemRegister.register(bus);
    }

    /**
     * Registers a machine using the provided registration configuration.
     */
    public <M extends AbstractMachineMenu, BE extends Machine<M>, B extends BaseMachineBlock<BE>, I extends BlockItem>
    MachineHolder<M, BE, B, I> register(MachineRegistration<M, BE, B, I> registration) {
        String name = registration.getName();
        ResourceLocation baseKey = new ResourceLocation(modId, name);

        // Menu registration
        DeferredHolder<MenuType<?>, MenuType<M>> menuType = DeferredHolder.create(Registries.MENU, baseKey);
        DeferredHolder<MenuType<?>, MenuType<M>> menuHolder = menuRegister.register(name, () ->
                IMenuTypeExtension.create((id, inv, data) ->
                        registration.getMenuFactory().apply(
                                menuType.get(),
                                id,
                                inv,
                                inv.player.level().getBlockEntity(data.readBlockPos()),
                                new SimpleContainerData(registration.getDataSize())
                        )
                )
        );

        // Block registration
        DeferredHolder<Block, B> blockHolder = blockRegister.register(name, () ->
                registration.getBlockFactory().apply(defaultBlockProps, registration.getBlockEntityFactory())
        );

        // Item registration
        DeferredHolder<Item, I> itemHolder = itemRegister.register(name, () ->
                registration.getItemFactory().apply(blockHolder.get(), new Item.Properties())
        );

        // BlockEntity registration
        DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> blockEntityHolder = blockEntityRegister.register(name, () ->
                BlockEntityType.Builder.of(registration.getBlockEntityFactory(), blockHolder.get()).build(null)
        );

        // Create holder
        MachineHolder<M, BE, B, I> holder = new MachineHolder<>(
                menuHolder, blockEntityHolder, blockHolder, itemHolder,
                registration.isHasUniqueTexture(),
                registration.isGeneric(),
                registration.isHasCustomRender()
        );

        allMachines.add(holder);
        
        if (registration.isGeneric()) {
            @SuppressWarnings("unchecked")
            MachineHolder<GenericMachineMenu, ? extends GenericMachine, ?, ?> genericHolder =
                (MachineHolder<GenericMachineMenu, ? extends GenericMachine, ?, ?>) holder;
            genericMachines.add(genericHolder);
        }

        return holder;
    }

    /**
     * Shorthand for registering a generic machine.
     */
    public <BE extends GenericMachine> MachineHolder<GenericMachineMenu, BE, MachineFourWayBlock<BE>, MachineBlockItem>
    registerGeneric(String name, BlockEntityType.BlockEntitySupplier<BE> factory) {
        return register(MachineRegistration.generic(name, factory).build());
    }

    /**
     * Shorthand for registering a generic machine with custom render.
     */
    public <BE extends GenericMachine> MachineHolder<GenericMachineMenu, BE, MachineFourWayBlock<BE>, MachineBlockItem>
    registerGeneric(String name, BlockEntityType.BlockEntitySupplier<BE> factory, boolean hasCustomRender) {
        var builder = MachineRegistration.generic(name, factory);
        if (hasCustomRender) builder.customRender();
        return register(builder.build());
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineHolder<?, ?, ?, ?> holder : allMachines) {
            registerMachineCapabilities(event, holder);
        }
    }

    private <M extends AbstractMachineMenu, BE extends Machine<M>, B extends BaseMachineBlock<BE>, I extends BlockItem>
    void registerMachineCapabilities(RegisterCapabilitiesEvent event, MachineHolder<M, BE, B, I> holder) {
        // Block capabilities
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, holder.getBlockEntity(), (be, ctx) -> {
            if (be instanceof SidedTransferMachine<?> sided) {
                return sided.inventory != null ? CapabilityHelper.regSidedItemCaps(sided, sided.inventory, ctx, sided.outputSlots) : null;
            }
            return be.inventory;
        });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, holder.getBlockEntity(), (be, ctx) -> {
            if (be instanceof SidedTransferMachine<?> sided) {
                return sided.tank != null ? CapabilityHelper.regSidedFluidCaps(sided, sided.tank, ctx) : null;
            }
            return be.tank;
        });

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, holder.getBlockEntity(), (be, ctx) ->
            be.energy != null ? be.energy : null
        );

        // Item capabilities
        event.registerItem(Capabilities.ItemHandler.ITEM, (st, ctx) -> new MachineItemItemStorage(st), holder.getItem());
        event.registerItem(Capabilities.FluidHandler.ITEM, (st, ctx) -> new MachineItemFluidStorage(st), holder.getItem());
        event.registerItem(Capabilities.EnergyStorage.ITEM, (st, ctx) -> new MachineItemEnergyStorage(st), holder.getItem());
    }

    public List<MachineHolder<?, ?, ?, ?>> getMachines() {
        return Collections.unmodifiableList(allMachines);
    }

    public List<MachineHolder<GenericMachineMenu, ? extends GenericMachine, ?, ?>> getGenericMachines() {
        return Collections.unmodifiableList(genericMachines);
    }

    public List<DeferredHolder<Block, ? extends Block>> getBlockHolders() {
        return allMachines.stream()
                .map(MachineHolder::blockHolder)
                .collect(Collectors.toUnmodifiableList());
    }
}
