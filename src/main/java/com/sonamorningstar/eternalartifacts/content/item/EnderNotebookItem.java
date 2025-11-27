package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.client.gui.widget.Warp;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.endernotebook.OpenItemStackScreenToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderNotebookItem extends Item {
    public EnderNotebookItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (other.getItem() instanceof EnderPaper) {
            CompoundTag tag = other.getTag();
            if (tag != null && tag.contains("Warp") && getWarpAmount(stack) < calculateMaxWarpAmount(stack, 8, 4)) {
                Warp warp = Warp.readFromNBT(tag);
                if (!player.getAbilities().instabuild) other.shrink(1);
                addWarp(stack, warp.getLabel(), warp.getDimension(), warp.getPosition());
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(player instanceof ServerPlayer serverPlayer) {
            Channel.sendToPlayer(new OpenItemStackScreenToClient(itemstack), serverPlayer);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    public static void addWarp(ItemStack stack, String name, ResourceKey<Level> dimension, BlockPos position) {
        ListTag listTag = stack.getTag() != null ? stack.getTag().getList("Warps", 10) : new ListTag();

        CompoundTag singleWarp = new CompoundTag();
        singleWarp.putString("Name", name);
        singleWarp.putString("Dimension", dimension.location().toString());
        singleWarp.putInt("X", position.getX());
        singleWarp.putInt("Y", position.getY());
        singleWarp.putInt("Z", position.getZ());
        listTag.add(singleWarp);

        CompoundTag tag = stack.getOrCreateTag();
        tag.put("Warps", listTag);
    }

    public static int calculateMaxWarpAmount(ItemStack stack, int warpsPerPage, int warpsPerEnchantmentLevel) {
        return warpsPerPage + (stack.getEnchantmentLevel(ModEnchantments.VOLUME.get()) * warpsPerEnchantmentLevel);
    }

    public static int getWarpAmount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        ListTag listTag = tag != null ? tag.getList("Warps", 10) : new ListTag();
        return listTag.size();
    }

}
