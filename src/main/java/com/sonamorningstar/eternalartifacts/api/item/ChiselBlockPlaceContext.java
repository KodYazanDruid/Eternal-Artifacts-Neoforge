package com.sonamorningstar.eternalartifacts.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ChiselBlockPlaceContext extends BlockPlaceContext {
    @Nullable
    private final BlockPos pos;
    public ChiselBlockPlaceContext(Player player, InteractionHand hand, ItemStack stack, BlockPos pos, BlockHitResult ray) {
        super(player, hand, stack, ray);
        this.pos = pos;
    }

    public ChiselBlockPlaceContext(Player player, InteractionHand hand, ItemStack stack, BlockHitResult ray) {
        super(player, hand, stack, ray);
        this.pos = ray.getBlockPos();
    }

    @Override
    public BlockPos getClickedPos() {
        return pos != null ? pos : super.getClickedPos();
    }
}
