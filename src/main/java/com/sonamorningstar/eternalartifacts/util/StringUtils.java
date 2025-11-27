package com.sonamorningstar.eternalartifacts.util;

import com.google.common.collect.Lists;
import com.sonamorningstar.eternalartifacts.content.fluid.PotionFluidType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class StringUtils {
    public static String prettyName(String path) {
        String displayName = path.replace('_', ' ');
        String[] pathWords = displayName.split("\\s");
        StringBuilder prettyPath = new StringBuilder();
        for(String word : pathWords) prettyPath.append(Character.toTitleCase(word.charAt(0))).append(word.substring(1)).append(" ");
        return prettyPath.toString().trim();
    }
    
    public static String prettyNameNoBlanks(String path) {
        String displayName = path.replace('_', ' ');
        String[] pathWords = displayName.split("\\s");
        StringBuilder prettyPath = new StringBuilder();
        for(String word : pathWords) prettyPath.append(Character.toTitleCase(word.charAt(0))).append(word.substring(1)).append(" ");
        return prettyPath.toString().trim().replaceAll(" ", "");
    }
    
    public static String formatNumber(double value, int decimals) {
        String[] suffixes = {"", "K", "M", "B", "T"};
        int index = 0;
        
        while (value >= 1000 && index < suffixes.length - 1) {
            value /= 1000.0;
            index++;
        }
        
        // Floor yapmak için: (value * pow) → floor → pow ile geri böl
        double pow = Math.pow(10, decimals);
        value = Math.floor(value * pow) / pow;
        
        // Dinamik ondalık formatı
        StringBuilder decimalPattern = new StringBuilder("0");
        if (decimals > 0) {
            decimalPattern.append(".");
            decimalPattern.append("#".repeat(decimals));
        }
        
        DecimalFormat format = new DecimalFormat(decimalPattern.toString());
        
        return format.format(value) + suffixes[index];
    }
    
    public static String formatNumberAuto(double value, int maxLen) {
        String[] suffixes = {"", "K", "M", "B", "T"};
        int index = 0;
        
        while (value >= 1000 && index < suffixes.length - 1) {
            value /= 1000.0;
            index++;
        }
        
        String suffix = suffixes[index];
        
        int available = maxLen - suffix.length();
        if (available <= 0) {
            return suffix;
        }
        
        int intDigits = (int) Math.floor(Math.log10(value)) + 1;
        
        if (intDigits > available) {
            return ((long) Math.floor(value)) + suffix;
        }
        
        int decimals = available - intDigits;
        if (decimals < 0) decimals = 0;
        
        double pow = Math.pow(10, decimals);
        double v2 = Math.floor(value * pow) / pow;
        
        StringBuilder pattern = new StringBuilder("0");
        if (decimals > 0) {
            pattern.append(".");
            pattern.append("#".repeat(decimals));
        }
        
        DecimalFormat fmt = new DecimalFormat(pattern.toString());
        return fmt.format(v2) + suffix;
    }
    
    public static List<Component> getTooltipFromContainerFluid(FluidStack stack, @Nullable Level level, boolean isAdvanced) {
        List<Component> list = Lists.newArrayList();
        boolean isPotion = stack.getFluid().getFluidType() instanceof PotionFluidType;
        
        if (stack.hasTag() && stack.getTag().contains("EtarFluidStackName")) {
            String name = stack.getTag().getString("EtarFluidStackName");
            list.add(Component.empty().append(name)
                .withStyle(stack.getFluid().getFluidType().getRarity().getStyleModifier())
                .withStyle(ChatFormatting.ITALIC));
        } else {
            list.add(Component.empty().append(stack.getDisplayName()));
        }
        
        if (isPotion) list.addAll(getPotionTooltips(stack, level));
        
        if (isAdvanced) {
            list.add(Component.literal(BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString()).withStyle(ChatFormatting.DARK_GRAY));
            if (stack.hasTag()) {
                list.add(Component.translatable("item.nbt_tags", stack.getTag().getAllKeys().size()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        
        if (isPotion) appendModName(list, ModListUtils.getCreatorModId(BuiltInRegistries.POTION, PotionUtils.getPotion(stack.getTag())));
        else appendModName(list, ModListUtils.getFluidCreatorModId(stack));
        
        return list;
    }
    
    public static void appendModName(List<Component> tooltip, Optional<String> modNameOpt) {
        modNameOpt.ifPresent(name ->
            tooltip.add(Component.literal(name).withStyle(ChatFormatting.ITALIC)
                .withStyle(ChatFormatting.BLUE)));
    }
    
    public static List<Component> getPotionTooltips(FluidStack stack, @Nullable Level level) {
        return getPotionTooltips(stack.getTag(), level);
    }
    
    public static List<Component> getPotionTooltips(ItemStack stack, @Nullable Level level) {
        return getPotionTooltips(stack.getTag(), level);
    }
    
    public static List<Component> getPotionTooltips(CompoundTag tag, @Nullable Level level) {
        List<Component> tooltips = Lists.newArrayList();
        PotionUtils.addPotionTooltip(PotionUtils.getAllEffects(tag), tooltips, 1.0F, level == null ? 20.F : level.tickRateManager().tickrate());
        return tooltips;
    }
}
