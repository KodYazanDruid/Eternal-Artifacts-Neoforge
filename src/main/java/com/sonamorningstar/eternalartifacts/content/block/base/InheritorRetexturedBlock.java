package com.sonamorningstar.eternalartifacts.content.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.jetbrains.annotations.Nullable;

public abstract class InheritorRetexturedBlock extends RetexturedBlock{
    public InheritorRetexturedBlock(Properties props) {
        super(props);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        Block texture = getTexture(level, pos);
        return texture.getSoundType(texture.defaultBlockState(), level, pos, entity);
    }
    
    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }
    
    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        //Block texture = getTexture(level, pos);
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        return lightManager.getLightAt(pos);
        //return texture.getLightEmission(texture.defaultBlockState(), level, pos);
    }
    
    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        Block texture = getTexture(level, pos);
        return texture.getExplosionResistance(texture.defaultBlockState(), level, pos, explosion);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        Block texture = getTexture(level, pos);
        return texture.getDestroyProgress(texture.defaultBlockState(), player, level, pos);
    }
    
    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        Block texture = getTexture(level, pos);
        return super.canHarvestBlock(texture.defaultBlockState(), level, pos, player);
    }
}
