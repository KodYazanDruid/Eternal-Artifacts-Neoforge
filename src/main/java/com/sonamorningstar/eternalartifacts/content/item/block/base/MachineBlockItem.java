package com.sonamorningstar.eternalartifacts.content.item.block.base;

import com.sonamorningstar.eternalartifacts.content.block.DynamoBlock;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class MachineBlockItem extends FluidHolderBlockItem {
    private static final Set<Enchantment> ALLOWED_ENCHANTMENTS = Set.of(
        Enchantments.BLOCK_EFFICIENCY,
        Enchantments.UNBREAKING,
        Enchantments.BLAST_PROTECTION,
        Enchantments.FIRE_PROTECTION
    );
    
    @Override
    public void onFluidContentChange(ItemStack stack) {
    
    }
    
    public MachineBlockItem(Block block, Properties props) {
        super(block, props);
    }
    
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.is(ModBlocks.SOLAR_PANEL.asItem())) return false;
        
        if (ModMachines.INDUCTION_FURNACE.getItem() == stack.getItem() && enchantment == Enchantments.BLOCK_EFFICIENCY) {
            return false;
        }
        if (enchantment == Enchantments.UNBREAKING && getBlock() instanceof DynamoBlock<?>) {
			return false;
        }
        return ALLOWED_ENCHANTMENTS.contains(enchantment) || super.canApplyAtEnchantingTable(stack, enchantment);
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
}
