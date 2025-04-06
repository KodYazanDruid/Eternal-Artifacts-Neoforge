package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.item.base.EnergyRendererItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BatteryItem extends EnergyRendererItem {
    public BatteryItem(Properties pProperties) {
        super(pProperties);
    }

    public static final String KEY_CHARGE = "Charging";

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()){
            if(!player.level().isClientSide()) switchCharge(stack, player.level(), player.blockPosition());
            return true;
        }else {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }else{
            switchCharge(stack, level, player.blockPosition());
            return InteractionResultHolder.consume(stack);
        }
    }

    public void switchCharge(ItemStack stack, Level level, BlockPos pos) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean(KEY_CHARGE)) {
            tag.putBoolean(KEY_CHARGE, true);
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 1.0F);
        }
        else {
            stack.removeTagKey(KEY_CHARGE);
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 0.5F);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isCharging(stack) || super.isFoil(stack);
    }

    public static boolean isCharging(ItemStack stack) {
        boolean flag = stack.hasTag();
        if (flag) {
            CompoundTag tag = stack.getTag();
            flag = tag.getBoolean(KEY_CHARGE);
        }
        return flag;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 22;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return getMaxStackSize(stack) == 1;
    }
}
