package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.content.item.block.base.FluidHolderBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class DrumBlockItem extends FluidHolderBlockItem {
    public DrumBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void onFluidContentChange(ItemStack stack) {}
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {}

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(stack));
    }
}
