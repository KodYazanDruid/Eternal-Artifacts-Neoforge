package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import static net.minecraft.world.level.block.Blocks.CAULDRON;

public class PlasticCauldronBlock extends LayeredCauldronBlock {
    public PlasticCauldronBlock() {
        super(Biome.Precipitation.NONE, ModCauldronInteraction.PLASTIC, BlockBehaviour.Properties.ofFullCopy(CAULDRON));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return Items.CAULDRON.getDefaultInstance();
    }
}
