package com.sonamorningstar.eternalartifacts.util;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class TooltipHelper {
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
    
    public static List<Component> getTooltipFromContainerFluid(FluidStack stack, boolean isAdvanced) {
        List<Component> list = Lists.newArrayList();
        
        if (stack.hasTag() && stack.getTag().contains("EtarFluidStackName")) {
            String name = stack.getTag().getString("EtarFluidStackName");
            list.add(Component.empty().append(name)
                .withStyle(stack.getFluid().getFluidType().getRarity().getStyleModifier())
                .withStyle(ChatFormatting.ITALIC));
        } else {
            list.add(Component.empty().append(stack.getDisplayName()));
        }
        
        if (isAdvanced) {
            list.add(Component.literal(BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString()).withStyle(ChatFormatting.DARK_GRAY));
            if (stack.hasTag()) {
                list.add(Component.translatable("item.nbt_tags", stack.getTag().getAllKeys().size()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        
        ModListUtils.getFluidCreatorModId(stack).ifPresent(name -> list.add(Component.literal(name).withStyle(ChatFormatting.ITALIC)
			.withStyle(ChatFormatting.BLUE)));
        return list;
    }
}
