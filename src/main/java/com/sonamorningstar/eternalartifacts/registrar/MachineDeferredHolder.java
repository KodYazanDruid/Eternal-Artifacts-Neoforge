package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.capabilities.energy.MachineItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MachineItemFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.MachineItemItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import com.sonamorningstar.eternalartifacts.util.CapabilityHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@RequiredArgsConstructor
public class
MachineDeferredHolder<M extends AbstractMachineMenu, BE extends MachineBlockEntity<M>, MB extends BaseMachineBlock<BE>, BI extends MachineBlockItem> implements ItemLike {
    private final DeferredHolder<MenuType<?>, MenuType<M>> menu;
    private final DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> blockEntity;
    private final DeferredHolder<Block, MB> block;
    private final DeferredHolder<Item, BI> item;
    @Getter
    private final boolean hasUniqueTexture;
    @Getter
    private final boolean isGeneric;
    @Getter
    private final boolean hasCustomRender;

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

    public ResourceLocation getBlockId() {
        return getBlockHolder().getId();
    }

    //TODO: Complete item capabilities.
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, getBlockEntity(), (be, ctx) -> {
            if (be instanceof SidedTransferMachineBlockEntity<?> sided) {
                if(sided.inventory != null) return CapabilityHelper.regSidedItemCaps(sided, sided.inventory, ctx, sided.outputSlots);
                else return null;
            } else return be.inventory;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, getBlockEntity(), (be, ctx) -> {
            if (be instanceof SidedTransferMachineBlockEntity<?> sided) {
                if(sided.tank != null) return CapabilityHelper.regSidedFluidCaps(sided, sided.tank, ctx);
                else return null;
            } else return be.tank;
        });
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, getBlockEntity(), (be, ctx) -> be.energy != null ? be.energy : null);

        event.registerItem(Capabilities.ItemHandler.ITEM, (st, ctx)-> new MachineItemItemStorage(st), getItem());
        event.registerItem(Capabilities.FluidHandler.ITEM, (st, ctx) -> new MachineItemFluidStorage(st), getItem());
        event.registerItem(Capabilities.EnergyStorage.ITEM, (st, ctx) -> new MachineItemEnergyStorage(st), getItem());
    }

    @Override
    public Item asItem() {
        return getItem();
    }
}
