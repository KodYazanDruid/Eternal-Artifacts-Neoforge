package com.sonamorningstar.eternalartifacts.content.item.block.base;

import com.sonamorningstar.eternalartifacts.api.machine.MachineEnchants;
import com.sonamorningstar.eternalartifacts.content.block.entity.SolarPanel;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MachineBlockItem extends FluidHolderBlockItem {
    
    @Override
    public void onFluidContentChange(ItemStack stack) {}
    
    public MachineBlockItem(Block block, Properties props) {super(block, props);}
    
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        var enchantmentMap = MachineEnchants.enchantMap;
        AtomicBoolean canApply = new AtomicBoolean(false);
        enchantmentMap.forEach((machine, enchs) -> {
            for (Block validBlock : machine.getValidBlocks()) {
                if (stack.getItem() instanceof BlockItem bi && bi.getBlock() == validBlock) {
                    canApply.set(enchs.contains(enchantment));
                }
            }
        });
        return canApply.get();
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }
    
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 22;
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
        if (energy != null && energy.getMaxEnergyStored() > 0) {
            int charge = energy.getEnergyStored();
            int maxCharge = energy.getMaxEnergyStored();
            Component chargeValue = charge == maxCharge ?
                    Component.literal(String.valueOf(charge)) :
                    Component.literal(String.valueOf(charge)).append(" / ").append(String.valueOf(maxCharge));
            tooltip.add(ModConstants.GUI.withSuffixTranslatable("energy").append(": ")
                    .append(chargeValue).withStyle(ChatFormatting.DARK_RED)
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
        return charge >= fluidAmount ? 0x880808 : super.getBarColor(stack);
    }

    protected static float getChargeLevel(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energy != null ? ((float) energy.getEnergyStored()) / energy.getMaxEnergyStored() : 0;
    }
    
    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        int oldEnergy;
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SolarPanel solarPanel) oldEnergy = solarPanel.energy.getEnergyStored();
        else return super.place(ctx);
        InteractionResult result = super.place(ctx);
        if (result.consumesAction()) {
             BlockEntity newBe = level.getBlockEntity(pos);
             if (newBe instanceof SolarPanel newSolarPanel) {
                 if (newSolarPanel.energy != null) {
                     newSolarPanel.energy.setEnergy(oldEnergy);
                 }
             }
             return result;
        }
        return InteractionResult.FAIL;
    }
}
