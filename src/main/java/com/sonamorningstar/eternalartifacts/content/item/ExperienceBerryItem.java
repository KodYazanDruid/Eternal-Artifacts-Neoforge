package com.sonamorningstar.eternalartifacts.content.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExperienceBerryItem extends Item {
    public ExperienceBerryItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        int consumed = player.isShiftKeyDown() ? stack.getCount() : 1;
        if(!player.getAbilities().instabuild) stack.shrink(consumed);
        player.giveExperiencePoints(5 * consumed);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS);
        return InteractionResultHolder.consume(stack);
    }

}
