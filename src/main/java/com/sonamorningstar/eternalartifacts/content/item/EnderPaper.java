package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.client.gui.widget.Warp;
import com.sonamorningstar.eternalartifacts.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EnderPaper extends Item {
    public EnderPaper(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof EnderPaper) {
            if(player.isDiscrete()) {
                CompoundTag tag = stack.getOrCreateTag();
                Warp warp = new Warp("Ender Paper Warp", level.dimension(), player.blockPosition());
                warp.writeToNBT(tag);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            } else {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("Warp")) {
                    Warp warp = Warp.readFromNBT(tag);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    warp.teleport();
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
                }
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Warp")) {
            Warp warp = Warp.readFromNBT(tag);
            String name = warp.getLabel();
            String dimension = TooltipHelper.prettyName(warp.getDimension().location().getPath());
            String pos = "X: " + warp.getPosition().getX() + ", Y: " + warp.getPosition().getY() + ", Z: " + warp.getPosition().getZ();
            tooltips.add(Component.literal("Name: " + name).withStyle(ChatFormatting.GREEN));
            tooltips.add(Component.literal("Dimension: " + dimension).withStyle(ChatFormatting.GREEN));
            tooltips.add(Component.literal("Position: " + pos).withStyle(ChatFormatting.GREEN));
        }
    }
}
