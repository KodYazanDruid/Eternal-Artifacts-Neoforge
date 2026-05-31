package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DynamoMenu extends AbstractMachineMenu {
    public DynamoMenu(MenuType<?> type, int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(type, pContainerId, inv, entity, data);
    }
}
