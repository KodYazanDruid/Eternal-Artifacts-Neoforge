package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.util.CapabilityHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@RequiredArgsConstructor
public class MachineDeferredHolder<M extends AbstractMachineMenu, S extends AbstractMachineScreen<M>,  BE extends MachineBlockEntity<M>, MB extends BaseMachineBlock<BE>, BI extends BlockItem> {
    private final DeferredHolder<MenuType<?>, MenuType<M>> menu;
    private final DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> blockEntity;
    private final DeferredHolder<Block, MB> block;
    private final DeferredHolder<Item, BI> item;
    private final MenuScreens.ScreenConstructor<M, S> screenConstructor;
    @Getter
    private final boolean hasUniqueTexture;

    public DeferredHolder<MenuType<?>, MenuType<M>> getMenuHolder() {return this.menu;}
    public MenuType<M> getMenu() {return getMenuHolder().get();}
    public DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> getBlockEntityHolder() {return this.blockEntity;}
    public BlockEntityType<BE> getBlockEntity() {return getBlockEntityHolder().get();}
    public DeferredHolder<Block, MB> getBlockHolder() { return this.block;}
    public MB getBlock() {return getBlockHolder().get();}
    public DeferredHolder<Item, BI> getItemHolder() {return this.item;}
    public BI getItem() {return getItemHolder().get();}

    public String getBlockTranslationKey() {
        return getBlock().getDescriptionId();
    }

    public String getItemTranslationKey() {
        return getItem().getDescriptionId();
    }

    public void registerScreen(RegisterMenuScreensEvent event) {
        event.register(menu.get(), screenConstructor);
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, getBlockEntity(), (be, ctx) -> be instanceof SidedTransferMachineBlockEntity<?> sided ? CapabilityHelper.regSidedItemCaps(sided, sided.inventory, ctx, sided.outputSlots) : be.inventory);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, getBlockEntity(), (be, ctx) -> be instanceof SidedTransferMachineBlockEntity<?> sided ? CapabilityHelper.regSidedFluidCaps(sided, sided.tank, ctx) : be.tank);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, getBlockEntity(), (be, ctx) -> be.energy);
    }
}
