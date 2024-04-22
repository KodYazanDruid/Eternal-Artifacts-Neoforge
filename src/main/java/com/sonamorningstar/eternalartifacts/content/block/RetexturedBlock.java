package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.IRetexturedBlockEntity;
import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.util.BlockEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public abstract class RetexturedBlock extends Block implements EntityBlock {
    public RetexturedBlock(Properties props) {
        super(props);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        updateTextureBlock(level, pos, stack);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return getPickBlock(level, pos, state);
    }

    public static void updateTextureBlock(Level level, BlockPos pos, ItemStack stack) {
        if(stack.hasTag()) BlockEntityHelper.get(IRetexturedBlockEntity.class, level, pos).ifPresent(entity -> entity.updateTexture(RetexturedBlockItem.getTextureName(stack)));
    }

    private ItemStack getPickBlock(LevelReader level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        ItemStack stack = new ItemStack(block);
        BlockEntityHelper.get(IRetexturedBlockEntity.class, level, pos).ifPresent(entity -> RetexturedBlockItem.setTexture(stack, entity.getTextureName()));
        return stack;
    }

}
