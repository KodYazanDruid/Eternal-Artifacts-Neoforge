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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public class ColoredShulkerShellItem extends Item {
    private final DyeColor color;
    public ColoredShulkerShellItem(Properties props, DyeColor color) {
        super(props);
        this.color = color;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        Level lvl = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        InteractionHand hand = ctx.getHand();
        InteractionHand oppositeHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherHandStack = ctx.getPlayer().getItemInHand(oppositeHand);
        BlockState state = lvl.getBlockState(pos);
        byte result = handleTransform(lvl, pos, stack, otherHandStack, player, state);
        if (result == 1) return InteractionResult.sidedSuccess(lvl.isClientSide());
        if (result == 2) return InteractionResult.FAIL;
        return super.useOn(ctx);
    }

    public static byte handleTransform(Level lvl, BlockPos pos, ItemStack stack, ItemStack otherHandStack, Player player, BlockState state) {
        if (!stack.is(otherHandStack.getItem())) {
            if (stack.getCount() < 2) {
                ItemStack temp = stack;
                stack = otherHandStack;
                otherHandStack = temp;
            }
            if (stack.getCount() < 2) return (byte) 0;
        }
        if (isShulkerShell(stack) && state.is(Blocks.CHEST)) {
            return transform(lvl, pos, stack, otherHandStack, player, getColor(stack)) ? (byte) 1 : (byte) 0;
        }
        return (byte) 2;
    }

    public static boolean transform(Level lvl, BlockPos pos, ItemStack stack, ItemStack otherHandStack, Player player, @Nullable DyeColor color) {
        if (!stack.is(otherHandStack.getItem()) && stack.getCount() < 2) return false;
        BlockState shulkerState = ShulkerBoxBlock.getBlockByColor(color).defaultBlockState();
        if (!transferItems(lvl, pos, shulkerState)) return false;
        if (!player.getAbilities().instabuild ){
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
        return true;
    }

    private static boolean transferItems(Level lvl, BlockPos pos, BlockState shulkerState) {
        BlockEntity be = lvl.getBlockEntity(pos);
        if (!(be instanceof ChestBlockEntity chestEntity)) return false;
        NonNullList<ItemStack> allTheItems;
        int shulkerSlots = 27;
        allTheItems = NonNullList.withSize(shulkerSlots, ItemStack.EMPTY);
        for (int i = 0; i < allTheItems.size(); i++) {
            allTheItems.set(i, chestEntity.getItem(i).copyAndClear());
        }
        lvl.setBlock(pos, shulkerState, 35);
        ShulkerBoxBlockEntity shulkerEntity = (ShulkerBoxBlockEntity) lvl.getBlockEntity(pos);
        if (shulkerEntity == null) return false;
        for (int i = 0; i < allTheItems.size(); i++) {
            if (i >= shulkerEntity.getItems().size()) break;
            ItemStack itemStack = allTheItems.get(i);
            shulkerEntity.setItem(i, itemStack);
        }
        return true;
    }

    private static boolean isShulkerShell(ItemStack stack) {
        return stack.getItem() instanceof ColoredShulkerShellItem || stack.is(Items.SHULKER_SHELL);
    }

    @Nullable
    public static DyeColor getColor(ItemStack stack) {
        if (stack.getItem() instanceof ColoredShulkerShellItem) {
            return ((ColoredShulkerShellItem) stack.getItem()).color;
        }
        return null;
    }
}
