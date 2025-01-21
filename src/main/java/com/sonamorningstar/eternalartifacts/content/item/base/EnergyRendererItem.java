package com.sonamorningstar.eternalartifacts.content.item.base;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyRendererItem extends Item {
    public EnergyRendererItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy instanceof ModItemEnergyStorage mies) {
            tooltipComponents.add(ModConstants.GUI.withSuffixTranslatable("stored_energy").append(": ")
                    .append(energy.getEnergyStored() + " / ").append(String.valueOf(energy.getMaxEnergyStored())).withStyle(ChatFormatting.YELLOW));
            tooltipComponents.add(ModConstants.GUI.withSuffixTranslatable("energy_transfer_rate").append(": ")
                    .append(String.valueOf(mies.getMaxTransfer())).withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            return energy.getEnergyStored() > 0;
        }
        return false;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float charge = getChargeLevel(stack);
        return (int) Math.round(charge * 13.0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x880808;
    }

    private static float getChargeLevel(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energy != null ? energy.getEnergyStored() / (float) energy.getMaxEnergyStored() : 0;
    }
}
