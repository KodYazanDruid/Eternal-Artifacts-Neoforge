package com.sonamorningstar.eternalartifacts.content.item;

import lombok.Getter;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

@Getter
public class ColoredShulkerShellItem extends Item {
    private final DyeColor color;
    public ColoredShulkerShellItem(Properties props, DyeColor color) {
        super(props);
        this.color = color;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        Level lvl = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        ItemStack stack = ctx.getItemInHand();
        InteractionHand hand = ctx.getHand();
        InteractionHand oppositeHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherHandStack = ctx.getPlayer().getItemInHand(oppositeHand);
        BlockState state = lvl.getBlockState(pos);
        byte result = handleTransform(lvl, pos, stack, otherHandStack, player, state);
        if (result == 1) return InteractionResult.sidedSuccess(lvl.isClientSide());
        if (result == 2) return InteractionResult.FAIL;
        return super.useOn(ctx);
    }

    private static byte handleTransform(Level lvl, BlockPos pos, ItemStack stack, ItemStack otherHandStack, Player player, BlockState state) {
        if (!stack.is(otherHandStack.getItem())) {
            if (stack.getCount() < 2) {
                ItemStack temp = stack;
                stack = otherHandStack;
                otherHandStack = temp;
            }
            if (stack.getCount() < 2) return (byte) 0;
        }
        if (!stack.is(otherHandStack.getItem()) && stack.getCount() < 2) return (byte) 0;
        if (stack.getItem() instanceof ColoredShulkerShellItem shellItem &&
                state.is(Tags.Blocks.CHESTS_WOODEN)) {
            return transform(lvl, pos, stack, otherHandStack, player, shellItem.getColor());
        }
        return (byte) 2;
    }

    //Making this separate method to support normal shulker shell transformation.
    public static byte transform(Level lvl, BlockPos pos, ItemStack stack, ItemStack otherHandStack, Player player, @Nullable DyeColor color) {
        IItemHandler chestStorage = lvl.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        NonNullList<ItemStack> allTheItems;
        if (chestStorage != null) {
            int shulkerSlots = 27;
            allTheItems = NonNullList.withSize(shulkerSlots, ItemStack.EMPTY);
            for (int i = 0; i < allTheItems.size(); i++) {
                allTheItems.set(i, chestStorage.getStackInSlot(i).copyAndClear());
            }
        } else allTheItems = NonNullList.create();
        BlockState shulkerState = ShulkerBoxBlock.getBlockByColor(color).defaultBlockState();
        lvl.setBlock(pos, shulkerState, 35);
        ShulkerBoxBlockEntity shulkerEntity = (ShulkerBoxBlockEntity) lvl.getBlockEntity(pos);
        if (shulkerEntity == null) return (byte) 0;
        for (int i = 0; i < allTheItems.size(); i++) {
            if (i >= shulkerEntity.getItems().size()) break;
            ItemStack itemStack = allTheItems.get(i);
            shulkerEntity.setItem(i, itemStack);
        }
        if (!player.getAbilities().instabuild){
            if (stack.is(otherHandStack.getItem())) {
                stack.shrink(1);
                otherHandStack.shrink(1);
            } else stack.shrink(2);
        }
        lvl.playSound(player, pos, shulkerState.getSoundType().getPlaceSound(), SoundSource.BLOCKS,1.0F, 1.0F);
        if (lvl instanceof ServerLevel sl) {
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, shulkerState),
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10,
                    0.5D, 0.5D, 0.5D, 0.0D);
        }
        lvl.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
        if (player instanceof ServerPlayer) CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, pos, stack);
        return (byte) 1;
    }
}
