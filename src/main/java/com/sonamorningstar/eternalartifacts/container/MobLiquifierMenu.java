package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MobLiquifierMenu extends AbstractMachineMenu{
    public MobLiquifierMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }
    public MobLiquifierMenu( int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MOB_LIQUIFIER.get(), pContainerId, inv, entity, data);

    }
}
