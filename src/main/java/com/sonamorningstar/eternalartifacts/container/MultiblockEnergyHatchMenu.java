package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMultiblockMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class MultiblockEnergyHatchMenu extends AbstractMultiblockMenu {
    public MultiblockEnergyHatchMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, ((AbstractMultiblockBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(2));
    }

    public MultiblockEnergyHatchMenu(int id, Inventory inv, AbstractMultiblockBlockEntity multiblockEntity, ContainerData data) {
        super(ModMenuTypes.MULTIBLOCK_ENERGY_HATCH.get(), id, inv, multiblockEntity, data);
    }
}
