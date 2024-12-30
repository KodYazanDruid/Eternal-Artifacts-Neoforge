package com.sonamorningstar.eternalartifacts.content.item.block.base;

import com.sonamorningstar.eternalartifacts.client.renderer.ModItemStackBEWLR;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class FluidHolderBlockItem extends BlockItem implements ICapabilityListener {
    public FluidHolderBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Component fluidName = getFluidName(stack);
        IFluidHandlerItem fhi = stack.getCapability(Capabilities.FluidHandler.ITEM);
        FluidStack fs = getFluidStack(stack);
        if(!Objects.equals(fluidName, Component.empty()) && fhi != null)
            tooltip.add(fluidName.copy().append(" ").append(String.valueOf(fs.getAmount())).append(" / ").append(String.valueOf(fhi.getTankCapacity(0))));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public Component getName(ItemStack stack) {
        Component fluidName = getFluidName(stack);
        if(!Objects.equals(fluidName, Component.empty())) return Component.translatable(getDescriptionId()+".filled", fluidName);
        else return super.getName(stack);
    }

    protected Fluid getFluid(ItemStack stack) {
        return getFluidStack(stack).getFluid();
    }

    protected FluidStack getFluidStack(ItemStack stack) {
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem != null) return fluidHandlerItem.getFluidInTank(0);
        return FluidStack.EMPTY;
    }

    protected Component getFluidName(ItemStack stack) {
        Fluid fluid = getFluid(stack);
        if(fluid.isSame(Fluids.EMPTY)) return Component.empty();
        else {
            String descriptionId = fluid.getFluidType().getDescriptionId();
            return Component.translatable(descriptionId).withColor(BlockHelper.getFluidTintColor(fluid));
        }
    }

    protected boolean isEmpty(ItemStack stack) {
        return getFluidStack(stack).isEmpty();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem != null) {
            return (int) ((fluidHandlerItem.getFluidInTank(0).getAmount() / (float) fluidHandlerItem.getTankCapacity(0)) * 13);
        }
        return 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getFluidStack(stack).getAmount() > 0 && !(stack.getCount() > 1);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BlockHelper.getFluidTintColor(getFluid(stack));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ModItemStackBEWLR.INSTANCE.get();
            }
        });
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return stack.getCount() == 1 && FluidUtil.getFluidContained(stack).map((fs) -> !fs.isEmpty()).orElse(false);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return FluidUtil.getFluidHandler(itemStack.copy()).map((handler) -> {
            handler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            return handler.getContainer();
        }).orElseThrow(RuntimeException::new);
    }
}
