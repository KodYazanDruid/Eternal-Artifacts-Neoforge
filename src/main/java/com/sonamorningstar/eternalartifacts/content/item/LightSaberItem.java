package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTiers;
import net.minecraft.nbt.CompoundTag;
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
        changeColor(player.getItemInHand(hand), 0xff2a17bc);
        return super.use(level, player, hand);
    }

    public void changeColor(ItemStack stack, int color) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt("Color", color);
    }
}
