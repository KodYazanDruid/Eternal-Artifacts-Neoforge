package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class GenericMachineHolder<BE extends GenericMachine> extends MachineDeferredHolder<GenericMachineMenu, BE, MachineFourWayBlock<BE>, MachineBlockItem>{
    public GenericMachineHolder(DeferredHolder<MenuType<?>, MenuType<GenericMachineMenu>> menu, DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> blockEntity,
                                DeferredHolder<Block, MachineFourWayBlock<BE>> block, DeferredHolder<Item, MachineBlockItem> item,
                                boolean hasUniqueTexture, boolean isGeneric, boolean hasCustomRender) {
        super(menu, blockEntity, block, item, hasUniqueTexture, isGeneric, hasCustomRender);
    }
}
