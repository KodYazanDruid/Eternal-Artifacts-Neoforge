package com.sonamorningstar.eternalartifacts.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ToolBlockPlaceContext extends BlockPlaceContext {
    @Nullable
    private final BlockPos pos;
    
    public ToolBlockPlaceContext(UseOnContext context, ItemStack stack) {
        super(context.getLevel(), context.getPlayer(), context.getHand(), stack, context.getHitResult());
        this.pos = context.getClickedPos();
    }
    
    public ToolBlockPlaceContext(Player player, InteractionHand hand, ItemStack stack, BlockPos pos, BlockHitResult ray) {
        super(player, hand, stack, ray);
        this.pos = pos;
    }

    public ToolBlockPlaceContext(Player player, InteractionHand hand, ItemStack stack, BlockHitResult ray) {
        super(player, hand, stack, ray);
        this.pos = ray.getBlockPos();
    }

    @Override
    public BlockPos getClickedPos() {
        return pos != null ? pos : super.getClickedPos();
    }
}
