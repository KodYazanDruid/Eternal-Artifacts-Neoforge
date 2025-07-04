package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import com.sonamorningstar.eternalartifacts.util.function.MenuConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class MachineDeferredRegister {
    private final DeferredRegister<MenuType<?>> menuRegister;
    private final DeferredRegister<BlockEntityType<?>> blockEntityRegister;
    private final DeferredRegister.Blocks blockRegister;
    private final DeferredRegister.Items itemRegister;
    private final List<MachineDeferredHolder<? extends AbstractMachineMenu, ? extends Machine<? extends AbstractMachineMenu>, ? extends BaseMachineBlock<? extends Machine<? extends AbstractMachineMenu>>, ? extends BlockItem>> machines = new ArrayList<>();
    private final List<MachineDeferredHolder<GenericMachineMenu, ? extends Machine<GenericMachineMenu>, ? extends BaseMachineBlock<? extends Machine<GenericMachineMenu>>, ? extends BlockItem>> genericMachines = new ArrayList<>();

    private final BlockBehaviour.Properties defaultProperties = BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL);

    public MachineDeferredRegister(String modid) {
        this.menuRegister = DeferredRegister.create(Registries.MENU, modid);
        this.blockEntityRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modid);
        this.blockRegister = DeferredRegister.createBlocks(modid);
        this.itemRegister = DeferredRegister.createItems(modid);
    }

    public void register(IEventBus bus) {
        this.menuRegister.register(bus);
        this.blockEntityRegister.register(bus);
        this.blockRegister.register(bus);
        this.itemRegister.register(bus);

    }
    
    public
    <M extends AbstractMachineMenu,  BE extends Machine<M>>
    MachineDeferredHolder<M, BE, BaseMachineBlock<BE>, MachineBlockItem> register(String name, MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menu, BlockEntityType.BlockEntitySupplier<BE> blockEntity) {
        return register(name, menu, 2, blockEntity, false);
    }
        
        public
    <M extends AbstractMachineMenu,  BE extends Machine<M>>
    MachineDeferredHolder<M, BE, BaseMachineBlock<BE>, MachineBlockItem> register(String name, MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menu, int dataSize, BlockEntityType.BlockEntitySupplier<BE> blockEntity) {
        return register(name, menu, dataSize, blockEntity, false);
    }

    public
    <M extends AbstractMachineMenu, BE extends Machine<M>>
    MachineDeferredHolder<M, BE, BaseMachineBlock<BE>, MachineBlockItem> register(String name, MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menu, int dataSize, BlockEntityType.BlockEntitySupplier<BE> blockEntity, boolean hasUniqueTexture) {
        return register(name, menu, dataSize, blockEntity, MachineFourWayBlock::new, MachineBlockItem::new, hasUniqueTexture, false);
    }

    public
    <BE extends GenericMachine>
    GenericMachineHolder<BE> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<BE> blockEntity, boolean hasUniqueTexture) {
        return registerGeneric(name, blockEntity, hasUniqueTexture, false);
    }

    public
    <BE extends GenericMachine>
    GenericMachineHolder<BE> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<BE> blockEntity, boolean hasUniqueTexture, boolean hasCustomRender) {
        ResourceLocation baseKey = new ResourceLocation(menuRegister.getNamespace(), name);
        DeferredHolder<MenuType<?>, MenuType<GenericMachineMenu>> menuType = DeferredHolder.create(Registries.MENU, baseKey);
        DeferredHolder<MenuType<?>, MenuType<GenericMachineMenu>> menuHolder = menuRegister.register(name, ()-> IMenuTypeExtension.create(((id, inv, data) -> new GenericMachineMenu(menuType.get(), id, inv, inv.player.level().getBlockEntity(data.readBlockPos()), new SimpleContainerData(2)))));

        DeferredHolder<Block, MachineFourWayBlock<BE>> blockHolder = blockRegister.register(name, () -> new MachineFourWayBlock<>(defaultProperties, blockEntity));
        DeferredHolder<Item, MachineBlockItem> itemHolder = itemRegister.register(name, ()-> new MachineBlockItem(blockHolder.get(), new Item.Properties()));

        DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> blockEntityHolder = blockEntityRegister.register(name, ()->
                BlockEntityType.Builder.of(blockEntity, blockHolder.get()).build(null));

        GenericMachineHolder<BE> holder = new GenericMachineHolder<>(menuHolder, blockEntityHolder, blockHolder, itemHolder, hasUniqueTexture, true, hasCustomRender);
        genericMachines.add(holder);
        machines.add(holder);
        return holder;
    }
    
    public
    <M extends AbstractMachineMenu, BE extends Machine<M>, MB extends BaseMachineBlock<BE>, BI extends MachineBlockItem>
    MachineDeferredHolder<M, BE, MB, BI> register(String name, MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menu, BlockEntityType.BlockEntitySupplier<BE> blockEntity, BiFunction<BlockBehaviour.Properties, BlockEntityType.BlockEntitySupplier<BE>, MB> block, BiFunction<Block, Item.Properties, BI> item, boolean hasUniqueTexture, boolean hasCustomRender) {
        return register(name, menu, 2, blockEntity, block, item, hasUniqueTexture, hasCustomRender);
    }
        
    public
    <M extends AbstractMachineMenu, BE extends Machine<M>, MB extends BaseMachineBlock<BE>, BI extends MachineBlockItem>
    MachineDeferredHolder<M, BE, MB, BI> register(String name, MenuConstructor<MenuType<?>, Integer, Inventory, BlockEntity, ContainerData, M> menu, int dataSize, BlockEntityType.BlockEntitySupplier<BE> blockEntity, BiFunction<BlockBehaviour.Properties, BlockEntityType.BlockEntitySupplier<BE>, MB> block, BiFunction<Block, Item.Properties, BI> item, boolean hasUniqueTexture, boolean hasCustomRender) {
        ResourceLocation baseKey = new ResourceLocation(menuRegister.getNamespace(), name);
        DeferredHolder<MenuType<?>, MenuType<M>> menuType = DeferredHolder.create(Registries.MENU, baseKey);
        DeferredHolder<MenuType<?>, MenuType<M>> menuHolder = menuRegister.register(name, ()-> IMenuTypeExtension.create(((id, inv, data) -> menu.apply(menuType.get(), id, inv, inv.player.level().getBlockEntity(data.readBlockPos()), new SimpleContainerData(dataSize)))));

        DeferredHolder<Block, MB> blockHolder = blockRegister.register(name, () -> block.apply(defaultProperties, blockEntity));
        DeferredHolder<Item, BI> itemHolder = itemRegister.register(name, ()-> item.apply(blockHolder.get(), new Item.Properties()));

        DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> blockEntityHolder = blockEntityRegister.register(name, ()->
                BlockEntityType.Builder.of(blockEntity, blockHolder.get()).build(null));

        MachineDeferredHolder<M, BE, MB, BI> holder = new MachineDeferredHolder<>(menuHolder, blockEntityHolder, blockHolder, itemHolder, hasUniqueTexture, false, hasCustomRender);
        machines.add(holder);
        return holder;
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        machines.forEach(holder -> holder.registerCapabilities(event));
    }

    public List<MachineDeferredHolder<? extends AbstractMachineMenu, ? extends Machine<? extends AbstractMachineMenu>, ? extends BaseMachineBlock<? extends Machine<? extends AbstractMachineMenu>>, ? extends BlockItem>> getMachines() {
        return Collections.unmodifiableList(this.machines);
    }
    public List<MachineDeferredHolder<GenericMachineMenu, ? extends Machine<GenericMachineMenu>, ? extends BaseMachineBlock<? extends Machine<GenericMachineMenu>>, ? extends BlockItem>> getGenericMachines() {
        return Collections.unmodifiableList(this.genericMachines);
    }

    public List<DeferredHolder<Block, ? extends Block>> getBlockHolders() {
        return machines.stream().map(MachineDeferredHolder::getBlockHolder).collect(Collectors.toUnmodifiableList());
    }
}
