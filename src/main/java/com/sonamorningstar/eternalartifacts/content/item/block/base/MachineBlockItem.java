package com.sonamorningstar.eternalartifacts.content.item.block.base;

import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MachineBlockItem extends FluidHolderBlockItem {
    public MachineBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getCount() == 1 && Math.max(getChargeLevel(stack), getFluidLevel(stack)) > 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            tooltip.add(ModConstants.GUI.withSuffixTranslatable("energy").append(": ")
                    .append(String.valueOf(energy.getEnergyStored())).append(" / ").append(String.valueOf(energy.getMaxEnergyStored())).withStyle(ChatFormatting.DARK_RED)
            );
        }
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float charge = getChargeLevel(stack);
        float fluidAmount = getFluidLevel(stack);
        float max = Math.max(charge, fluidAmount);
        return (int) (max * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float charge = getChargeLevel(stack);
        float fluidAmount = getFluidLevel(stack);
        return charge >= fluidAmount ? 0x880808 : BlockHelper.getFluidTintColor(getFluid(stack));
    }

    private static float getChargeLevel(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energy != null ? energy.getEnergyStored() / (float) energy.getMaxEnergyStored() : 0;
    }

    private static float getFluidLevel(ItemStack stack) {
        IFluidHandlerItem tank = stack.getCapability(Capabilities.FluidHandler.ITEM);
        return tank != null ? tank.getFluidInTank(0).getAmount() / (float) tank.getTankCapacity(0) : 0;
    }

    @Override
    public void onFluidContentChange(ItemStack stack) {

    }
}
