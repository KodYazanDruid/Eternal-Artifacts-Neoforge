package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.content.item.block.base.FluidHolderBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

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
