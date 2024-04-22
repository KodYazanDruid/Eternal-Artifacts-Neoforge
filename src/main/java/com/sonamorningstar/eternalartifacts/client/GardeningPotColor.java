package com.sonamorningstar.eternalartifacts.client;

import com.sonamorningstar.eternalartifacts.content.block.GardeningPotBlock;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.content.block.entity.GardeningPotEntity;
import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import javax.annotation.Nullable;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class GardeningPotColor implements BlockColor, ItemColor {

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if(level != null && pos != null && tintIndex == 0) {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof GardeningPotEntity potEntity) {
                Block texture = potEntity.getTexture();
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
            if(stack.getItem() instanceof RetexturedBlockItem item && item.getBlock() instanceof GardeningPotBlock) {
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
