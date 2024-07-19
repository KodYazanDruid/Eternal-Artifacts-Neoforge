package com.sonamorningstar.eternalartifacts.client;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.IRetexturedBlockEntity;
import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class RetexturedColor
        implements BlockColor, ItemColor {

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if(level != null && pos != null && tintIndex == 0) {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof IRetexturedBlockEntity retexturedBlockEntity) {
                Block texture = retexturedBlockEntity.getTexture();
                if(texture != Blocks.AIR) {
                    int color = Minecraft.getInstance().getBlockColors().getColor(texture.defaultBlockState(), level, pos, tintIndex);
                    if(color != -1) return color;
                }
            }
        }
        return 0xFFFFFF;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(tintIndex == 0) {
            if(stack.getItem() instanceof RetexturedBlockItem) {
                Block texture = RetexturedBlockItem.getTexture(stack);
                if(texture != Blocks.AIR) {
                    int color = Minecraft.getInstance().getItemColors().getColor(texture.asItem().getDefaultInstance(), tintIndex);
                    if(color != 1) return color;
                }
            }
        }
        return 0xFFFFFF;
    }

}
