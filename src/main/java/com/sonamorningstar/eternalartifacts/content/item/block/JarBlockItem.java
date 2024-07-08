package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.content.item.block.base.FluidHolderBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class JarBlockItem extends FluidHolderBlockItem {

    public static final String KEY_OPEN = "Open";
    private JarDrinkEvent event = null;

    public JarBlockItem(Properties pProperties) {
        super(ModBlocks.JAR.get(), pProperties);
    }

    @Override
    public void onFluidContentChange(ItemStack stack) {
        event = null;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        ItemStack itemstack = ctx.getItemInHand();
        return player == null || isAbleToPick(itemstack) ? InteractionResult.PASS : super.useOn(ctx);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, isAbleToPick(itemstack) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if(hitResult.getType() == HitResult.Type.MISS && player.isCrouching()) {
            toggleLid(itemstack);
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        } else if(hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = hitResult.getBlockPos();
            Direction direction = hitResult.getDirection();
            if(player.mayInteract(level, blockpos)) {
                BlockState pickupFluid;
                if(isEmpty(itemstack)) {
                    pickupFluid = level.getBlockState(blockpos);
                    if(pickupFluid.getBlock() instanceof BucketPickup bucketPickup) {
                        player.awardStat(Stats.ITEM_USED.get(this));
                        level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);
                        FluidActionResult result = FluidUtil.tryPickUpFluid(itemstack.copy(), player, level, blockpos, direction);
                        if(result.isSuccess()) {
                            ItemStack filledJar = result.result;
                            int stackCount = itemstack.getCount();
                            if(!player.getAbilities().instabuild){
                                if (stackCount == 1) player.setItemInHand(hand, filledJar);
                                else {
                                    itemstack.shrink(1);
                                    addOrDrop(player, filledJar);
                                }
                            }
                            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                        }
                    }
                }
            }
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
            if(event != null) {
                event.getAfterDrink().accept(player, stack);
                if(event.getAfterDrinkSound() != null) {
                    level.playSound(null,
                            player.getX(), player.getY(), player.getZ(),
                            event.getAfterDrinkSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
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

    private boolean isAbleToPick(ItemStack itemstack) {
        return isOpen(itemstack) && isEmpty(itemstack);
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
