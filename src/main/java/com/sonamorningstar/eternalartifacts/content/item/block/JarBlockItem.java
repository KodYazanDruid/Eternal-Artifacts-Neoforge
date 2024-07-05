package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.content.item.block.base.FluidHolderBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class JarBlockItem extends FluidHolderBlockItem {

    public static final String KEY_OPEN = "Open";
    private JarDrinkEvent event = null;

    public JarBlockItem(Properties pProperties) {
        super(ModBlocks.JAR.get(), pProperties);
    }

    @Override
    public void onChange(ItemStack stack) {
        event = null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        //TODO: Fluid pick up logic.

        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if(hitResult.getType() == HitResult.Type.MISS && player.isCrouching()) {
            toggleLid(itemstack);
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        } else {
            event = new JarDrinkEvent(getFluidStack(itemstack), player);
            if(isOpen(itemstack)) {
                if(NeoForge.EVENT_BUS.post(event).isCanceled()) return super.use(level, player, hand);
                else if(event.getUseTime() > 0 && event.getDrinkingAmount() > 0) return ItemUtils.startUsingInstantly(level, player, hand);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);
        if (livingEntity instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }

        ItemStack drankJar = stack.copyWithCount(1);

        if (livingEntity instanceof Player player ) {
            if(event != null) event.getAfterDrink().accept(player, stack);
            if(!player.getAbilities().instabuild) stack.shrink(1);
        }

        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            IFluidHandlerItem fluidHandlerItem = drankJar.getCapability(Capabilities.FluidHandler.ITEM);
            if(fluidHandlerItem != null) {
                FluidStack fluidStack = fluidHandlerItem.getFluidInTank(0);
                int amount = fluidStack.getAmount();
                int drinkingAmount = event.getDrinkingAmount();
                if(drinkingAmount <= amount) fluidHandlerItem.drain(drinkingAmount, IFluidHandler.FluidAction.EXECUTE);
            }
            if(stack.isEmpty()) return drankJar;
            else addOrDrop(player, drankJar);
        }
        return stack;
    }

    private void addOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return this.event != null ? event.getUseTime() : super.getUseDuration(stack);
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return event != null ? event.getDrinkingSound() : super.getDrinkingSound();
    }

    @Override
    public SoundEvent getEatingSound() {
        return event != null ? event.getEatingSound() : super.getEatingSound();
    }

    private void toggleLid(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean(KEY_OPEN)) tag.putBoolean(KEY_OPEN, true);
        else stack.removeTagKey(KEY_OPEN);
    }

    private boolean isOpen(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) return tag.getBoolean(KEY_OPEN);
        return false;
    }
}
