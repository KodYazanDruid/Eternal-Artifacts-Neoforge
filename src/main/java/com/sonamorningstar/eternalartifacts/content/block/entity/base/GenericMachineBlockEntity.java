package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.machine.GenericScreenInfo;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class GenericMachineBlockEntity extends SidedTransferMachineBlockEntity<GenericMachineMenu> implements IButtonHolder {
    protected final Map<Integer, Consumer<Integer>> buttonConsumerMap = new HashMap<>();
    public GenericMachineBlockEntity(MachineDeferredHolder<?, ? ,? ,?> machineHolder, BlockPos pos, BlockState blockState) {
        super(machineHolder.getBlockEntity(), pos, blockState, (a, b, c, d) -> new GenericMachineMenu(machineHolder.getMenu(), a, b ,c ,d));
    }

    protected GenericScreenInfo screenInfo = new GenericScreenInfo(this);

    @Override
    public Map<Integer, Consumer<Integer>> buttonConsumerMap() {
        return buttonConsumerMap;
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        if (inventory != null) {
            if (inventory.getSlots() - outputSlots.size() > 0) performAutoInput(lvl, pos, inventory);
            if (!outputSlots.isEmpty()) performAutoOutput(lvl, pos, inventory, outputSlots.toArray(Integer[]::new));
        }
    }
}
