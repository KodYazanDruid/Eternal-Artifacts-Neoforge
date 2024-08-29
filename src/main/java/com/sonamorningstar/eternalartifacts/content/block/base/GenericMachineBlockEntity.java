package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.api.machine.GenericScreenInfo;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Getter
public abstract class GenericMachineBlockEntity extends SidedTransferMachineBlockEntity<GenericMachineMenu> {
    public GenericMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, MenuType<?> menuType) {
        super(type, pos, blockState, (a, b, c, d) -> new GenericMachineMenu(menuType, a, b ,c ,d));
    }

    protected GenericScreenInfo screenInfo = new GenericScreenInfo(this);

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        if (inventory != null) {
            if (inventory.getSlots() - outputSlots.size() > 0) performAutoInput(lvl, pos, inventory);
            if (!outputSlots.isEmpty()) performAutoOutput(lvl, pos, inventory, outputSlots.toArray(Integer[]::new));
        }
    }
}
