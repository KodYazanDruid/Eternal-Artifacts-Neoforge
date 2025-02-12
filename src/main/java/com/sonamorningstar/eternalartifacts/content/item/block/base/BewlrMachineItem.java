package com.sonamorningstar.eternalartifacts.content.item.block.base;

import com.sonamorningstar.eternalartifacts.client.renderer.BEWLRProps;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class BewlrMachineItem extends MachineBlockItem {
    public BewlrMachineItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(BEWLRProps.INSTANCE);
    }
}
