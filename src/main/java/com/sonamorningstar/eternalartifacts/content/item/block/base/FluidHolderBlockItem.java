package com.sonamorningstar.eternalartifacts.content.item.block.base;

import com.sonamorningstar.eternalartifacts.client.render.ModItemStackBEWLR;
import com.sonamorningstar.eternalartifacts.content.fluid.PotionFluidType;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.TooltipHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public abstract class FluidHolderBlockItem extends BlockItem implements ICapabilityListener {
    public FluidHolderBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        IFluidHandlerItem fhi = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fhi != null) {
            if (fhi.getTanks() == 1){
                FluidStack tankFluid = fhi.getFluidInTank(0);
                int fluidAmount = tankFluid.getAmount();
                int tankCapacity = fhi.getTankCapacity(0);
                Component value = fluidAmount == tankCapacity ?
                        Component.literal(String.valueOf(fluidAmount)) :
                        Component.literal(String.valueOf(fluidAmount)).append(" / ").append(String.valueOf(tankCapacity));
                if (!tankFluid.isEmpty()) {
                    tooltip.add(getFluidName(stack, 0, false).append(": ").append(value)
                            .withColor(BlockHelper.getFluidTintColor(tankFluid))
                    );
                    if (tankFluid.getFluid().getFluidType() instanceof PotionFluidType) {
                        tooltip.addAll(TooltipHelper.getPotionTooltips(tankFluid, level));
                    }
                }
            } else {
                for (int i = 0; i < fhi.getTanks(); i++) {
                    FluidStack tankFluid = fhi.getFluidInTank(i);
                    Component fluidName = getFluidName(stack, i,false);
                    int fluidAmount = tankFluid.getAmount();
                    int tankCapacity = fhi.getTankCapacity(i);
                    Component value = fluidAmount == tankCapacity ?
                        Component.literal(String.valueOf(fluidAmount)) :
                        Component.literal(String.valueOf(fluidAmount)).append(" / ").append(String.valueOf(tankCapacity));
                    if (!tankFluid.isEmpty()) {
                        tooltip.add(Component.translatable(ModConstants.TOOLTIP.withSuffix("tank"), i + 1)
                                .append(": ").append(fluidName).append(" ").append(value)
                                .withColor(BlockHelper.getFluidTintColor(tankFluid))
                        );
                    }
                }
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        Component fluidName = getFluidName(stack, 0, true);
        if(!Objects.equals(fluidName, Component.empty())) return Component.translatable(getDescriptionId()+".filled", fluidName);
        else return super.getName(stack);
    }

    protected Fluid getFluid(ItemStack stack, int tank) {
        return getFluidStack(stack, tank).getFluid();
    }

    protected FluidStack getFluidStack(ItemStack stack, int tank) {
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem != null) return fluidHandlerItem.getFluidInTank(tank);
        return FluidStack.EMPTY;
    }
    
    protected FluidStack getMostAmountFluidStack(ItemStack stack) {
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem != null) {
            FluidStack mostAmountFluid = FluidStack.EMPTY;
            for (int i = 0; i < fluidHandlerItem.getTanks(); i++) {
                FluidStack tankFluid = fluidHandlerItem.getFluidInTank(i);
                if (tankFluid.getAmount() >= mostAmountFluid.getAmount()) {
                    mostAmountFluid = tankFluid;
                }
            }
            return mostAmountFluid;
        }
        return FluidStack.EMPTY;
    }

    protected MutableComponent getFluidName(ItemStack stack, int tank, boolean doColor) {
        Fluid fluid = getFluid(stack, tank);
        if(fluid.isSame(Fluids.EMPTY)) return Component.empty();
        else {
            FluidStack fluidStack = getFluidStack(stack, tank);
            MutableComponent fluidName = fluid.getFluidType().getDescription(fluidStack).copy();
            return doColor ? fluidName.withColor(BlockHelper.getFluidTintColor(fluidStack)) : fluidName;
        }
    }

    protected boolean isEmpty(ItemStack stack) {
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem != null) {
            for (int i = 0; i < fluidHandlerItem.getTanks(); i++) {
                if (!fluidHandlerItem.getFluidInTank(i).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler != null) {
            int index = IntStream.range(0, handler.getTanks())
                .boxed()
                .max(Comparator.comparingInt(i -> handler.getFluidInTank(i).getAmount()))
                .orElse(-1);
            if (index == -1) return 0;
            int amount = handler.getFluidInTank(index).getAmount();
            int capacity = handler.getTankCapacity(index);
            if (amount <= 0) return 0;
            return Math.max(1, (int) Math.round((amount / (double) capacity) * 13));
        }
        return 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getMostAmountFluidStack(stack).getAmount() > 0 && !(stack.getCount() > 1);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BlockHelper.getFluidTintColor(getMostAmountFluidStack(stack));
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

    protected static float getFluidLevel(ItemStack stack) {
        IFluidHandlerItem tank = stack.getCapability(Capabilities.FluidHandler.ITEM);
        return tank != null && tank.getTanks() > 0 ? ((float) tank.getFluidInTank(0).getAmount()) / tank.getTankCapacity(0) : 0;
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
