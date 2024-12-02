package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTiers;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.endernotebook.OpenItemStackScreenToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;

public class LightSaberItem extends SwordItem {
    public LightSaberItem() {
        super(ModTiers.CHLOROPHYTE, 5, -2.0F, new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(player instanceof ServerPlayer serverPlayer) {
            Channel.sendToPlayer(new OpenItemStackScreenToClient(itemstack), serverPlayer);
        }
        return super.use(level, player, hand);
    }

    public void changeColor(ItemStack stack, int color) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt("Color", color);
    }

    public void toggleGlint(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (!nbt.getBoolean("SuppressGlint")) nbt.putBoolean("SuppressGlint", true);
        else stack.removeTagKey("SuppressGlint");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().getBoolean("SuppressGlint")) return false;
        else return super.isFoil(stack);
    }
}
